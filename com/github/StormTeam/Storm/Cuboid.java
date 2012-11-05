package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to speed up large block changes in an area
 * <p/>
 * Credits to desht for most of this: this is a version trimmed and fitted to Storm.
 */

public class Cuboid {

    public final String worldName;
    public final int x1, y1, z1;
    public final int x2, y2, z2;
    private final Object mutex = new Object();

    public Cuboid(Location l1, Location l2) {
        this(l1.getWorld(), Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()), Math.min(l1.getBlockZ(), l2.getBlockZ()),
                Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()), Math.max(l1.getBlockZ(), l2.getBlockZ()));
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("Can't find world " + worldName);
        }
        return world;
    }


    public int getSizeX() {
        return (x2 - x1) + 1;
    }

    public int getSizeY() {
        return (y2 - y1) + 1;
    }

    public int getSizeZ() {
        return (z2 - z1) + 1;
    }

    public int getLowerX() {
        return x1;
    }

    public int getLowerY() {
        return y1;
    }

    public int getLowerZ() {
        return z1;
    }

    public int getUpperX() {
        return x2;
    }

    public int getUpperY() {
        return y2;
    }

    public int getUpperZ() {
        return z2;
    }

    public Cuboid expand(BlockFace dir, int amount) {
        switch (dir) {
            case NORTH:
                return new Cuboid(getWorld(), x1 - amount, y1, z1, x2, y2, z2);
            case SOUTH:
                return new Cuboid(getWorld(), x1, y1, z1, x2 + amount, y2, z2);
            case EAST:
                return new Cuboid(getWorld(), x1, y1, z1 - amount, x2, y2, z2);
            case WEST:
                return new Cuboid(getWorld(), x1, y1, z1, x2, y2, z2 + amount);
            case DOWN:
                return new Cuboid(getWorld(), x1, y1 - amount, z1, x2, y2, z2);
            case UP:
                return new Cuboid(getWorld(), x1, y1, z1, x2, y2 + amount, z2);
            default:
                throw new IllegalArgumentException("invalid direction " + dir);
        }
    }

    public boolean contains(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public boolean contains(Block b) {
        return contains(b.getLocation());
    }

    public boolean contains(Location l) {
        if (l.getWorld() != getWorld()) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public List<Chunk> getChunks() {
        List<Chunk> res = new ArrayList<Chunk>();

        World world = getWorld();
        int x1 = getLowerX() & ~0xF;
        int x2 = getUpperX() & ~0xF;
        int z1 = getLowerZ() & ~0xF;
        int z2 = getUpperZ() & ~0xF;
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                synchronized (mutex) {
                    res.add(world.getChunkAt(x >> 4, z >> 4));
                }
            }
        }
        Verbose.log("Chunks: " + res);
        return res;
    }


    public void syncSetBlockFastDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    syncSetBlockFast(b, id);
            }
        }, delay);
    }

    public void syncSetBlockFast(final Block b, final int id) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                setBlockFast(b, id);
            }
        }, 0L);
    }

    public void setBlockFast(Block b, int typeId) {
        setBlockFast(b, typeId, (byte) 0);
    }

    public void setBlockFast(Block b, int typeId, byte data) {
        ((CraftChunk) b.getChunk()).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
    }

    public void sendClientChanges() {
        int threshold = ((Bukkit.getServer().getViewDistance() << 4) + 32) ^ 2;
        List<ChunkCoordIntPair> pairs = new ArrayList<ChunkCoordIntPair>();
        for (Chunk c : getChunks()) {
            pairs.add(new ChunkCoordIntPair(c.getX(), c.getZ()));
        }
        int centerX = getLowerX() + getSizeX() / 2, centerZ = getLowerZ() + getSizeZ() / 2;
        for (Player player : getWorld().getPlayers()) {
            int px = player.getLocation().getBlockX(), pz = player.getLocation().getBlockZ();
            if ((px - centerX) * (px - centerX) + (pz - centerZ) * (pz - centerZ) < threshold) {
                queueChunks(((CraftPlayer) player).getHandle(), pairs);
            }
        }
    }

    private void queueChunks(EntityPlayer ep, List<ChunkCoordIntPair> pairs) {
        Set<ChunkCoordIntPair> queued = new HashSet<ChunkCoordIntPair>();
        for (Object o : ep.chunkCoordIntPairQueue) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (ChunkCoordIntPair pair : pairs) {
            if (!queued.contains(pair)) {
                ep.chunkCoordIntPairQueue.add(pair);
            }
        }
    }
}

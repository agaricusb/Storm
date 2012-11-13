package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet56MapChunkBulk;
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
    private Set<ChunkCoordIntPair> chunkCache = new HashSet();
    int threshold = 0, centerX = 0, centerZ = 0;

    public Cuboid(Location l1, Location l2) {
        this(l1.getWorld(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
        threshold = (int) Math.pow(((Bukkit.getServer().getViewDistance() << 4) + 32), 2);
        centerX = getLowerX() + getSizeX() >> 1;
        centerZ = getLowerZ() + getSizeZ() >> 1;
        loadChunks();
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
        }
        return null;
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

    public boolean contains(Chunk chunk) {
        return chunkCache.contains(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
    }

    public void loadChunks() {
        World world = getWorld();
        for (int x = (getLowerX() & ~0xF); x <= (getUpperX() & ~0xF); x += 16) {
            for (int z = (getLowerZ() & ~0xF); z <= (getUpperZ() & ~0xF); z += 16) {
                synchronized (world) {
                    int xShifted = x >> 4, zShifted = z >> 4;
                    if (!world.isChunkLoaded(xShifted, zShifted))
                        world.loadChunk(xShifted, zShifted);
                    Chunk q = world.getChunkAt(xShifted, zShifted);
                    chunkCache.add(new ChunkCoordIntPair(q.getX(), q.getZ()));
                }
            }
        }
    }

    public void setBlockFastDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    setBlockFast(b, id);
            }
        }, delay);
    }

    public void setBlockFast(Block b, int typeId) {
        setBlockFast(b, typeId, (byte) 0);
    }

    public void setBlockFast(Block b, int typeId, byte data) {
        ((CraftChunk) b.getChunk()).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data); //%16
    }

    public void sendClientChanges() {
        for (Player player : getWorld().getPlayers())
            if (Math.pow((player.getLocation().getBlockX() - centerX), 2) + Math.pow((player.getLocation().getBlockZ() - centerZ), 2) < threshold)
                queueChunks(((CraftPlayer) player).getHandle(), chunkCache);
    }

    private void queueChunks(EntityPlayer ep, Set<ChunkCoordIntPair> pairs) {
        synchronized (getWorld()) {
            List<net.minecraft.server.Chunk> nc = new ArrayList<net.minecraft.server.Chunk>();
            for (ChunkCoordIntPair par : pairs)
                nc.add(((CraftChunk) getWorld().getChunkAt(par.x, par.z)).getHandle());
            synchronized (ep.netServerHandler) {
                ep.netServerHandler.sendPacket(new Packet56MapChunkBulk(nc));
            }
        }
    }
}
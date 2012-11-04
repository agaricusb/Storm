package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cuboid {

    public final String worldName;
    public final int x1, y1, z1;
    public final int x2, y2, z2;

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

    public Location getCenter() {
        return new Location(getWorld(), getLowerX() + (getUpperX() - getLowerX()) / 2,
                getLowerY() + (getUpperY() - getLowerY()) / 2,
                getLowerZ() + (getUpperZ() - getLowerZ()) / 2);
    }

    public List<Block> getCorners() {
        List<Block> res = new ArrayList<Block>(8);
        World world = getWorld();
        res.add(world.getBlockAt(x1, y1, z1));
        res.add(world.getBlockAt(x1, y1, z2));
        res.add(world.getBlockAt(x1, y2, z1));
        res.add(world.getBlockAt(x1, y2, z2));
        res.add(world.getBlockAt(x2, y1, z1));
        res.add(world.getBlockAt(x2, y1, z2));
        res.add(world.getBlockAt(x2, y2, z1));
        res.add(world.getBlockAt(x2, y2, z2));
        return res;

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
        int x1 = getLowerX() & ~0xf;
        int x2 = getUpperX() & ~0xf;
        int z1 = getLowerZ() & ~0xf;
        int z2 = getUpperZ() & ~0xf;
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                res.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }
        return res;
    }

    public void initLighting() {
        for (Chunk c : getChunks()) {
            ((CraftChunk) c).getHandle().initLighting();
        }
    }

    public void sendClientChanges() {
        int threshold = (Bukkit.getServer().getViewDistance() << 4) + 32;
        threshold = threshold * threshold;

        List<ChunkCoordIntPair> pairs = new ArrayList<ChunkCoordIntPair>();
        for (Chunk c : getChunks()) {
            pairs.add(new ChunkCoordIntPair(c.getX(), c.getZ()));
        }
        int centerX = getLowerX() + getSizeX() / 2,
                centerZ = getLowerZ() + getSizeZ() / 2;
        for (Player player : getWorld().getPlayers()) {
            int px = player.getLocation().getBlockX();
            int pz = player.getLocation().getBlockZ();
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
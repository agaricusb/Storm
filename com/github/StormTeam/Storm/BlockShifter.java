package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BlockShifter {

    public static HashMap<String, Set<ChunkCoordIntPair>> qued = new HashMap<String, Set<ChunkCoordIntPair>>();
    public static int bt = 15;
    private static Object mutex = new Object();

    public static void syncSetBlock(final Block b, final int id) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                b.setTypeId(id);
            }
        }, 0L);
    }

    public static void syncSetBlockDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    b.setTypeId(id);
            }
        }, delay);
    }

    public static void syncSetBlockFast(final Block b, final int id) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                setBlockFast(b, id);
            }
        }, 0L);
    }

    public static void syncSetBlockFastDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    syncSetBlockFast(b, id);
            }
        }, delay);
    }

    public static void setBlockFast(Block b, int typeId) {
        setBlockFast(b, typeId, (byte) 0);
    }

    public static void setBlockFast(Block b, int typeId, byte data) {
        synchronized (mutex) {
            Chunk c = b.getChunk();
            net.minecraft.server.Chunk chunk = ((CraftChunk) c).getHandle();
            chunk.a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
            ChunkCoordIntPair toQue = new ChunkCoordIntPair(c.getX(), c.getZ());
            String name = c.getWorld().getName();
            if (qued.containsKey(name) && !qued.get(name).contains(toQue)) {
                qued.get(name).add(toQue);
                if (qued.get(name).size() >= bt)
                    sendClientChanges();
            } else
                qued.put(name, StormUtil.asSet(toQue));

        }
    }

    public static void sendClientChanges() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            queueChunks(((CraftPlayer) player).getHandle(), qued.get(player.getWorld().getName()));
        }
    }

    public static void queueChunks(EntityPlayer ep, Set<ChunkCoordIntPair> pairs) {
        Set<ChunkCoordIntPair> queued = new HashSet<ChunkCoordIntPair>();
        for (Object o : ep.chunkCoordIntPairQueue) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (ChunkCoordIntPair pair : pairs) {
            if (!queued.contains(pair)) {
                ep.chunkCoordIntPairQueue.add(pair);
            }
        }
        qued = new HashMap<String, Set<ChunkCoordIntPair>>(); //Clear it
    }
}

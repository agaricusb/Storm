package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockShifter {

    private static final Set<Chunk> modifiedChunks = new HashSet<Chunk>();

    public static boolean setBlockFast(Block b, int typeId) {
        Chunk in = b.getChunk();
        synchronized (modifiedChunks) {
            modifiedChunks.add(in);
            return ((CraftChunk) in).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId);
        }
    }

    public static boolean setBlockFast(Block b, int typeId, byte data) {
        Chunk in = b.getChunk();
        synchronized (modifiedChunks) {
            modifiedChunks.add(in);
            return ((CraftChunk) in).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
        }
    }

    public static void updateClient(World world) {
        List<ChunkCoordIntPair> pairs = new ArrayList<ChunkCoordIntPair>();

        synchronized (modifiedChunks) {
            for (Chunk cun : modifiedChunks) {
                pairs.add(new ChunkCoordIntPair(cun.getX(), cun.getZ()));
            }
            modifiedChunks = new HashSet<Chunk>();
        }

        for (Player player : world.getPlayers()) {
            queueChunks(((CraftPlayer) player).getHandle(), pairs);
        }
    }

    private static void queueChunks(EntityPlayer ep, List<ChunkCoordIntPair> pairs) {
        Set<ChunkCoordIntPair> queued = new HashSet<ChunkCoordIntPair>();
        for (Object o : ep.chunkCoordIntPairQueue) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (ChunkCoordIntPair pair : pairs) {
            ep.chunkCoordIntPairQueue.add(pair);
        }
    }
}

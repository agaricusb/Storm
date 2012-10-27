package com.github.StormTeam.Storm;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BlockShifter {

    private static final Set<Chunk> modifiedChunks = new HashSet<Chunk>();

    public static void setBlockFast(Block b, int typeId) {
        //    try {
        //   Chunk in = b.getChunk();
        //   synchronized (modifiedChunks) {
        //     modifiedChunks.add(in);
        //     ((CraftChunk) in).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId);
        //   }
        // } catch (Exception e) {
        syncSetBlock(b, typeId);
        //}
    }

    public static void setBlockFast(Block b, int typeId, byte data) {
        //    try {
        //   Chunk in = b.getChunk();
        //   synchronized (modifiedChunks) {
        //        modifiedChunks.add(in);
        //       ((CraftChunk) in).getHandle().a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
        //    }
        // } catch (Exception e) {
        syncSetBlock(b, typeId);
        // }
    }

    public static void updateClient(World world) {
        //   try {
        // List<ChunkCoordIntPair> pairs = new ArrayList<ChunkCoordIntPair>();
        //  List<Player> players;

        //  synchronized (modifiedChunks) {
        //     for (Chunk cun : modifiedChunks) {
        //         pairs.add(new ChunkCoordIntPair(cun.getX(), cun.getZ()));
        //      }
        //      modifiedChunks.clear();
        //      players = new ArrayList<Player>(world.getPlayers());
        //   }

        //  for (Player player : players) {
        //     queueChunks(((CraftPlayer) player).getHandle(), pairs);
        //  }
        //  } catch (Exception ignored) {
        //Ah well...
        //    }
    }

    private static void queueChunks(EntityPlayer ep, List<ChunkCoordIntPair> pairs) {
        Set<ChunkCoordIntPair> queued = new HashSet<ChunkCoordIntPair>();
        for (Object o : ep.chunkCoordIntPairQueue) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (Object o : pairs) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (ChunkCoordIntPair pair : queued) {
            ep.chunkCoordIntPairQueue.add(pair);
        }
    }

    public static void syncSetBlock(final Block b, final int id) {
        Future<Boolean> callBlockChange = Bukkit.getScheduler().callSyncMethod(Storm.instance,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return b.setTypeId(id);
                    }
                }
        );
        try {
            callBlockChange.get();
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

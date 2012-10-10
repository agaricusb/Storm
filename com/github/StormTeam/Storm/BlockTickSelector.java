package com.github.StormTeam.Storm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.WorldServer;
import net.minecraft.server.Chunk;

public class BlockTickSelector {

    private WorldServer world;
    private Method a, recheckGaps;
    private int chan;
    private final Random rand = new Random();

    public BlockTickSelector(World world, int selChance)
            throws NoSuchMethodException,
            SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {

        this.world = ((CraftWorld) world).getHandle();

        this.recheckGaps = Chunk.class.getDeclaredMethod(Storm.version == 1.3 ? "k" : "o"); //If 1.3.X, method is named "k", else "o".
        this.recheckGaps.setAccessible(true); //Is private by default
        this.a = net.minecraft.server.World.class.getDeclaredMethod("a", int.class, int.class, Chunk.class);
        this.a.setAccessible(true);

    }

    public ArrayList<ChunkCoordIntPair> getRandomTickedChunks() throws InvocationTargetException, IllegalAccessException {

        ArrayList<ChunkCoordIntPair> doTick = new ArrayList<ChunkCoordIntPair>();

        if (world.players.isEmpty()) {
            return doTick;
        }

        List<org.bukkit.Chunk> loadedChunks = Arrays.asList(world.getWorld().getLoadedChunks());

        for (Object ob : world.players) {
            EntityHuman entityhuman = (EntityHuman) ob;
            int eX = (int) Math.floor(entityhuman.locX / 16),
                    eZ = (int) Math.floor(entityhuman.locZ / 16);
            byte range = 7;
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    if (loadedChunks.contains(world.getChunkAt(x + eX, z + eZ).bukkitChunk)) {
                        doTick.add(new ChunkCoordIntPair(x + eX, z + eZ));
                    }
                }
            }
        }
        return doTick;
    }

    public ArrayList<Block> getRandomTickedBlocks()
            throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        ArrayList<Block> doTick = new ArrayList<Block>();
        ArrayList<ChunkCoordIntPair> ticked = getRandomTickedChunks();

        if (ticked.isEmpty()) {
            return doTick;
        }

        for (ChunkCoordIntPair pair : ticked) {
            int xOffset = pair.x << 4, zOffset = pair.z << 4;
            Chunk chunk = world.getChunkAt(pair.x, pair.z);
            
            a.invoke(world, xOffset, zOffset, chunk); //Make sure chunk is loaded (?)
            recheckGaps.invoke(chunk);
            if (rand.nextInt(100) <= chan) {
                int x = rand.nextInt(15), z = rand.nextInt(15);
                doTick.add(world.getWorld().getBlockAt(x + xOffset, world.g(x + xOffset, z + zOffset), z + zOffset));
            }
        }
        return doTick;
    }
}

package com.github.StormTeam.Storm;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.WorldServer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockTickSelector {

    private WorldServer world;
    private Method a, recheckGaps;
    private int chan;

    /**
     * Constructs a BlockTickSelector utility object.
     *
     * @param world     The world to construct it for
     * @param selChance The chance of a block being returned from list of ticked blocks
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */

    public BlockTickSelector(World world, int selChance)
            throws NoSuchMethodException,
            SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {

        this.world = ((CraftWorld) world).getHandle();

        this.recheckGaps = Chunk.class.getDeclaredMethod(Storm.version == 1.3 ? "k" : "o"); //If 1.3.X, method is named "k", else "o".
        this.recheckGaps.setAccessible(true); //Is private by default
        this.a = net.minecraft.server.World.class.getDeclaredMethod("a", int.class, int.class, Chunk.class);
        this.a.setAccessible(true);
        this.chan = selChance;

    }

    /**
     * Gets a ArrayList of chunks coerced with player locations.
     *
     * @return A list of ticked chunks
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    ArrayList<ChunkCoordIntPair> getRandomTickedChunks() throws InvocationTargetException, IllegalAccessException {

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

    /**
     * Gets an ArrayList of ticked blocks. Very fast.
     *
     * @return An ArrayList of ticked blocks
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
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
            //I am not sure what the a method does, but it seems to be needed for this to return anything.
            a.invoke(world, xOffset, zOffset, chunk);
            recheckGaps.invoke(chunk);
            if (Storm.random.nextInt(100) <= chan) {
                int x = Storm.random.nextInt(15), z = Storm.random.nextInt(15);
                doTick.add(world.getWorld().getBlockAt(x + xOffset, world.g(x + xOffset, z + zOffset), z + zOffset));
            }
        }
        return doTick;
    }
}

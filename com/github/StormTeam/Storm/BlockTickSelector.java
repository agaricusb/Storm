package com.github.StormTeam.Storm;

import net.minecraft.server.v1_4_6.Chunk;
import net.minecraft.server.v1_4_6.ChunkCoordIntPair;
import net.minecraft.server.v1_4_6.EntityHuman;
import net.minecraft.server.v1_4_6.WorldServer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An object for returning large lists of pseudorandom blocks at high speeds.
 *
 * @author Icyene
 */

public class BlockTickSelector {

    private final WorldServer world;
    private final World bWorld;
    private int chan;
    private int radius;

    /**
     * Creates a BlockTickSelector for given world with given chance.
     *
     * @param world     The world to create this tick selector for
     * @param selChance The chance for a block from the ticked list to be returned
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */

    public BlockTickSelector(World world, int selChance, int rad)
            throws NoSuchMethodException,
            SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {

        this.world = ((CraftWorld) world).getHandle();
        this.bWorld = world;
        this.radius = rad;
        this.chan = selChance;
    }

    public BlockTickSelector(World world, int selChance)
            throws NoSuchMethodException,
            SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        this(world, selChance, 16);
    }

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
     * Fetches a random list of ticked blocks.
     *
     * @return A random list of ticked blocks
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
            chunk.bukkitChunk.load();
            if (Storm.random.nextInt(100) <= chan) {
                int x = Storm.random.nextInt(radius), z = Storm.random.nextInt(radius);
                doTick.add(world.getWorld().getBlockAt(x + xOffset, bWorld.getHighestBlockYAt(x + xOffset, z + zOffset), z + zOffset));
            }
        }
        return doTick;
    }
}

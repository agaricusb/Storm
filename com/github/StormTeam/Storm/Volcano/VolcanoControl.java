/*
 * This file is part of Storm.
 *
 * Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Storm.  If not, see
 * <http://www.gnu.org/licenses/>.
 */


package com.github.StormTeam.Storm.Volcano;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class VolcanoControl implements Listener {
    static public final Set<VolcanoMaker> volcanoes = new HashSet<VolcanoMaker>();
    static final HashMap<String, List<Integer>> volcanoBlockCache = new HashMap<String, List<Integer>>();
    static final Object mutex = new Object();
    static final Set<Integer> cannotBlow = new HashSet() {{
        add(Material.BEDROCK.getId());
        add(Material.OBSIDIAN.getId());
    }};

    @EventHandler
    public void coolLava(BlockFromToEvent e) {
        if (e.isCancelled())
            return;
        Block from = e.getBlock();
        for (VolcanoMaker volcano : volcanoes) {
            if ((from.getTypeId() & 0xfe) == 0xa && volcano.active && volcano.area.contains(from) && volcano.ownsBlock(from)) { //Checks if the block is lava and if a volcano owns it
                solidify(volcano, from, randomVolcanoBlock(from.getWorld()));
                if (!volcano.ownsBlock(e.getToBlock())) ;
                volcano.grow(false); //Reached boundaries
            }
        }
    }

    @EventHandler
    public void unloadChunk(ChunkUnloadEvent e) {
        Chunk c = e.getChunk();
        for (VolcanoMaker volcano : volcanoes) {
            if (volcano.area.contains(c)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void unloadWorld(WorldUnloadEvent e) {
        if (e.isCancelled())
            return;
        for (VolcanoMaker vulk : volcanoes) {
            if (vulk.world.equals(e.getWorld())) {
                vulk.active = false;
                vulk.dumpVolcanoes();
            }
        }
    }

    // @EventHandler
    public void explode(EntityExplodeEvent e) {
        if (e.isCancelled())
            return;
        Entity ex = e.getEntity();
        if (ex == null)
            return;

        int id = ex.getEntityId();
        boolean boom = false;
        VolcanoMaker of = null;
        for (VolcanoMaker vulc : volcanoes) {
            if (vulc.explosionIDs.contains(id)) {
                boom = true;
                of = vulc;
                break;
            }
        }

        if (!boom)
            return;

        ArrayList<Block> toProtect = new ArrayList<Block>(), toRemove = new ArrayList<Block>();

        for (Block block : e.blockList())
            if (Storm.util.isBlockProtected(block))
                toProtect.add(block);
            else
                toRemove.add(block);

        for (Block block : toProtect)
            e.blockList().remove(block);
        for (Block block : toRemove) {
            if (!block.getType().equals(Material.FIRE)) {
                e.blockList().remove(block);
                block.setTypeId(0);
            }
        }

        if (ex.isDead())
            of.explosionIDs.remove(id);
    }

    static void solidify(VolcanoMaker vulc, Block lava, int idTo) {
        int data;
        if ((data = lava.getData()) != 0x9)
            vulc.area.setBlockFastDelayed(lava, idTo, ((data & 0x8) == 0x8 ? 1 : 4 - data / 2) * 20 * 2);
    }

    static List<Integer> getVolcanoBlock(String world) {
        if (!volcanoBlockCache.containsKey(world))
            volcanoBlockCache.put(world, new ArrayList() {
                {
                    for (int i = 0; i < 100; ++i)
                        add(Material.STONE.getId());
                }
            });
        return volcanoBlockCache.get(world);
    }

    static int randomVolcanoBlock(String world) {
        List<Integer> choices = getVolcanoBlock(world);
        return choices.get(Storm.random.nextInt(choices.size()));
    }

    static int randomVolcanoBlock(World world) {
        return randomVolcanoBlock(world.getName());
    }

    public static void save(final File dump) {
        synchronized (mutex) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Storm.instance, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!dump.exists())
                            dump.createNewFile();
                        String contents = "";
                        for (VolcanoMaker vulc : volcanoes) {
                            contents = contents + vulc.serialize();
                        }
                        BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                        writer.write(contents);
                        writer.close();
                    } catch (Exception e) {
                        ErrorLogger.generateErrorLog(e);
                    }
                }
            }
            );

        }
    }

    public static void load(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();
        String contents = Files.toString(file, Charset.defaultCharset());
        if (!StringUtils.isEmpty(contents))
            for (String vulc : Arrays.asList(contents.split("\n"))) {
                VolcanoMaker maker = new VolcanoMaker();
                maker.deserialize(vulc);
                maker.spawn();
                volcanoes.add(maker);
            }
    }
}

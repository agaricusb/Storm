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

import com.github.StormTeam.Storm.BlockShifter;
import com.github.StormTeam.Storm.Storm;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class VolcanoControl implements Listener {
    static public Set<VolcanoMaker> volcanoes = new HashSet<VolcanoMaker>();
    static HashMap<String, List<Integer>> volcanoBlockCache = new HashMap<String, List<Integer>>();
    static Object mutex = new Object();
    static HashMap<World, List<Chunk>> anchoredChunks = new HashMap();

    @EventHandler
    public void coolLava(BlockFromToEvent e) {
        Block from = e.getBlock();
        for (VolcanoMaker volcano : volcanoes) {
            if ((from.getTypeId() & 0xfe) == 0xa && volcano.active && volcano.ownsBlock(from)) //Checks if the block is lava and if a volcano owns it
                solidify(from, randomVolcanoBlock(from.getWorld()));
        }
    }

    @EventHandler
    public void unloadEvent(ChunkUnloadEvent e) {
        if (anchoredChunks.containsKey(e.getWorld())) {
            if (anchoredChunks.get(e.getWorld()).contains(e.getChunk()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void unloadWorld(WorldUnloadEvent e) {
        for (VolcanoMaker vulk : volcanoes) {
            if (vulk.world.equals(e.getWorld())) {
                vulk.active = false;
                vulk.dumpVolcanoes();
            }
        }
    }

    static List<Integer> getVolcanoBlock(String world) {
        if (!volcanoBlockCache.containsKey(world))
            volcanoBlockCache.put(world, Storm.wConfigs.get(world).Volcano_Composition);
        return volcanoBlockCache.get(world);
    }

    static int randomVolcanoBlock(String world) {
        List<Integer> choices = getVolcanoBlock(world);
        return choices.get(Storm.random.nextInt(choices.size()));
    }

    static int randomVolcanoBlock(World world) {
        return randomVolcanoBlock(world.getName());
    }

    public static void save(File dump) throws IOException {
        synchronized (mutex) {
            if (!dump.exists())
                dump.createNewFile();
            String contents = "";
            for (VolcanoMaker vulc : volcanoes) {
                contents = contents + vulc.serialize();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
            writer.write(contents);
            writer.close();
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

    static void solidify(Block lava, int idTo) {
        int data;
        if ((data = lava.getData()) != 0x9)
            BlockShifter.syncSetBlockDelayed(lava, idTo, ((data & 0x8) == 0x8 ? 1 : 4 - data / 2) * 20 * 2);
        else
            return;
        if (anchoredChunks.containsKey(lava.getWorld()))
            anchoredChunks.get(lava.getWorld()).add(lava.getChunk());
    }
}

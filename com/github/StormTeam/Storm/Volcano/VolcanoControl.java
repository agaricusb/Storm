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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Semaphore;

public class VolcanoControl implements Listener {
    static public Set<VolcanoMaker> volcanoes = new HashSet<VolcanoMaker>();
    static HashMap<String, List<Integer>> volcanoBlockCache = new HashMap<String, List<Integer>>();

    public VolcanoControl() {
        System.out.println("Registered!");
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
        if (!dump.exists())
            dump.createNewFile();
        String contents = "";
        for (VolcanoMaker vulc : volcanoes) {
            contents = contents + vulc.serialize();
        }
        System.out.println("Serialized to: " + contents);
        BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
        writer.write(contents);
        writer.close();
    }

    public static void load(File file) throws IOException, ClassNotFoundException, InterruptedException {
        Semaphore mutex = new Semaphore(1);
        mutex.acquire();
        if (!file.exists())
            file.createNewFile();
        String contents = Files.toString(file, Charset.defaultCharset());
        if (StringUtils.isEmpty(contents)) return;
        List<String> sers = Arrays.asList(contents.split("\n"));
        for (String vulc : sers) {
            VolcanoMaker maker = new VolcanoMaker();
            maker.deserialize(vulc);
            maker.spawn();
            volcanoes.add(maker);
        }
        mutex.release();
    }

    static void solidify(Block lava, int idTo) {
        int data;
        if ((data = lava.getData()) != 0x9)
            BlockShifter.syncSetBlockDelayed(lava, idTo, ((data & 0x8) == 0x8 ? 1 : 4 - data / 2) * 20 * 2);
    }

    @EventHandler
    public void coolLava(BlockFromToEvent e) {
        Block from = e.getBlock();
        int id = from.getTypeId();
        for (VolcanoMaker volcano : volcanoes) {
            if (volcano.ownsBlock(from) && (id == 10 || id == 11))
                solidify(from, randomVolcanoBlock(from.getWorld()));
        }
    }
}

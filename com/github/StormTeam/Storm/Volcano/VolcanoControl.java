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

import com.github.StormTeam.Storm.Storm;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolcanoControl implements Listener {
    static public Set<Volcano> volcanos = new HashSet();
    static HashMap<String, List<Integer>> volcanoBlockCache = new HashMap<String, List<Integer>>();

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

    @EventHandler
    public void coolLava(BlockFromToEvent e) {
        Block from = e.getBlock();
        int id = from.getTypeId();
        for (Volcano volcano : VolcanoControl.volcanos)
            if (volcano.ownsBlock(from) && (id == 10 || id == 11))
                new LavaSolidifier(from, randomVolcanoBlock(from.getWorld()));
    }
}

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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VolcanoControl implements Listener {
    static public List<Volcano> volcanos = new ArrayList<Volcano>();
    static HashMap<String, List<Integer>> volcanoBlockCache = new HashMap<String, List<Integer>>();

    static List<Integer> getVolcanoBlock(String world) {
        if (!volcanoBlockCache.containsKey(world)) {
            volcanoBlockCache.put(world, Storm.wConfigs.get(world).Volcano_Composition);
        }
        return volcanoBlockCache.get(world);
    }

    static Material randomVolcanoBlock(String world) {
        List<Integer> choices = getVolcanoBlock(world);
        return Material.getMaterial(choices.get(Storm.random.nextInt(choices.size())));
    }

    static Material randomVolcanoBlock(World world) {
        return randomVolcanoBlock(world.getName());
    }

    void newSolidifier(Block lava) {
        new LavaSolidifier(lava, randomVolcanoBlock(lava.getWorld()).getId());
    }

    @EventHandler
    public void OnBlockFlow(BlockFromToEvent e) {
        Block to = e.getToBlock();
        Block from = e.getBlock();
        World world = from.getWorld();
        for (Volcano volcano : VolcanoControl.volcanos) {
            if (volcano.ownsBlock(from)) {
                if (from.getTypeId() == 10 || from.getTypeId() == 11) {

                    Block growth = from.getRelative(BlockFace.UP);

                    Location center = volcano.getCenter();
                    int vx = center.getBlockX();
                    int vy = center.getBlockY();
                    int vz = center.getBlockZ();

                    Location grow = growth.getLocation();
                    int bx = grow.getBlockX();
                    int by = grow.getBlockY();
                    int bz = grow.getBlockZ();

                    // int calc = (int) Math.abs(Math.sqrt((Math.abs(vx - bx) ^ 2 + Math.abs(vz - bz) ^ 2) / 2 + Math.abs(vy - by)) * 2);

                    // calc = calc == 0 ? 1 : calc;

                    // if (Storm.random.nextInt(calc) != 1)
                    // return;

                    newSolidifier(from);

                    //BlockShifter.syncSetBlockDelayed(from.getRelative(BlockFace.UP), Material.LAVA.getId(), Storm.random.nextInt(80) + 40);
                }
            }
        }
    }
}

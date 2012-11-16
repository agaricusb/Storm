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

package com.github.StormTeam.Storm.Earthquake.Tasks;

import com.github.StormTeam.Storm.Cuboid;
import com.github.StormTeam.Storm.Math.Cracker;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Verbose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;

public class RuptureTask implements Runnable {

    private Location location;
    private int length;
    private final int width;
    private final int depth;
    private int id = -1;
    private int layerIndex = 0;
    private Cracker cracker;
    private Cuboid area;
    private World world;

    public RuptureTask(Cuboid area, Location location, int length, int width, int depth) {

        this.location = location;
        this.length = length;
        this.width = width;
        this.depth = depth;
        this.area = area;

        cracker = new Cracker(length, location.getBlockX(), location.getBlockY(), location.getBlockZ(), width, depth);
        cracker.plot();
    }


    public void run() {
        World w = location.getWorld();
        Verbose.log("Cracking layer " + layerIndex);
        List<Vector> layer = cracker.get(layerIndex);
        if (layer.size() == 0) {
            stop();
            return;
        }
        for (Vector block : layer) {
            BlockIterator bi = new BlockIterator(w, block, new Vector(0, 1, 0), 0, (256 - block.getBlockY()));
            while (bi.hasNext()) {
                Block toInspect = bi.next();
                int id = toInspect.getTypeId();
                if (id != 0 && id != 7)
                    area.setBlockFast(toInspect, 0);
                if ((id & 0xFE) == 8) // 8 or 9
                    toInspect.setTypeId(9, true);
                else if ((id & 0xFE) == 10) // 10 or 11
                    toInspect.setTypeId(10, true);
            }
        }
        Storm.util.playSoundNearby(location, length * width + 500, "ambient.weather.thunder", 3F, Storm.random.nextInt(3) + 1);
        area.sendClientChanges();
        area.loadChunks();
        ++layerIndex;
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 20, 20);
    }

    /**
     * Ends the task.
     */
    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

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
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VolcanoWeather extends StormWeather {
    /**
     * Constructor. DO NOT CHANGE ARGUMENTS.
     *
     * @param storm Storm plugin object
     * @param world World name to act opon
     */
    public VolcanoWeather(Storm storm, String world) {
        super(storm, world);
    }

    Location victim;
    int size;
    List<Integer> unsafeBlocks = Arrays.asList(Material.WATER.getId(), Material.STATIONARY_WATER.getId());

    @Override
    public boolean canStart() {
        size = (int) Storm.random.gauss(25, 8);
        if (size < 5)
            return false;

        ArrayList<Location> candidate = new ArrayList<Location>();
        for (Chunk chunk : Bukkit.getWorld(world).getLoadedChunks()) {
            Location location = chunk.getBlock(Storm.random.nextInt(16), 64, Storm.random.nextInt(16)).getLocation();
            location = StormUtil.getSurface(location);
            if (!StormUtil.isLocationNearBlock(location, unsafeBlocks, 50))
                candidate.add(location);
        }
        if (candidate.size() == 0)
            return false;
        victim = candidate.get(Storm.random.nextInt(candidate.size()));
        return true;
    }

    @Override
    public void start() {
        Volcano.volcano(victim, size);
    }

    @Override
    public void end() {
        // Does nothing really
    }
}

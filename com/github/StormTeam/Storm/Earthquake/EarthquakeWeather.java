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

package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;

public class EarthquakeWeather extends StormWeather {
    Location victim;
    Quake quake;
    WorldVariables glob;
    int magnitude;

    /**
     * Constructor. DO NOT CHANGE ARGUMENTS.
     *
     * @param storm Storm plugin object
     * @param world World name to act opon
     */
    public EarthquakeWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
        magnitude = (int) Storm.random.triangular(1, 9, 2);
        autoKillTicks = magnitude * 30;
    }

    @Override
    public boolean canStart() {
        if (!glob.Weathers__Enabled_Natural__Disasters_Earthquakes)
            return false;

        ArrayList<Location> candidate = new ArrayList<Location>();
        for (Chunk chunk : Bukkit.getWorld(world).getLoadedChunks()) {
            Location location = chunk.getBlock(Storm.random.nextInt(16), 64, Storm.random.nextInt(16)).getLocation();
            location = StormUtil.getSurface(location);
            candidate.add(location);
        }
        if (candidate.size() == 0)
            return false;
        victim = candidate.get(Storm.random.nextInt(candidate.size()));
        return true;
    }

    @Override
    public void start() {
        int magnitude = (int) Storm.random.triangular(1, 9, 2);
        StormUtil.broadcast(glob.Natural__Disasters_Earthquakes_Message__On__Earthquake__Start.replace("%m", magnitude + ""), world);
        quake = EarthquakeControl.loadQuake(victim, magnitude);
    }

    @Override
    public void end() {
        StormUtil.broadcast(glob.Natural__Disasters_Earthquakes_Message__On__Earthquake__End, world);
        quake.stop();
    }
}

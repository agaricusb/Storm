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

package com.github.StormTeam.Storm.Meteor_Shower;

import com.github.StormTeam.Storm.Meteor_Shower.Tasks.ShowerWorker;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import static com.github.StormTeam.Storm.Storm.random;

public class ShowerWeather extends StormWeather {
    ShowerWorker worker;

    /**
     * Constructor. DO NOT CHANGE ARGUMENTS.
     *
     * @param storm Storm plugin object
     * @param world World name to act opon
     */
    public ShowerWeather(Storm storm, String world) {
        super(storm, world);
    }

    @Override
    public void start() {
        World world = Bukkit.getWorld(this.world);
        Chunk[] loadedChunks = world.getLoadedChunks();
        Chunk chunk = loadedChunks[random.nextInt(loadedChunks.length)];
        Location location = chunk.getBlock(random.nextInt(16), 255, random.nextInt(16)).getLocation();
        worker = new ShowerWorker(location, random.gaussUnsigned(60, 10));
    }

    @Override
    public void end() {
        worker.stop();
        worker = null;
    }
}

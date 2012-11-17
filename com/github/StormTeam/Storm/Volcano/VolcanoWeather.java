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
import com.github.StormTeam.Storm.Weather.StormWeather;

public class VolcanoWeather extends StormWeather {
    /**
     * Constructor. DO NOT CHANGE ARGUMENTS.
     *
     * @param storm Storm plugin object
     * @param world World name to act opon
     */
    protected VolcanoWeather(Storm storm, String world) {
        super(storm, world);
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }
}

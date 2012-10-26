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

import com.github.StormTeam.Storm.BlockShifter;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class Volcano implements Listener {
    private Location center;
    private World world;
    private float power;
    private int radius;
    private List<Vector> border;

    public Volcano(Location center, float power, int radius) {
        this.center = center;
        this.power = power;
        this.radius = radius;
        this.world = center.getWorld();
        this.border = new ArrayList<Vector>();
        for (Vector v : new PointsOnCircle(radius)) {
            this.border.add(v);
        }
    }

    void makeShaft() {
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        world.createExplosion(x, y, z, 3 * power);
        for (int i = 6; i <= y; ++i) {
            world.createExplosion(x, i, z, power);
            fillLayer(11, x, i - 5, z);
            BlockShifter.sendClientChanges(world);
        }
        for (int i = y - 5; i <= y; ++i) {
            fillLayer(11, x, i, z);
            //Call the update thing here
            BlockShifter.sendClientChanges(world);
        }
    }

    void randomExplosionsAroundShaftBorder(int y) {
        Location location = center.clone();
        location.add(border.get(Storm.random.nextInt(border.size())));
    }

    void fillLayer(int material, int x, int y, int z) {
        Location location = new Location(world, x, y, z);
        Block block = location.getBlock();

        if (block.getTypeId() != 0)
            return;
        BlockShifter.setBlockFast(block, material);
        // Recursively fill the adjacent blocks only if the current
        // location is within the radius specified, using Pythegorean
        // theorem
        if (Math.sqrt(Math.pow(x - center.getBlockX(), 2) + Math.pow(z - center.getBlockZ(), 2)) < this.radius) {
            fillLayer(material, x + 1, y, z);
            fillLayer(material, x - 1, y, z);
            fillLayer(material, x, y, z + 1);
            fillLayer(material, x, y, z - 1);
        }
    }

    Vector getFlight(Vector v, float power, double flight) {
        double x = v.getX() > 0.0D ? flight : v.getX() == 0.0D ? -flight : Storm.random.nextBoolean() ? flight : -flight;
        double y = power * 3.0F;
        double z = v.getZ() > 0.0D ? flight : v.getZ() == 0.0D ? -flight : Storm.random.nextBoolean() ? flight : -flight;
        return new Vector(x, y, z);
    }

    Vector getFlight(Location l, float power, double flight) {
        return getFlight(l.subtract(center).toVector(), power, flight);
    }
}

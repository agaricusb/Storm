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

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import static com.github.StormTeam.Storm.Storm.random;

public class ShowerWorker implements Runnable {
    double radius;
    Location center;
    int x, y, z;
    World world;
    int id;

    public ShowerWorker(Location center, double radius) {
        this.center = center;
        this.x = center.getBlockX();
        this.y = center.getBlockY();
        this.z = center.getBlockZ();
        this.world = center.getWorld();
        this.radius = radius;
    }

    static final double TWOPI = Math.PI * 2;

    protected Location getLocation() {
        double t = TWOPI * random.nextDouble();
        double u = random.nextDouble() + random.nextDouble();
        double r = (u > 1 ? 2 - u : u) * radius;
        double x = r * Math.cos(t) + this.x;
        double z = r * Math.sin(t) + this.z;
        return new Location(world, x, 255, z);
    }

    @Override
    public void run() {
        Location location = getLocation();
        Fireball fireball = (Fireball) world.spawnEntity(location, EntityType.FIREBALL);
        fireball.setDirection(new Vector(random.gauss(0, 2), random.nextInt(15, 30), random.gauss(0, 2)));
        fireball.setIsIncendiary(random.nextBoolean());
        fireball.setYield(random.nextInt(0, 2));
        if (Storm.random.nextInt(100) > 95)
            StormUtil.playSoundNearby(location, 100, "mob.ghast.shriek", 1F, 1F);
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Storm.instance, this, 10, 10);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

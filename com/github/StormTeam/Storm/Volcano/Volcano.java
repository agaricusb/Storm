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
import net.minecraft.server.Explosion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Volcano implements Listener {
    private Location center;
    private World world;
    private float power;
    private Set<Explosion> explosions = new HashSet<Explosion>();

    public Volcano(Location center, float power) {
        this.center = center;
        this.power = power;
        this.world = center.getWorld();
    }

    void makeVolcano() {
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        while (y > 0) {
            Explosion exp = new Explosion(((CraftWorld) world).getHandle(), null, x, y, z, power);
            explosions.add(exp);
            exp.a();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityExplodeEvent(EntityExplodeEvent e) {
        e.blockList();
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

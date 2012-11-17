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

import com.github.StormTeam.Storm.Cuboid;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Verbose;
import com.github.StormTeam.Storm.Volcano.Tasks.EruptTask;
import com.github.StormTeam.Storm.Volcano.Tasks.GrowthTask;
import net.minecraft.server.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolcanoWorker {
    public Location center;
    public World world;
    public int radius;
    public VolcanoControl controller = null;
    public int layer = 0;
    public boolean active = true;
    public int x, y, z;
    public Cuboid area;
    public Set<Integer> explosionIDs = new HashSet<Integer>();
    public boolean isExternal = false;

    public EruptTask eruptor;
    public GrowthTask grower;

    public VolcanoWorker(Location center, int radius, int layer) {
        this.center = center;
        this.world = center.getWorld();
        this.radius = radius;
        this.layer = layer;
    }

    public VolcanoWorker() {
    }

    public void start() {
        if (controller == null)
            Storm.pm.registerEvents((controller = new VolcanoControl()), Storm.instance);

        area = new Cuboid(center, center);
        area = area.expand(BlockFace.UP, radius).expand(BlockFace.DOWN, radius / 4);
        area = area.expand(BlockFace.NORTH, radius * 2);
        area = area.expand(BlockFace.EAST, radius * 2);
        area = area.expand(BlockFace.SOUTH, radius * 2);
        area = area.expand(BlockFace.WEST, radius * 2);
        x = center.getBlockX();
        y = center.getBlockY();
        z = center.getBlockZ();

        if (!isExternal) {
            explode(center, 10F);
            active = true;
        }

        grower = new GrowthTask(this);
        grower.start();
        eruptor = new EruptTask(this);
        eruptor.start();
        VolcanoControl.volcanoes.add(this);
    }

    public void delete() {
        VolcanoControl.volcanoes.remove(this);
        stop();
    }

    public void stop() {
        if (controller != null && VolcanoControl.volcanoes.isEmpty())
            controller.forget();
        grower.stop();
        eruptor.stop();
        active = false;
    }

    public boolean ownsBlock(Block block) {
        /* && Math.sqrt(Math.pow(Math.abs(block.getX() - x), 2) + Math.pow(Math.abs(block.getZ() - z), 2)) < this.radius * 2 */
        return block.getWorld().equals(world) && area.contains(block);
    }

    public void recalculateLayer() {
        while (world.getBlockTypeIdAt(x, y + layer, z) == 0) layer--;
        Verbose.log("Recalculated layer to " + layer);
    }

    public void explode(final Location exp, final float power) {
        EntityTNTPrimed dummy = new EntityTNTPrimed(((CraftWorld) world).getHandle()); //Entity is abstract, and really, we just need the id...
        explosionIDs.add(dummy.id);
        Verbose.log("Adding explosion with associated id: " + dummy.id + " for volcano.");
        StormUtil.createExplosion(dummy, exp.getX(), exp.getY(), exp.getZ(), power, true);
    }

    @Override
    public String toString() {
        StringBuilder serialized = new StringBuilder();
        serialized.append("").append((int) center.getX());
        serialized.append("|").append((int) center.getY());
        serialized.append("|").append((int) center.getZ());
        serialized.append("|").append(world.getName());
        serialized.append("|").append(radius);
        serialized.append("|").append(layer);
        serialized.append("|").append(active);
        return serialized.toString() + "\n";
    }

    public void fromString(String de) {
        List<String> split = Arrays.asList(de.replace("\n", "").split("\\|"));

        int x = Integer.parseInt(split.get(0));
        int y = Integer.parseInt(split.get(1));
        int z = Integer.parseInt(split.get(2));

        world = Bukkit.getWorld(split.get(3));
        center = new Location(world, x, y, z);
        radius = Integer.parseInt(split.get(4));
        layer = Integer.parseInt(split.get(5));
        active = Boolean.valueOf(split.get(6));

        isExternal = true;
    }
}

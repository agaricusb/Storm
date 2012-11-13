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
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Verbose;
import net.minecraft.server.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolcanoWorker {
    public Location center;
    public World world;
    public int radius;
    public Listener controller = null;
    public int layer = 0;
    public int volcanoGrowthID = -1;
    public boolean active = true;
    private int x, y, z;
    public Cuboid area;
    public Set<Integer> explosionIDs = new HashSet<Integer>();
    public boolean loaded = false;

    public VolcanoWorker(Location center, int radius, int layer) {
        this.center = center;
        this.world = center.getWorld();
        this.radius = radius;
        this.layer = layer;
    }

    public VolcanoWorker() {
    }

    public void deserialize(String de) {
        de = de.replace("\n", "");
        Verbose.log(de);
        List<String> split = Arrays.asList(de.split("\\|"));

        int x = Integer.parseInt(split.get(0));
        int y = Integer.parseInt(split.get(1));
        int z = Integer.parseInt(split.get(2));

        world = Bukkit.getWorld(split.get(3));
        center = new Location(world, x, y, z);
        radius = Integer.parseInt(split.get(4));
        layer = Integer.parseInt(split.get(5));
        active = Boolean.valueOf(split.get(6));

        loaded = true;
    }

    public void spawn() {
        try {
            if (controller == null) {
                controller = new VolcanoControl();
                Storm.pm.registerEvents(controller, Storm.instance);
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
        area = new Cuboid(center, center);
        area = area.expand(BlockFace.UP, radius).expand(BlockFace.DOWN, radius / 4);
        area = area.expand(BlockFace.NORTH, radius * 2);
        area = area.expand(BlockFace.EAST, radius * 2);
        area = area.expand(BlockFace.SOUTH, radius * 2);
        area = area.expand(BlockFace.WEST, radius * 2);
        x = center.getBlockX();
        y = center.getBlockY();
        z = center.getBlockZ();
        if (!loaded)
            syncExplosion(center, 10F);
        grow(true);
        VolcanoControl.volcanoes.add(this);
    }

    public void remove() {
        if (controller != null && VolcanoControl.volcanoes.isEmpty()) {
            HandlerList.unregisterAll(controller);
        }
        grow(false);
        VolcanoControl.volcanoes.remove(this);
    }

    public void grow(boolean flag) {
        if (flag)
            if (!Bukkit.getScheduler().isCurrentlyRunning(volcanoGrowthID))
                volcanoGrowthID = Bukkit.getScheduler().scheduleAsyncDelayedTask(Storm.instance, new Runnable() {
                    public void run() {
                        generateVolcanoAboveGround();
                    }
                }, 0L);
            else if (Bukkit.getScheduler().isCurrentlyRunning(volcanoGrowthID))
                Bukkit.getScheduler().cancelTask(volcanoGrowthID);
    }

    void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    void generateVolcanoAboveGround() {
        int height = radius * 2 + y;
        long sleep = 15000;
        // erupt();
        for (int i = 0; i < height; ++i) {
            generateLayer();
            sleep(sleep += 100);
        }
        grow(false);
    }

    void generateLayer() {

        while (world.getBlockTypeIdAt(x, y + layer, z) == 0) {
            layer--;
        }
        layer++;
        Block set = center.clone().add(0, layer, 0).getBlock();
        if (!area.contains(set)) {
            grow(false);
            return;
        }
        area.setBlockFast(set, Material.LAVA.getId());
        area.sendClientChanges();
        dumpVolcanoes();
    }

    public boolean ownsBlock(Block block) {
        return block.getWorld().equals(world) /* && Math.sqrt(Math.pow(Math.abs(block.getX() - x), 2)
                + Math.pow(Math.abs(block.getZ() - z), 2)) < this.radius * 2 */ && area.contains(block);
    }

    public void erupt() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Storm.instance, new Runnable() {
            public void run() {
                while (true) {
                    if (layer < 10)
                        continue;
                    Location location = center.clone();
                    double dx = Storm.random.nextGaussian() * 5;
                    double dy = Storm.random.nextGaussian() * 5;
                    double dz = Storm.random.nextGaussian() * 5;
                    location.add(dx, layer + dy, dz);
                    syncExplosion(location, 15f);

                    sleep(5000 / layer);

                }
            }
        }, 1L, 15000L);
    }

    void syncExplosion(final Location exp, final float power) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                EntityTNTPrimed dummy = new EntityTNTPrimed(((CraftWorld) world).getHandle()); //Entity is abstract, and really, we just need the id...
                explosionIDs.add(dummy.id);
                Verbose.log("Adding explosion with associated id: " + dummy.id + " for volcano.");
                Storm.util.createExplosion(dummy, exp.getX(), exp.getY(), exp.getZ(), power, true);
            }
        }, 0L);
    }

    public void dumpVolcanoes() {
        try {
            VolcanoControl.save(Volcano.vulkanos);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
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
}

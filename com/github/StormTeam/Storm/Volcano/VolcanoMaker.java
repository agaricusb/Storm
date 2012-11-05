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

import com.github.StormTeam.Storm.Cuboid;
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class VolcanoMaker {
    public Location center;
    public World world;
    public float power;
    public int radius;
    public Listener controller = null;
    public int layer = 0;
    public int volcanoGrowthID = -1;
    public boolean active = true;
    private int x, y, z;
    public Cuboid area;
    private final Object mutex = new Object();

    public VolcanoMaker(Location center, float power, int radius, int layer) {
        this.center = center;
        this.world = center.getWorld();
        this.power = power;
        this.radius = radius;
        this.layer = layer;
    }

    public VolcanoMaker() {
    }

    public String serialize() {
        StringBuilder serialized = new StringBuilder();
        serialized.append("").append((int) center.getX());
        serialized.append("|").append((int) center.getY());
        serialized.append("|").append((int) center.getZ());
        serialized.append("|").append(world.getName());
        serialized.append("|").append(radius);
        serialized.append("|").append(layer);
        serialized.append("|").append(active);

        //  String back = ":45:48:99:world:30:5\n";
        return serialized.toString() + "\n";
    }

    public void deserialize(String de) {
        de = de.replace("\n", "");
        System.out.println(de);
        List<String> split = Arrays.asList(de.split("\\|"));

        int x = Integer.parseInt(split.get(0));
        int y = Integer.parseInt(split.get(1));
        int z = Integer.parseInt(split.get(2));

        world = Bukkit.getWorld(split.get(3));
        center = new Location(world, x, y, z);
        radius = Integer.parseInt(split.get(4));
        layer = Integer.parseInt(split.get(5));
        active = Boolean.valueOf(split.get(6));
    }

    public void spawn() {
        grow(true);
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
        this.x = center.getBlockX();
        this.y = center.getBlockY();
        this.z = center.getBlockZ();
        System.out.println(area.toString());
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
        for (int i = 0; i < height; ++i) {
            generateLayer(center.getBlockY() + layer);
            sleep(sleep += 100);
        }
        grow(false);
    }

    void generateLayer(int y) {   
        dumpVolcanoes();
        Location location = center.clone();
        location.setY(y);
        
        if (location.subtract(0, 1, 0).getBlock().getTypeId() == 0) {
            layer--;
            generateLayer(center.getBlockY() + layer);
            return;        
        } 

        synchronized (Storm.mutex) {
            area.syncSetBlockFast(location.getBlock(), Material.LAVA.getId());
            area.sendClientChanges();
        }
        layer++;
    }

    public boolean ownsBlock(Block block) {
        return block.getWorld().equals(world) && Math.sqrt(Math.pow(Math.abs(block.getX() - x), 2)
                + Math.pow(Math.abs(block.getZ() - z), 2)) < this.radius * 2;
    }

    public void erupt() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, new Runnable() {
            public void run() {
                while (true) {
                    if (layer > 10)
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                    Location location = center.clone();
                    double dx = Storm.random.nextGaussian() * 5;
                    double dy = Storm.random.nextGaussian() * 5;
                    double dz = Storm.random.nextGaussian() * 5;
                    location.add(dx, layer + dy, dz);
                    syncExplosion(location, 5f);
                    try {
                        Thread.sleep(5000 / layer);
                    } catch (InterruptedException ignored) {}
                }
            }
        }, 1L, 15000L);
    }

    void syncExplosion(final Location exp, final float power) {
        Future<Void> callExplosion = Bukkit.getScheduler().callSyncMethod(Storm.instance,
                new Callable<Void>() {
                    @Override
                    public Void call() {
                        world.createExplosion(exp, power, true);
                        return null;
                    }
                }
        );
        try {
            callExplosion.get();
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    public void dumpVolcanoes() {
        try {
            VolcanoControl.save(Volcano.vulkanos);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

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
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        serialized.append("" + (int) center.getX());
        serialized.append("|" + (int) center.getY());
        serialized.append("|" + (int) center.getZ());
        serialized.append("|" + world.getName());
        serialized.append("|" + (int) radius);
        serialized.append("|" + (int) layer);
        serialized.append("|" + active);

        //  String back = ":45:48:99:world:30:5\n";
        return serialized.toString() + "\n";
    }

    public void deserialize(String de) {
        de = de.replace("\n", "");
        System.out.println(de);
        List<String> split = Arrays.asList(de.split("|"));

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
        int height = radius * 2 + center.getBlockY();
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
        synchronized (location) {
            BlockShifter.syncSetBlock(location.getBlock(), 11);
            layer++;
        }
    }

    public boolean ownsBlock(Block block) {
        if (!block.getWorld().equals(world)) {
            return false;
        }
        return Math.sqrt(Math.abs(block.getX() - center.getBlockX()) ^ 2 + Math.abs(block.getZ() - center.getBlockZ()) ^ 2) < this.radius * 2;
    }

    public void erupt() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, new Runnable() {
            public void run() {
                Location location = center.clone();
                location.setY(center.getBlockY() + layer);
                syncExplosion(location, 5f);
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

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
import com.github.StormTeam.Storm.Serialization.SerializableLocation;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class Volcano implements Serializable {
    private SerializableLocation center;
    public transient World world;
    private float power;
    public int radius;
    private transient static boolean isListenerRegistered = false;
    private transient static Listener controller;
    public transient int layer = 0;
    private transient int volcanoGrowthID = -1;
    private transient File vulkanos;
    public int x, y, z;

    public Volcano(Location center, float power, int radius) {
        this.center = new SerializableLocation(center);
        this.world = center.getWorld();
        this.power = power;
        this.radius = radius;
        this.world = center.getWorld();
        this.vulkanos = new File(Storm.instance.getDataFolder() + File.separator + "volcanoes.bin");
    }

    public Volcano(World world, int x, int y, int z, float power, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.center = new SerializableLocation(new Location(world, x, y, z));
        this.world = center.getWorld();
        this.power = power;
        this.radius = radius;
        this.world = center.getWorld();
        this.vulkanos = new File(Storm.instance.getDataFolder() + File.separator + "volcanoes.bin");

    }

    public void spawn() {
        if (!isListenerRegistered) {
            Storm.pm.registerEvents((controller = new VolcanoControl()), Storm.instance);
        }
        volcanoGrowthID = Bukkit.getScheduler().scheduleAsyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                generateVolcanoAboveGround();
            }
        }, 10L);
    }

    public void remove() {
        if (isListenerRegistered) {
            HandlerList.unregisterAll(controller);
        }
        if (Bukkit.getScheduler().isCurrentlyRunning(volcanoGrowthID))
            Bukkit.getScheduler().cancelTask(volcanoGrowthID);
    }

    void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    void syncExplosion(final int x, final int y, final int z, final float power) {
        Future callExplosion = Bukkit.getScheduler().callSyncMethod(Storm.instance,
                new Callable<Void>() {
                    public Void call() {
                        world.createExplosion(x, y, z, power, true);
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

    void generateVolcanoAboveGround() {
        int height = radius * 2 + center.getBlockY();
        long sleep = 15000;
        for (int i = center.getBlockY(); i < height; ++i) {
            generateLayer(i);
            sleep(sleep += 100);
        }
    }

    void generateLayer(int y) {
        dumpVolcanoes();
        Location location = center.getLocation().clone();
        location.setY(y);
        BlockShifter.syncSetBlock(location.getBlock(), Material.LAVA.getId());
        layer = layer++;
    }

    public boolean ownsBlock(Block block) {
        if (block.getWorld() != world)
            return false;
        Location location = block.getLocation();
        location.setY(center.getBlockY());
        return location.distance(center.getLocation()) < this.radius * 2;
    }

    public void dumpVolcanoes() {
        try {
            if (!vulkanos.exists())
                if (!vulkanos.createNewFile())
                    return;
            VolcanoControl.save(vulkanos);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

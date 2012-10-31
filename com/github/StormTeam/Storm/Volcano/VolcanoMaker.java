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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class VolcanoMaker {
    private Location center;
    private Chunk centerChunk;
    private World world;
    private float power;
    private int radius;
    private Listener controller = null;
    private int layer = 0;
    private int volcanoGrowthID = -1;

    public VolcanoMaker(Location center, float power, int radius, int layer) {
        this.center = center;
        this.world = center.getWorld();
        this.power = power;
        this.radius = radius;
        this.layer = layer;
    }

    public VolcanoMaker() {
    }

    ;

    public World getWorld() {
        return this.world;
    }

    public Location getCenter() {
        return this.center;
    }

    public String serialize() {
        StringBuilder serialized = new StringBuilder();
        serialized.append("" + (int) center.getX());
        serialized.append("|" + (int) center.getY());
        serialized.append("|" + (int) center.getZ());
        serialized.append("|" + world.getName());
        serialized.append("|" + (int) radius);
        serialized.append("|" + (int) layer);

        //  String back = ":45:48:99:world:30:5\n";
        return serialized.toString() + "\n";
    }

    public void deserialize(String de) {
        de = de.replace("\n", "");
        System.out.println(de);
        List<String> split = Arrays.asList(de.split("\\|"));

        System.out.println(split);

        int x = Integer.parseInt(split.get(0));
        int y = Integer.parseInt(split.get(1));
        int z = Integer.parseInt(split.get(2));

        world = Bukkit.getWorld(split.get(3));
        center = new Location(world, x, y, z);
        radius = Integer.parseInt(split.get(4));
        layer = Integer.parseInt(split.get(5));
    }

    public void spawn() {

        volcanoGrowthID = Bukkit.getScheduler().scheduleAsyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                generateVolcanoAboveGround();
            }
        }, 0L);


        try {
            if (controller == null) {
                controller = new VolcanoControl();
                Storm.pm.registerEvents(controller, Storm.instance);
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
        VolcanoControl.volcanoes.add(this);
        this.centerChunk = world.getChunkAt(center);
    }

    public void remove() {
        if (controller != null && VolcanoControl.volcanoes.isEmpty()) {
            HandlerList.unregisterAll(controller);
        }
        if (Bukkit.getScheduler().isCurrentlyRunning(volcanoGrowthID))
            Bukkit.getScheduler().cancelTask(volcanoGrowthID);
        VolcanoControl.volcanoes.remove(this);
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
            if (!centerChunk.isLoaded())
                continue;
            generateLayer(center.getBlockY() + layer);
            sleep(sleep += 100);
        }
    }

    void generateLayer(int y) {
        dumpVolcanoes();
        Location location = center.clone();
        location.setY(y);
        BlockShifter.syncSetBlock(location.getBlock(), Material.LAVA.getId());
        layer++;
    }

    public boolean ownsBlock(Block block) {
        if (!block.getWorld().equals(world)) {
            return false;
        }
        Location location = block.getLocation();
        location.setY(center.getBlockY());
        return location.distance(center) < this.radius * 2;
    }

    public void dumpVolcanoes() {
        try {
            VolcanoControl.save(Volcano.vulkanos);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

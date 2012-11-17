package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Earthquake.Tasks.QuakeTask;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.World;

public class Quake {
    public World world;
    public final Location epicenter;
    private EarthquakeControl controller;
    private QuakeTask quaker;
    public final int radius;
    public final int magnitude;

    public Quake(Location epicenter, int magnitude) {
        this.epicenter = epicenter;
        this.radius = magnitude * 100;
        this.magnitude = magnitude;
        this.world = epicenter.getWorld();
    }

    public void start() {
        // Blocks will bounce everywhere in the quake!
        Storm.pm.registerEvents((controller = new EarthquakeControl()), Storm.instance);
        quaker = new QuakeTask(this);

        // Get cracking!
        EarthquakeControl.crack(new Location(world, epicenter.getBlockX(), epicenter.getBlockY(), epicenter.getBlockZ()), (int) Math.sqrt(radius) * 3, magnitude * 2, 64 + magnitude);
    }

    public void stop() {
        if (controller != null && EarthquakeControl.quakes.isEmpty()) {
            controller.forget();
        }
        quaker.stop();
    }

    public boolean isQuaking(Location point) {
        return point.getWorld().getName().equals(this.world) && epicenter.distance(point) <= radius;
    }
}

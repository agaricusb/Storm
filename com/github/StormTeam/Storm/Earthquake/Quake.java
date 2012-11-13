package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Earthquake.Listeners.BlockListener;
import com.github.StormTeam.Storm.Earthquake.Listeners.MobListener;
import com.github.StormTeam.Storm.Earthquake.Tasks.QuakeTask;
import com.github.StormTeam.Storm.Pair;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Level;

public class Quake {

    private Storm storm;

    public int quakeID;

    private String world;
    private Pair<Integer, Integer> point1;
    private Pair<Integer, Integer> point2;
    private Pair<Integer, Integer> epicenter;

    private MobListener mL;
    private BlockListener bL;

    private int tID, rLID;
    private boolean isLoading = false;
    private boolean isRunning = false;

    private void load() {

        this.isLoading = true;

        World w = Bukkit.getServer().getWorld(world);
        int x = (point1.LEFT + point2.LEFT) / 2;
        int z = (point1.RIGHT + point2.RIGHT) / 2;
        this.epicenter = new Pair<Integer, Integer>(x, z);

        // Calculate blocks
        Chunk chunkOrigin = w.getChunkAt(x, z);
        Chunk chunkUp = w.getChunkAt(x + 16, z);
        Chunk chunkDown = w.getChunkAt(x - 16, z);

        storm.getLogger().severe("===== DEBUG =====");
        storm.getLogger().severe("----- Chunk center -----");
        storm.getLogger().severe("X: " + chunkOrigin.getX());
        storm.getLogger().severe("Z: " + chunkOrigin.getZ());
        storm.getLogger().severe("----- Chunk left -----");
        storm.getLogger().severe("X: " + chunkUp.getX());
        storm.getLogger().severe("Z: " + chunkUp.getZ());
        storm.getLogger().severe("----- Chunk right -----");
        storm.getLogger().severe("X: " + chunkDown.getX());
        storm.getLogger().severe("Z: " + chunkDown.getZ());

        // Creepers will not attack player during quake!
        mL = new MobListener(this);

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(mL, storm);

        tID = Bukkit.getScheduler().scheduleSyncDelayedTask(storm, new Runnable() {

            @Override
            public void run() {
                start();
            }

        }, 20L);//18000L);
    }

    private void go() {

        // Blocks will bounce everywhere in the quake!
        bL = new BlockListener(this, storm);
        Bukkit.getServer().getPluginManager().registerEvents(bL, storm);

        storm.getLogger().log(Level.SEVERE, "Quake started at: [" + this.point1.LEFT + " - " + this.point1.RIGHT + "] - [" + this.point2.LEFT + " - " + this.point2.RIGHT + "]");

        tID = Bukkit.getScheduler().scheduleSyncRepeatingTask(storm, new QuakeTask(this), 0L, 2L);
    }

    public Quake(Storm storm, int qID, Location point1, Location point2) {
        this.storm = storm;
        this.quakeID = qID;

        String w = point1.getWorld().getName();
        String w2 = point2.getWorld().getName();
        if (w.equals(w2)) {
            int minX = Math.min(point1.getBlockX(), point2.getBlockX());
            int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
            int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
            int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
            this.world = w;
            this.point1 = new Pair<Integer, Integer>(minX, minZ);
            this.point2 = new Pair<Integer, Integer>(maxX, maxZ);

            storm.getLogger().log(Level.SEVERE, "Quake loading at: [" + this.point1.LEFT + " - " + this.point1.RIGHT + "] - [" + this.point2.LEFT + " - " + this.point2.RIGHT + "]");
            this.load();
        } else {
            throw new RuntimeException("World " + w + " and World " + w2 + " do not match!");
        }
    }

    void start() {
        this.isLoading = false;
        this.isRunning = true;
        this.go();
    }

    public void stop() {
        this.isLoading = false;
        this.isRunning = false;
        if (mL != null) {
            mL.forget();
        }

        if (bL != null) {
            bL.forget();
        }

        if (Bukkit.getScheduler().isCurrentlyRunning(tID)) {
            Bukkit.getScheduler().cancelTask(tID);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public boolean isQuaking(Location point) {
        if (!point.getWorld().getName().equals(this.world))
            return false;

        /*storm.getLogger().severe("========= DEBUG ========");
          storm.getLogger().severe("loc x: " + point.getBlockX());
          storm.getLogger().severe("loc z: " + point.getBlockZ());
          storm.getLogger().severe("quake min x: " + point1.LEFT);
          storm.getLogger().severe("quake min z: " + point1.RIGHT);
          storm.getLogger().severe("quake max x: " + point2.LEFT);
          storm.getLogger().severe("quake max z: " + point2.RIGHT);*/


        return (point.getBlockX() >= point1.LEFT && point.getBlockZ() >= point1.RIGHT
                && point.getBlockX() <= point2.LEFT && point.getBlockZ() <= point2.RIGHT);
    }

    public World getWorld() {
        return Bukkit.getServer().getWorld(this.world);
    }

    public Pair<Integer, Integer> getEpicenter() {
        return this.epicenter;
    }

    public Pair<Integer, Integer> getPointOne() {
        return this.point1;
    }

    public Pair<Integer, Integer> getPointTwo() {
        return this.point2;
    }
}

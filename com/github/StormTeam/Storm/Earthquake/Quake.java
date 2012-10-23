package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Earthquake.Listeners.BlockListener;
import com.github.StormTeam.Storm.Earthquake.Listeners.MobListener;
import com.github.StormTeam.Storm.Earthquake.Tasks.QuakeTask;
import com.github.StormTeam.Storm.Pair;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Level;

public class Quake {

    private Storm storm;

    @SuppressWarnings("FieldCanBeLocal")
    private Integer quakeID;

    private String world;
    private Pair<Integer, Integer> point1;
    private Pair<Integer, Integer> point2;
    private Pair<Integer, Integer> epicenter;

    private MobListener mL;
    private BlockListener bL;

    private Integer tID, rLID;
    private Boolean isLoading = false;
    private Boolean isRunning = false;

    private void load() {

        this.isLoading = true;

        World w = storm.getServer().getWorld(world);
        int x = (point1.LEFT + point2.LEFT) / 2;
        int z = (point1.RIGHT + point2.RIGHT) / 2;
        this.epicenter = new Pair<Integer, Integer>(x, z);

        // Calculate blocks
        Chunk c = w.getChunkAt(x, z);
        Chunk c2 = w.getChunkAt(x + 16, z);
        Chunk c3 = w.getChunkAt(x - 16, z);

        storm.getLogger().severe("===== DEBUG =====");
        storm.getLogger().severe("----- Chunk center -----");
        storm.getLogger().severe("X: " + c.getX());
        storm.getLogger().severe("Z: " + c.getZ());
        storm.getLogger().severe("----- Chunk left -----");
        storm.getLogger().severe("X: " + c2.getX());
        storm.getLogger().severe("Z: " + c2.getZ());
        storm.getLogger().severe("----- Chunk right -----");
        storm.getLogger().severe("X: " + c3.getX());
        storm.getLogger().severe("Z: " + c3.getZ());

        // Creepers will not attack player during quake!
        mL = new MobListener(this);

        // Register events
        storm.getServer().getPluginManager().registerEvents(mL, storm);

        tID = storm.getServer().getScheduler().scheduleSyncDelayedTask(storm, new Runnable() {

            @Override
            public void run() {
                start();
            }

        }, 20L);//18000L);
    }

    private void go() {

        // Blocks will bounce everywhere in the quake!
        bL = new BlockListener(this, storm);
        storm.getServer().getPluginManager().registerEvents(bL, storm);

        storm.getLogger().log(Level.SEVERE, "Quake started at: [" + this.point1.LEFT + " - " + this.point1.RIGHT + "] - [" + this.point2.LEFT + " - " + this.point2.RIGHT + "]");

        tID = storm.getServer().getScheduler().scheduleSyncRepeatingTask(storm, new QuakeTask(this, storm), 0L, 2L);
    }

    public Quake(Storm storm, Integer qID, Location point1, Location point2) {
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
        if (null != mL) {
            mL.forget();
        }

        if (null != bL) {
            bL.forget();
        }

        if (rLID != null) {
            storm.getServer().getScheduler().cancelTask(rLID);
        }

        if (tID != null) {
            storm.getServer().getScheduler().cancelTask(tID);
        }
    }

    public Boolean isRunning() {
        return this.isRunning;
    }

    public Boolean isLoading() {
        return this.isLoading;
    }

    public Boolean isQuaking(Location point) {
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
        return storm.getServer().getWorld(this.world);
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

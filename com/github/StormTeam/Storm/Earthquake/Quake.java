package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Earthquake.Listeners.BlockListener;
import com.github.StormTeam.Storm.Earthquake.Listeners.MobListener;
import com.github.StormTeam.Storm.Earthquake.Tasks.QuakeTask;
import com.github.StormTeam.Storm.Pair;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Verbose;
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
    private QuakeTask quaker;

    public boolean isLoading = false;
    public boolean isRunning = false;

    public int radius = 50;

    private void load() {

        isLoading = true;

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
        start();
    }

    private void go() {

        // Blocks will bounce everywhere in the quake!
        bL = new BlockListener(this, storm);
        Bukkit.getServer().getPluginManager().registerEvents(bL, storm);
        mL = new MobListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(mL, storm);

        Verbose.log(Level.SEVERE, "Quake started at: [" + this.point1.LEFT + " - " + this.point1.RIGHT + "] - [" + this.point2.LEFT + " - " + this.point2.RIGHT + "]");

        quaker = new QuakeTask(this);

        // Get cracking!
        Bukkit.getScheduler().scheduleAsyncDelayedTask(storm, new Runnable() {
            @Override
            public void run() {
                int x = (point1.LEFT + point2.LEFT) / 2;
                int y = 80;
                int z = point1.RIGHT;
                int length = point2.RIGHT - point1.RIGHT;
                Earthquake.crack(new Location(Bukkit.getWorld(world), x, y, z), length, 20, y - 10);
            }
        }, 10);
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

            Verbose.log(Level.SEVERE, "Quake loading at: [" + this.point1.LEFT + " - " + this.point1.RIGHT + "] - [" + this.point2.LEFT + " - " + this.point2.RIGHT + "]");
            load();
        } else {
            throw new RuntimeException("World " + w + " and World " + w2 + " do not match!");
        }
    }

    void start() {
        isLoading = false;
        isRunning = true;
        go();
    }

    public void stop() {
        isLoading = false;
        isRunning = false;
        if (mL != null) {
            mL.forget();
        }

        if (bL != null) {
            bL.forget();
        }

        if (quaker != null)
            quaker.stop();
    }

    public boolean isQuaking(Location point) {
        return point.getWorld().getName().equals(this.world) && Math.sqrt(Math.pow(epicenter.LEFT - point.getBlockX(), 2) + (Math.pow(epicenter.RIGHT - point.getBlockZ(), 2))) <= radius;
    }

    public World getWorld() {
        return Bukkit.getServer().getWorld(world);
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

package com.github.StormTeam.Storm.Earthquake.Tasks;

import com.github.StormTeam.Storm.Cuboid;
import com.github.StormTeam.Storm.Math.Cracker;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Verbose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;

public class RuptureTask implements Runnable {

    private Location location;
    private int length;
    private final int width;
    private int id;
    private int layerIndex = 0;
    private Cracker cracker;
    private Cuboid area;
    private World world;

    public RuptureTask(Cuboid area, Location location, int length, int width, int depth) {
        this.location = location;
        this.world = location.getWorld();
        this.length = length;
        this.width = width;
        this.area = area;

        cracker = new Cracker(length, location.getBlockX(), location.getBlockY(), location.getBlockZ(), width, depth);
        cracker.plot();
    }

    public void run() {
        Verbose.log("Cracking layer " + layerIndex);
        List<Vector> layer = cracker.get(layerIndex);
        if (layer.size() == 0) {
            stop();
            return;
        }
        for (Vector block : layer) {
            BlockIterator bi = new BlockIterator(world, block, new Vector(0, 1, 0), 0, (256 - block.getBlockY()));
            while (bi.hasNext()) {
                Block toInspect = bi.next();
                int bid = toInspect.getTypeId();
                if (bid == 0 || bid == 7)
                    continue;
                area.setBlockFast(toInspect, 0);
                if ((bid & 0xFE) == 8) // 8 or 9
                    toInspect.setTypeId(9, true);
                else if ((bid & 0xFE) == 10) // 10 or 11
                    toInspect.setTypeId(10, true);
            }
        }
        StormUtil.playSoundNearby(location, (length * width) / 2, "ambient.weather.thunder", 1F, Storm.random.nextInt(3) + 1);
        area.sendClientChanges();
        ++layerIndex;
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 0, 20);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

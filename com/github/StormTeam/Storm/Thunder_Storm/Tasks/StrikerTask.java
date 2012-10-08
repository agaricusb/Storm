/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.StormTeam.Storm.Thunder_Storm.Tasks;

/**
 *
 * @author Tudor
 */
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.StormTeam.Storm.BlockTickSelector;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.block.BlockFace;

public class StrikerTask {

    private int id, end_;
    private Storm storm;
    private GlobalVariables glob;
    private BlockTickSelector ticker;
    private World affectedWorld;
    private Random rand = new Random();

    public StrikerTask(Storm storm, World affectedWorld) {
        this.storm = storm;
        this.glob = Storm.wConfigs.get(affectedWorld.getName());
        this.affectedWorld = affectedWorld;
        try {
            ticker = new BlockTickSelector(affectedWorld,
                    glob.Thunder__Storm_Strike__Chance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        id = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ArrayList<Block> bloks = ticker.getRandomTickedBlocks();

                            for (Block b : bloks) {
                                Block tran = b.getRelative(BlockFace.DOWN);
                                if (Storm.biomes.isRainy(tran.getBiome())) {
                                    affectedWorld.strikeLightning(tran.getLocation());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                0,
                glob.Thunder__Storm_Scheduler_Striker__Calculation__Intervals__In__Ticks);

        end_ = Bukkit.getScheduler()
                .scheduleAsyncDelayedTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Storm.manager.stopWeather("storm_thunderstorm", affectedWorld.getName());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 7500 + rand.nextInt(1024));

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
        Bukkit.getScheduler().cancelTask(end_);
    }
}

package com.github.StormTeam.Storm.Thunder_Storm.Tasks;

/**
 *
 * @author Tudor
 */

import com.github.StormTeam.Storm.BlockTickSelector;
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class StrikerTask implements Runnable {

    private int id;
    private final Storm storm;
    private final GlobalVariables glob;
    private BlockTickSelector ticker;
    private final World affectedWorld;

    public StrikerTask(Storm storm, World affectedWorld) {
        this.storm = storm;
        this.glob = Storm.wConfigs.get(affectedWorld.getName());
        this.affectedWorld = affectedWorld;
        try {
            ticker = new BlockTickSelector(affectedWorld,
                    glob.Thunder__Storm_Strike__Chance);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    @Override
    public void run() {
        try {
            ArrayList<Block> blocks = ticker.getRandomTickedBlocks();
            for (Block b : blocks) {
                Block tran = b.getRelative(BlockFace.DOWN);
                if (Storm.util.isRainy(tran.getBiome())) {
                    affectedWorld.strikeLightning(tran.getLocation());
                }
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                storm, this, 0,
                glob.Thunder__Storm_Scheduler_Striker__Calculation__Intervals__In__Ticks);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

package com.github.StormTeam.Storm.Volcano.Tasks;

import org.bukkit.entity.FallingBlock;

/**
 *
 * @author xiaomao
 */

import com.github.StormTeam.Storm.BlockTickSelector;
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class EruptTask implements Runnable {

    private int id;
    private final Storm storm;
    private final VolcanoWorker volcano;
    private final GlobalVariables glob;
    private BlockTickSelector ticker;
    private final World affectedWorld;

    public EruptTask(Storm storm, World affectedWorld, VolcanoWorker volcano) {
        this.storm = storm;
        this.volcano = volcano;
        this.glob = Storm.wConfigs.get(affectedWorld.getName());
        this.affectedWorld = affectedWorld;
    }

    @Override
    public void run() {
        FallingBlock block = volcano.world.spawnFallingBlock(volcano.center, 10, 0);
        block.setVelocity(new Vector(Math.random() - 0.5, 0.3, Math.random() - 0.5));
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(storm, this, 0, 13);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

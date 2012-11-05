package com.github.StormTeam.Storm.Acid_Rain.Tasks;

import com.github.StormTeam.Storm.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * An object for dissolving blocks during acid rain.
 *
 * @author Tudor
 */

public class BlockDissolverTask {

    private int id;
    private final Storm storm;
    private final GlobalVariables glob;
    private BlockTickSelector ticker;

    private final List<BlockTransformer> transformations = new ArrayList();

    /**
     * Creates a dissolver object for given world.
     *
     * @param storm         The Storm plugin used for config retrieving
     * @param affectedWorld The world to handle
     */

    public BlockDissolverTask(Storm storm, String affectedWorld) {
        this.storm = storm;
        glob = Storm.wConfigs.get(affectedWorld);
        try {
            ticker = new BlockTickSelector(Bukkit.getWorld(affectedWorld),
                    glob.Acid__Rain_Dissolver_Block__Deterioration__Chance);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

        for (List<String> trans : glob.Acid__Rain_Dissolver_Block__Transformations) {
            transformations.add(new BlockTransformer(new IDBlock(trans.get(0)), new IDBlock(trans.get(1))));
        }

    }

    /**
     * Starts the task.
     */

    public void run() {
        id = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                        storm,
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (Block b : ticker.getRandomTickedBlocks()) {
                                        Block tran = b.getRelative(BlockFace.DOWN);
                                        if (tran.getTypeId() != 0 && Storm.util.isRainy(tran.getBiome()) && !Storm.util.isBlockProtected(tran)) {
                                            for (BlockTransformer t : transformations) {
                                                if (t.transform(tran))
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    ErrorLogger.generateErrorLog(e);
                                }
                            }
                        },
                        0,
                        glob.Acid__Rain_Scheduler_Dissolver__Calculation__Intervals__In__Ticks);

    }

    /**
     * Ends the task.
     */

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

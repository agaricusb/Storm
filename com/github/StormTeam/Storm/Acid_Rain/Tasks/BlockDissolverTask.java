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
 * @author Icyene
 */

public class BlockDissolverTask implements Runnable {

    private int id;
    private final Storm storm;
    private final GlobalVariables glob;
    private BlockTickSelector ticker;

    private final List<BlockTransformer> transformations = new ArrayList<BlockTransformer>();

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
                    glob.Acid__Rain_Dissolver_Block__Deterioration__Chance, glob.Acid__Rain_Dissolver_Block__Deterioration__Area);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

        for (List<String> trans : glob.Acid__Rain_Dissolver_Block__Transformations) {
            transformations.add(new BlockTransformer(new IDBlock(trans.get(0)), new IDBlock(trans.get(1))));
        }

    }

    @Override
    public void run() {
        try {
            for (Block b : ticker.getRandomTickedBlocks()) {
                Block tran = b.getRelative(BlockFace.DOWN);
                if (tran.getTypeId() != 0 && StormUtil.isRainy(tran.getBiome()) && !StormUtil.isBlockProtected(tran)) {
                    for (BlockTransformer t : transformations) {
                        if (t.transform(tran)) {
                            StormUtil.playSoundNearby(tran.getLocation(), 5F, "random.fizz", 1F, 1F);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    /**
     * Starts the task.
     */
    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(storm, this, 0, glob.Acid__Rain_Scheduler_Dissolver__Calculation__Intervals__In__Ticks);
    }

    /**
     * Ends the task.
     */

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

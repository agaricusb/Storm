package com.github.StormTeam.Storm.Acid_Rain.Tasks;

import com.github.StormTeam.Storm.BlockTickSelector;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class DissolverTask {

    private int id;
    private Storm storm;
    private GlobalVariables glob;
    private BlockTickSelector ticker;

    public DissolverTask(Storm storm, String affectedWorld) {
        this.storm = storm;
        glob = Storm.wConfigs.get(affectedWorld);
        try {
            ticker = new BlockTickSelector(Bukkit.getWorld(affectedWorld),
                    glob.Acid__Rain_Dissolver_Block__Deterioration__Chance);
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
                                    ArrayList<Block> bloks = ticker
                                            .getRandomTickedBlocks();

                                    for (Block b : bloks) {

                                        Block tran = b.getRelative(BlockFace.DOWN);

                                        if (!Storm.util.isBlockProtected(tran)) {
                                            if (Storm.util.isRainy(tran
                                                    .getBiome())
                                                    && tran.getTypeId() != 0) {
                                                Storm.util
                                                        .transform(
                                                                tran,
                                                                glob.Acid__Rain_Dissolver_Block__Transformations);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        0,
                        glob.Acid__Rain_Scheduler_Dissolver__Calculation__Intervals__In__Ticks);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

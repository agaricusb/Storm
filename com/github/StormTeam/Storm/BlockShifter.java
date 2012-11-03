package com.github.StormTeam.Storm;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class BlockShifter {

    public static void syncSetBlock(final Block b, final int id) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                b.setTypeId(id);
            }
        }, 0L);
    }

    public static void syncSetBlockDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    b.setTypeId(id);
            }
        }, delay);
    }
}

package com.github.StormTeam.Storm;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

public class BlockShifter {

    public static void syncSetBlock(final Block b, final int id) {
        Future<Boolean> callBlockChange = Bukkit.getScheduler().callSyncMethod(Storm.instance,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return b.setTypeId(id);
                    }
                }
        );
        try {
            callBlockChange.get();
        } catch (CancellationException ignored) {
            //This will be thrown when the server shuts down while volcano is running. Don't scare the user!
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    public static void syncSetBlockDelayed(final Block b, final int id, long delay) {
        final int pre = b.getTypeId();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                if (pre == b.getTypeId())
                    syncSetBlock(b, id);
            }
        }, delay);
    }
}

package com.github.StormTeam.Storm.Earthquake.Listeners;

import com.github.StormTeam.Storm.Earthquake.Quake;
import com.github.StormTeam.Storm.Earthquake.QuakeUtil;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

public class BlockListener implements Listener {

    private final Quake quake;
    private final Storm storm;

    public BlockListener(Quake quake, Storm storm) {
        this.quake = quake;
        this.storm = storm;
    }

    public void forget() {
        BlockPlaceEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        // Don't target creative mode players!
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (!quake.isQuaking(e.getBlock().getLocation()))
            return;

        final Block b = e.getBlock();
        if (Storm.util.isBlockProtected(b))
            return;

        if (QuakeUtil.isBounceable(b))
            return;

        final FallingBlock fB = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        fB.setDropItem(true);

        // Avoid block duplication by removing the placed block a tick later
        Bukkit.getScheduler().scheduleSyncDelayedTask(storm, new Runnable() {

            @Override
            public void run() {
                b.setType(Material.AIR);
                fB.setVelocity(new Vector(Math.random() - 0.5, 0.3, Math.random() - 0.5));
            }
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // Don't target creative mode players!
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || !quake.isQuaking(e.getBlock().getLocation()))
            return;

        final Block b = e.getBlock();
        if (Storm.util.isBlockProtected(b) || QuakeUtil.isBounceable(b))
            return;

        FallingBlock fB = e.getPlayer().getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        fB.setDropItem(true);
        b.setTypeId(0);
        fB.setVelocity(new Vector(Math.random() - 0.5, 0.3, Math.random() - 0.5));
    }
}

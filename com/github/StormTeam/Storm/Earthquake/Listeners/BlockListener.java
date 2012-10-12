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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

public class BlockListener implements Listener {

    private Quake q;
    private Storm storm;

    public BlockListener(Quake q, Storm storm) {
        this.q = q;
        this.storm = storm;
    }

    public void forget() {
        BlockPlaceEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        // Don't target creative mode players!
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (!q.isQuaking(e.getBlock().getLocation()))
            return;

        //e.setCancelled(true);

        final Block b = e.getBlock();
        if (Storm.util.isBlockProtected(b))
            return;

        if (QuakeUtil.isBounceable(b))
            return;

        final FallingBlock fB = e.getPlayer().getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        fB.setDropItem(true);

        // Avoid block duplication by removing the placed block a tick later
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(storm, new Runnable() {

            @Override
            public void run() {

                float x = ((float) Storm.random.nextInt(10) - 5F) / 10F;
                float z = ((float) Storm.random.nextInt(10) - 5F) / 10F;

                b.setType(Material.AIR);
                fB.setVelocity(new Vector(x, 0.3F, z));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        // Don't target creative mode players!
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (!q.isQuaking(e.getBlock().getLocation()))
            return;

        //e.setCancelled(true);

        final Block b = e.getBlock();
        if (Storm.util.isBlockProtected(b))
            return;

        if (QuakeUtil.isBounceable(b))
            return;

        FallingBlock fB = e.getPlayer().getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        fB.setDropItem(true);

        float x = ((float) Storm.random.nextInt(10) - 5F) / 10F;
        float z = ((float) Storm.random.nextInt(10) - 5F) / 10F;

        b.setType(Material.AIR);
        fB.setVelocity(new Vector(x, 0.3F, z));
    }

}

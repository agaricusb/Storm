package com.github.StormTeam.Storm.Wildfire.Listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Wildfire.Wildfire;
import static com.github.StormTeam.Storm.Wildfire.Wildfire.wildfireBlocks;

public class WildfireListeners implements Listener {
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (!event.getCause().equals(IgniteCause.SPREAD)) {
            return;
        }
        Location loc = event.getBlock().getLocation();
        String name = loc.getWorld().getName();

        if (!Storm.wConfigs.containsKey(name)) {
            return;
        }
        GlobalVariables glob = Storm.wConfigs.get(name);

        if (GetWFBlocks(name).size() < glob.Natural__Disasters_Maximum__Fires) {
            final int radiuski = glob.Natural__Disasters_Wildfires_Scan__Radius;
            for (int x = -radiuski; x <= radiuski; ++x) {
                for (int y = -radiuski; y <= radiuski; ++y) {
                    for (int z = -radiuski; z <= radiuski; ++z) {
                        if (GetWFBlocks(name).contains(
                                new Location(w, x + loc.getX(), y
                                + loc.getY(), z + loc.getZ())
                                .getBlock())) {
                            scanForIgnitables(loc, w, radiuski,
                                    glob.Natural__Disasters_Wildfires_Spread__Limit);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockEx(final BlockFadeEvent event) {
        GetWFBlocks(faded.getWorld().getName()).remove(event.getBlock());    }

    private void scanForIgnitables(final Location loc, final World w,
            final int radiuski, final int spreadLimit) {
        Block block, block2;
        int spread = 0;

        for (int x = -radiuski; x <= radiuski; ++x) {
            for (int y = -radiuski; y <= radiuski; ++y) {
                for (int z = -radiuski; z <= radiuski; ++z) {
                    block = w.getBlockAt(loc.getBlockX() + x, loc.getBlockX() + y, loc.getBlockX() + z);

                    if (block.getTypeId() != 0) {
                        continue;
                    }

                    // Tries to burn all blocks with one face touching `block` and `block` itself
                    for (int i = -1; i < 6; ++i) {
                        if (spread < spreadLimit) {
                            block2 = block.getRelative(
                                    i >> 1 == 0 ? ((i & 1) == 0 ? 1 : -1) : 0,
                                    i >> 1 == 1 ? ((i & 1) == 0 ? 1 : -1) : 0,
                                    i >> 1 == 2 ? ((i & 1) == 0 ? 1 : -1) : 0);
                            burn(block2);
                            ++spread;
                        }
                    }
                }
            }
        }
    }

    public void burn(final Block toBurn) {
        if (!canBurn(toBurn)) {
            return;
        }

        GetWFBlocks(toBurn.getWorld().getName()).add(toBurn);
    }

    public boolean canBurn(Block toCheck) {
        return Wildfire.flammable.contains(toCheck.getTypeId());
    }
}

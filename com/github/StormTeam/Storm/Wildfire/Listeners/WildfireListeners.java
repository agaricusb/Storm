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
import static com.github.StormTeam.Storm.Wildfire.Wildfire.getWFBlocks;

public class WildfireListeners implements Listener {

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (!event.getCause().equals(IgniteCause.SPREAD)) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        int ox = (int) loc.getX(), oy = (int) loc.getY(), oz = (int) loc.getZ();
        World world = loc.getWorld();
        String name = world.getName();

        if (!Storm.wConfigs.containsKey(name)) {
            return;
        }
        GlobalVariables glob = Storm.wConfigs.get(name);

        if (getWFBlocks(name).size() < glob.Natural__Disasters_Maximum__Fires) {
            final int radiuski = glob.Natural__Disasters_Wildfires_Scan__Radius;
            for (int x = -radiuski; x <= radiuski; ++x) {
                for (int y = -radiuski; y <= radiuski; ++y) {
                    for (int z = -radiuski; z <= radiuski; ++z) {
                        if (getWFBlocks(name).contains(
                                new Location(world, x + ox, y
                                + oy, z + oz)
                                .getBlock())) {
                            scanForIgnitables(loc, world, radiuski,
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
        Block faded = event.getBlock();
        getWFBlocks(faded.getWorld().getName()).remove(faded);
    }

    private void scanForIgnitables(Location loc, World w, int radiuski, int spreadLimit) {
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

        getWFBlocks(toBurn.getWorld().getName()).add(toBurn);
    }

    public boolean canBurn(Block toCheck) {
        return Wildfire.flammable.contains(toCheck.getTypeId());
    }
}

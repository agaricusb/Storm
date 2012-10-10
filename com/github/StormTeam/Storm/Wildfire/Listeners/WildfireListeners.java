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
        World world = loc.getWorld();
        int ox = (int) loc.getX(), oy = (int) loc.getY(), oz = (int) loc.getZ();
        String name = loc.getWorld().getName();

        GlobalVariables glob;

        if (!Storm.wConfigs.containsKey(name)) {
            return;
        }
        glob = Storm.wConfigs.get(name);

        if (wildfireBlocks.containsKey(name)
                && (wildfireBlocks.get(name).size() < glob.Natural__Disasters_Maximum__Fires)) {

            boolean doScan = false;

            int radiuski = glob.Natural__Disasters_Wildfires_Scan__Radius;

            for (int x = -radiuski; x <= radiuski; x++) {
                for (int y = -radiuski; y <= radiuski; y++) {
                    for (int z = -radiuski; z <= radiuski; z++) {
                        if (wildfireBlocks.containsKey(name) && wildfireBlocks.get(name).contains(
                                new Location(world, x + ox, y + oy /*OY!*/, z + oz).getBlock())) {

                            doScan = true;
                        }
                    }
                }
            }

            if (doScan) {
                scanForIgnitables(loc, world, radiuski, glob.Natural__Disasters_Wildfires_Spread__Limit);
            }
        }

    }

    @EventHandler
    public void onBlockEx(final BlockFadeEvent event) {
        Block faded = event.getBlock();
        String name = faded.getWorld().getName();

        if (wildfireBlocks.containsKey(name)) {
            wildfireBlocks.get(name).remove(faded);
        }

    }

    private void scanForIgnitables(final Location loc, final World w,
            int radiuski, int spreadLimit) {
        Block bR;
        int C = 0;

        for (int x = -radiuski; x <= radiuski; x++) {
            for (int y = -radiuski; y <= radiuski; y++) {
                for (int z = -radiuski; z <= radiuski; z++) {

                    bR = w.getBlockAt((int) loc.getX() + x,
                            (int) loc.getY() + y, (int) loc.getZ() + z);

                    if (bR.getTypeId() != 0) {
                        continue;
                    }

                    bR = bR.getRelative(0, -1, 0);

                    if (canBurn(bR) && (C < spreadLimit)) {
                        burn(bR);
                        C++;
                    }

                    bR = bR.getRelative(-1, 0, 0);

                    if (canBurn(bR) && (C < spreadLimit)) {
                        burn(bR);
                        C++;
                    }

                    bR = bR.getRelative(1, 0, 0);

                    if (canBurn(bR) && (C < spreadLimit)) {
                        burn(bR);
                        C++;
                    }

                    bR = bR.getRelative(0, 0, -1);

                    if (canBurn(bR) && (C < spreadLimit)) {
                        burn(bR);
                        C++;
                    }

                    bR = bR.getRelative(0, 0, 1);

                    if (canBurn(bR) || (C < spreadLimit)) {
                        burn(bR);
                        C++;
                    }

                    burn(bR);
                    C++;
                }
            }
        }
    }

    public void burn(Block toBurn) {
        if (canBurn(toBurn)) {
            String name = toBurn.getWorld().getName();
            if (wildfireBlocks.containsKey(name)) {
                toBurn.setTypeId(51);
                wildfireBlocks.get(name).add(toBurn);
            }
        }
    }

    public boolean canBurn(Block toCheck) {
        return Wildfire.flammableList.contains(toCheck.getTypeId());
    }
}

package com.github.StormTeam.Storm.Wildfire;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import net.minecraft.server.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A class for loading wildfires.
 */

public class Wildfire {

    /**
     * A HashMap containing world names and blocks that are part of a wildfire.
     */
    public static HashMap<String, Set<org.bukkit.block.Block>> wildfireBlocks = new HashMap<String, Set<org.bukkit.block.Block>>();
    /**
     * A Set of all flammable blocks.
     */
    public static Set<Integer> flammable = StormUtil.asSet(Block.FENCE.id, Block.WOOD.id, Block.WOOD_STAIRS.id,
            Block.WOODEN_DOOR.id, Block.LEAVES.id, Block.BOOKSHELF.id,
            Block.GRASS.id, Block.WOOL.id);

    /**
     * Enables wildfires.
     *
     * @param storm The Storm plugin, used for CommandExecutor registration
     */

    public static void load(Storm storm) {
        try {
            Storm.manager.registerWeather(WildfireWeather.class, "storm_wildfire");

            for (World w : Bukkit.getWorlds()) {
                String name = w.getName();
                GlobalVariables temp = Storm.wConfigs.get(name);
                if (temp.Features_Meteor) {
                    Storm.manager.enableWeatherForWorld("storm_wildfire", name,
                            temp.Natural__Disasters_Wildfires_Chance__To__Start,
                            temp.Natural__Disasters_Wildfires_Wildfire__Base__Interval);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    Player snd = (Player) sender;
                    GlobalVariables glob = Storm.wConfigs.get(snd.getWorld().getName());
                    if (glob.Features_Wildfires) {
                        wildfire(snd.getTargetBlock(null, 0).getLocation());
                    } else {
                        sender.sendMessage("Wildfires not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {

                    if (args[0] != null) {
                        try {
                            if (!Storm.manager.startWeather("storm_wildfire", args[0])) {
                                sender.sendMessage("Wildfires not enabled in specified world or are conflicting with another weather!");
                            }
                            return true;
                        } catch (Exception e) {
                            sender.sendMessage("Wildfires not enabled in specified world or are conflicting with another weather!");
                        }
                    }
                }
                return false;
            }
        };


        storm.getCommand("wildfire").setExecutor(exec);

    }

    private static void wildfire(Location targetLoc) {
        org.bukkit.block.Block fire = targetLoc.getBlock().getRelative(BlockFace.UP);
        fire.setType(Material.FIRE);
        String world = targetLoc.getWorld().getName();
        if (wildfireBlocks.containsKey(world)) {
            wildfireBlocks.get(world).add(fire);
            return;
        }
        getWFBlocks(world).add(fire);
    }

    /**
     * Returns a Set of all fire blocks involved in a wildfire in given world.
     *
     * @param world The World name
     * @return A set of wildfire blocks
     */
    public static Set<org.bukkit.block.Block> getWFBlocks(String world) {
        Set<org.bukkit.block.Block> set = wildfireBlocks.get(world);
        if (set == null) {
            set = new HashSet<org.bukkit.block.Block>();
            wildfireBlocks.put(world, set);
        }
        return set;
    }
}

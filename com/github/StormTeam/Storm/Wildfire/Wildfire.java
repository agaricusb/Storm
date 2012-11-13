package com.github.StormTeam.Storm.Wildfire;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import com.github.StormTeam.Storm.Wildfire.Listeners.WildfireListeners;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
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
    public static final HashMap<String, Set<org.bukkit.block.Block>> wildfireBlocks = new HashMap<String, Set<org.bukkit.block.Block>>();

    /**
     * Enables wildfires.
     */

    public static void load() {
        try {
            Storm.pm.registerEvents(new WildfireListeners(), Storm.instance);
            Storm.manager.registerWeather(WildfireWeather.class, "storm_wildfire");

            for (World w : Bukkit.getWorlds()) {
                loadWorld(w);
            }

            Storm.manager.registerWorldLoadHandler(Wildfire.class.getDeclaredMethod("loadWorld", World.class));
            Storm.commandRegistrator.register(Wildfire.class);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    private static void loadWorld(World world) throws WeatherNotFoundException {
        String name = world.getName();
        GlobalVariables temp = Storm.wConfigs.get(name);
        if (temp.Features_Meteor) {
            Storm.manager.enableWeatherForWorld("storm_wildfire", name,
                    temp.Natural__Disasters_Wildfires_Chance__To__Start,
                    temp.Natural__Disasters_Wildfires_Wildfire__Base__Interval);
        }
    }

    @ReflectCommand.Command(
            name = "wildfire",
            usage = "/<command> [world]",
            permission = "storm.wildfire.command",
            permissionMessage = "You don't have the permission to start a wildfire! No mass destruction for you!",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public static boolean wildfireConsole(CommandSender sender, String world) {
        if (wildfireConsole(world)) {
            sender.sendMessage(ChatColor.RED + "Wildfires are not enabled in specified world or are conflicting with another weather!");
            return true;
        }
        return false;
    }

    @ReflectCommand.Command(
            name = "wildfire",
            permission = "storm.wildfire.command",
            permissionMessage = "You don't have the permission to start a wildfire! No mass destruction for you!",
            sender = ReflectCommand.Sender.PLAYER
    )
    public static boolean wildfirePlayer(Player sender) {
        wildfire(sender.getTargetBlock(null, 0).getLocation());
        return true;
    }

    private static boolean wildfireConsole(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_wildfire")) {
                Storm.manager.stopWeather("storm_wildfire", world);
                return false;
            } else {
                return Storm.manager.startWeather("storm_wildfire", world);
            }
        } catch (Exception ex) {
            return true;
        }
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

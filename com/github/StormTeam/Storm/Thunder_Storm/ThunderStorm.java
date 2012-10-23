package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A class for loading thunder storms.
 */

public class ThunderStorm {

    /**
     * Enables thunder storms.
     */

    public static void load() {

        try {
            Storm.manager.registerWeather(ThunderStormWeather.class, "storm_thunderstorm");

            for (World w : Bukkit.getWorlds()) {
                loadWorld(w);
            }
            Storm.manager.registerWorldLoadHandler(ThunderStorm.class.getDeclaredMethod("loadWorld", World.class));

        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (thunderstorm(((Player) sender).getWorld().getName())) {
                        sender.sendMessage(ChatColor.RED + "Thunderstorms not enabled in specified world or are conflicting with another weather!");
                    }
                } else {
                    if (args.length > 0 && !StringUtils.isEmpty(args[0])) {
                        if (thunderstorm(args[0])) {
                            sender.sendMessage(ChatColor.RED + "Thunderstorms not enabled in specified world or are conflicting with another weather!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Must specify world when executing from console!");
                        }

                    }
                }
                return true;
            }
        };
        Storm.instance.getCommand("thunderstorm").setExecutor(exec);
    }

    private static void loadWorld(World world) throws WeatherNotFoundException {
        String name = world.getName();
        GlobalVariables temp = Storm.wConfigs.get(name);
        if (temp.Features_Thunder__Storms_Thunder__Striking) {
            Storm.manager.enableWeatherForWorld("storm_thunderstorm", name,
                    temp.Thunder__Storm_Thunder__Storm__Chance, temp.Thunder__Storm_Thunder__Storm__Base__Interval);
        }
    }

    private static boolean thunderstorm(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_thunderstorm")) {
                Storm.manager.stopWeather("storm_thunderstorm", world);
                return false;
            } else {
                return Storm.manager.startWeather("storm_thunderstorm", world);
            }
        } catch (Exception ex) {
            return true;
        }
    }
}

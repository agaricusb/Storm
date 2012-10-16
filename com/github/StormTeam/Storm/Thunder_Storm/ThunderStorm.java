package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A class for loading thunder storms.
 */

public class ThunderStorm {

    private static Storm storm;

    /**
     * Enables thunder storms.
     *
     * @param storm The Storm plugin, used for CommandExecutor registration
     */

    public static void load(Storm storm) {
        ThunderStorm.storm = storm;

        try {
            Storm.manager.registerWeather(ThunderStormWeather.class, "storm_thunderstorm");

            for (World w : Bukkit.getWorlds()) {
                String name = w.getName();
                GlobalVariables temp = Storm.wConfigs.get(name);
                if (temp.Features_Thunder__Storms) {
                    Storm.manager.enableWeatherForWorld("storm_thunderstorm", name,
                            temp.Thunder__Storm_Thunder__Storm__Chance, temp.Thunder__Storm_Thunder__Storm__Base__Interval);
                }
            }

        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (thunderstorm(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Thunderstorms not enabled in specified world or are conflicting with another weather!");
                    }
                } else {
                    if (args.length > 0 && !StringUtils.isEmpty(args[0])) {
                        if (thunderstorm(args[0])) {
                            sender.sendMessage("Thunderstorms not enabled in specified world or are conflicting with another weather!");
                        } else {
                            sender.sendMessage("Must specify world when executing from console!");
                        }

                    }
                }
                return true;
            }
        };
        ThunderStorm.storm.getCommand("thunderstorm").setExecutor(exec);
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

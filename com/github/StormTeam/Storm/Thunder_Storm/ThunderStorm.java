package com.github.StormTeam.Storm.Thunder_Storm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.GlobalVariables;

/**
 * @author Tudor
 */
public class ThunderStorm {

    public static ArrayList<World> thunderingWorlds = new ArrayList<World>();
    @SuppressWarnings("FieldCanBeLocal")
    private static Storm storm;

    public static void load(Storm ztorm) {
        storm = ztorm;

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
            e.printStackTrace();
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (!thunderstorm(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Thunderstorms not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {
                    if (args[0] != null) {
                        if (!thunderstorm(args[0])) {
                            sender.sendMessage("Thunderstorms not enabled in specified world or are conflicting with another weather!");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        storm.getCommand("thunderstorm").setExecutor(exec);
    }

    public static boolean thunderstorm(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_thunderstorm")) {
                Storm.manager.stopWeather("storm_thunderstorm", world);
                return true;
            } else {                
                return !Storm.manager.startWeather("storm_thunderstorm", world);
            }
        } catch (Exception ex) {
            return false;
        }
    }
}

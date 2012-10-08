package com.github.StormTeam.Storm.Thunder_Storm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.WeatherNotAllowedException;
import com.github.StormTeam.Storm.Weather.WeatherNotFoundException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tudor
 */
public class ThunderStorm {

    public static ArrayList<World> thunderingWorlds = new ArrayList<World>();
    private static Storm storm;
    private static CommandExecutor exec;

    public static void load(Storm ztorm) {
        storm = ztorm;

        try {
            Storm.manager.registerWeather(ThunderStormWeather.class, "storm_thunderstorm", Arrays.asList("world"), 0, 155555);
        } catch (Exception e) {
            e.printStackTrace();
        }

        exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    thunderstorm(((Player) sender).getWorld().getName());
                    return true;
                } else {
                    if (args[0] != null) {
                        thunderstorm(args[0]);
                        return true;
                    }
                }
                return false;
            }
        };
        storm.getCommand("thunderstorm").setExecutor(exec);
    }

    public static void thunderstorm(String world) {
        if (Storm.manager.getActiveWeathers(world).contains("storm_thunderstorm")) {
            try {
                Storm.manager.stopWeather("storm_thunderstorm", world);
            } catch (WeatherNotFoundException ex) {
                Logger.getLogger(ThunderStorm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                try {
                    Storm.manager.startWeather("storm_thunderstorm", world);
                } catch (WeatherNotAllowedException ex) {
                    Logger.getLogger(ThunderStorm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (WeatherNotFoundException ex) {
                Logger.getLogger(ThunderStorm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
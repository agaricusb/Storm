package com.github.StormTeam.Storm.Blizzard;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.GlobalVariables;

public class Blizzard {

    private static CommandExecutor exec;

    public static void load(Storm ztorm) {

        try {
            Storm.manager.registerWeather(BlizzardWeather.class, "storm_blizzard");

            for (World w : Bukkit.getWorlds()) {
                String name = w.getName();
                GlobalVariables temp = Storm.wConfigs.get(name);
                if (temp.Features_Blizzards_Player__Damaging || temp.Features_Blizzards_Slowing__Snow) {
                    Storm.manager.enableWeatherForWorld("storm_blizzard", name,
                            temp.Blizzard_Blizzard__Chance, temp.Blizzard_Blizzard__Base__Interval);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (!blizzard(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Blizzards not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {
                    if (args[0] != null) {
                        if (!blizzard(args[0])) {
                           sender.sendMessage("Blizzards not enabled in specified world or are conflicting with another weather!");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        ztorm.getCommand("blizzard").setExecutor(exec);

    }

    public static boolean blizzard(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_blizzard")) {
                Storm.manager.stopWeather("storm_blizzard", world);
                return true;
            } else {               
                return !Storm.manager.startWeather("storm_blizzard", world);
            }
        } catch (Exception ex) {
            return false;
        }
    }
}
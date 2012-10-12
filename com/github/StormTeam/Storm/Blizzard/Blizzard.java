package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Blizzard {

    public static SnowModder modder;

    /**
     * Enables blizzards.
     *
     * @param storm The Storm plugin, used for CommandExecutor setting
     */

    public static void load(Storm storm) {
        modder = new SnowModder();
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

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (blizzard(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Blizzards not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {
                    if (args[0] != null) {
                        if (blizzard(args[0])) {
                            sender.sendMessage("Blizzards not enabled in specified world or are conflicting with another weather!");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        storm.getCommand("blizzard").setExecutor(exec);

    }

    private static boolean blizzard(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_blizzard")) {
                Storm.manager.stopWeather("storm_blizzard", world);
                return false;
            } else {
                return Storm.manager.startWeather("storm_blizzard", world);
            }
        } catch (Exception ex) {
            return true;
        }
    }
}

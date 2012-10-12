package com.github.StormTeam.Storm.Acid_Rain;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A class for loading acid rain.
 */

public class AcidRain {

    /**
     * Enables acid rain.
     *
     * @param storm The Storm plugin, used for CommandExecutor registration
     */

    public static void load(Storm storm) {
        try {
            Storm.manager.registerWeather(AcidRainWeather.class, "storm_acidrain");

            for (World w : Bukkit.getWorlds()) {
                String name = w.getName();
                GlobalVariables temp = Storm.wConfigs.get(name);
                if (temp.Features_Acid__Rain_Dissolving__Blocks || temp.Features_Acid__Rain_Player__Damaging) {
                    Storm.manager.enableWeatherForWorld("storm_acidrain", name,
                            temp.Acid__Rain_Acid__Rain__Chance, temp.Acid__Rain_Acid__Rain__Base__Interval);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (acidrain(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Acid rain not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {
                    if (args[0] != null) {
                        if (acidrain(args[0])) {
                            sender.sendMessage("Wildfires not enabled in specified world or are conflicting with another weather!");
                        }
                        return true;
                    }
                }
                return false;
            }
        };

        storm.getCommand("acidrain").setExecutor(exec);
    }

    private static boolean acidrain(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_acidrain")) {
                Storm.manager.stopWeather("storm_acidrain", world);
                return false;
            } else {
                return Storm.manager.startWeather("storm_acidrain", world);
            }
        } catch (Exception ex) {
            return true;
        }
    }
}

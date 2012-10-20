package com.github.StormTeam.Storm.Acid_Rain;

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
            ErrorLogger.generateErrorLog(e);
        }
        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    if (acidrain(((Player) sender).getWorld().getName())) {
                        sender.sendMessage("Acid rain not enabled in specified world or are conflicting with another weather!");
                    }
                } else {
                    if (args.length > 0 && !StringUtils.isEmpty(args[0])) {
                        if (acidrain(args[0])) {
                            sender.sendMessage("Acid rain not enabled in specified world or are conflicting with another weather!");
                        }
                    } else {
                        sender.sendMessage("Must specify world when executing from console!");
                    }
                }
                return true;
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
            ex.printStackTrace();
            return true;
        }

    }
}

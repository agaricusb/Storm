package com.github.StormTeam.Storm.Blizzard;

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
 * A class for loading blizzard.
 */

public class Blizzard {

    /**
     * The SnowModder object used for modding snow to SnowLayer.
     */
    public static SnowModder modder;

    /**
     * Enables blizzard.
     *
     * @param storm The Storm plugin, used for CommandExecutor registration
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
                } else {
                    if (!StringUtils.isEmpty(args[0])) {
                        if (blizzard(args[0])) {
                            sender.sendMessage("Blizzards not enabled in specified world or are conflicting with another weather!");
                        }
                    } else {
                        sender.sendMessage("Must specify world when executing from console!");
                    }
                }
                return true;
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

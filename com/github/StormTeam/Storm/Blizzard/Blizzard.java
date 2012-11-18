package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
     */

    public static void load() {
        modder = new SnowModder();
        try {
            Storm.manager.registerWeather(BlizzardWeather.class, "storm_blizzard");

            for (World w : Bukkit.getWorlds()) {
                loadWorld(w);
            }
            Storm.manager.registerWorldLoadHandler(Blizzard.class.getDeclaredMethod("loadWorld", World.class));
            Storm.commandRegistrator.register(Blizzard.class);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

    }

    private static void loadWorld(World world) throws WeatherNotFoundException {
        String name = world.getName();
        WorldVariables temp = Storm.wConfigs.get(name);
        if (temp.Features_Blizzards_Player__Damaging || temp.Features_Blizzards_Slowing__Snow) {
            Storm.manager.enableWeatherForWorld("storm_blizzard", name,
                    temp.Blizzard_Blizzard__Chance, temp.Blizzard_Blizzard__Base__Interval);
        }
    }

    @ReflectCommand.Command(
            name = "blizzard",
            usage = "/<command> [world]",
            permission = "storm.blizzard.command",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public static boolean blizzardConsole(CommandSender sender, String world) {
        if (blizzard(world)) {
            sender.sendMessage(ChatColor.RED + "Blizzards are not enabled in specified world or are conflicting with another weather!");
            return true;
        }
        return false;
    }

    @ReflectCommand.Command(
            name = "blizzard",
            permission = "storm.blizzard.command",
            sender = ReflectCommand.Sender.PLAYER
    )
    public static boolean blizzardPlayer(Player sender) {
        if (blizzard((sender).getWorld().getName())) {
            sender.sendMessage(ChatColor.RED + "Blizzards are not enabled in specified world or are conflicting with another weather!");
            return true;
        }
        return false;
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

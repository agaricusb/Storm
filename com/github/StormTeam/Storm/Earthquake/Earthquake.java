package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Earthquake {

    public static void load() {
        try {
            for (World w : Bukkit.getWorlds()) {
                loadWorld(w);
            }
            Storm.manager.registerWorldLoadHandler(Earthquake.class.getDeclaredMethod("loadWorld", World.class));
            Storm.commandRegistrator.register(Earthquake.class);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    private static void loadWorld(World world) throws WeatherNotFoundException {
        String name = world.getName();
        WorldVariables temp = Storm.wConfigs.get(name);
        if (temp.Weathers__Enabled_Natural__Disasters_Earthquakes) {
            Storm.manager.enableWeatherForWorld("storm_earthquake", name,
                    temp.Natural__Disasters_Earthquakes_Chance__To__Start, temp.Natural__Disasters_Earthquakes_Earthquake__Base__Interval);
        }
    }

    @ReflectCommand.Command(
            name = "earthquake",
            alias = "quake",
            permission = "storm.earthquake.command"
    )
    public static boolean quake(Player sender) {
        EarthquakeControl.loadQuake(sender.getLocation(), 4);
        return true;
    }

    @ReflectCommand.Command(
            name = "earthquake",
            alias = "quake",
            usage = "/<command> [magnitude]",
            permission = "storm.earthquake.command"
    )
    public static boolean quake(Player sender, String magnitude) {
        EarthquakeControl.loadQuake(sender.getLocation(), Integer.parseInt(magnitude));
        return true;
    }

    @ReflectCommand.Command(
            name = "crack",
            usage = "/<command>",
            permission = "storm.earthquake.crack.command"
    )
    public static boolean crack(Player p) {
        EarthquakeControl.crack(p.getTargetBlock(null, 200).getLocation(), 90, 10, 60);
        return true;
    }

    @ReflectCommand.Command(
            name = "crack",
            usage = "/<command> [length] [width] [depth]",
            permission = "storm.earthquake.crack.command"
    )
    public static boolean crack(Player p, String length, String width, String depth) {
        EarthquakeControl.crack(p.getTargetBlock(null, 200).getLocation(), Integer.parseInt(length), Integer.parseInt(width), Integer.parseInt(depth));
        return true;
    }
}

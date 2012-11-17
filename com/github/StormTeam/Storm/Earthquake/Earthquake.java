package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.entity.Player;

public class Earthquake {

    public static void load() {
        Storm.commandRegistrator.register(Earthquake.class);
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

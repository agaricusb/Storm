package com.github.StormTeam.Storm.Volcano;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.Tasks.LifeTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;

public class Volcano {
    public static File vulkanos;

    public static void load() {
        try {
            vulkanos = new File(Storm.instance.getDataFolder() + File.separator + "volcanoes.bin");
            if (vulkanos.exists() || vulkanos.createNewFile())
                VolcanoControl.load(vulkanos);
            Storm.commandRegistrator.register(Volcano.class);
            new LifeTask().start();
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    @ReflectCommand.Command(
            name = "volcano",
            alias = {"firemountain", "vulkano"},
            usage = "/<command>",
            permission = "storm.volcano.command",
            sender = ReflectCommand.Sender.PLAYER
    )
    public static boolean volcano(Player p) {
        volcano(p.getTargetBlock(null, 0).getLocation(), 90);
        return true;
    }

    @ReflectCommand.Command(
            name = "volcano",
            alias = {"firemountain", "vulkano"},
            usage = "/<command>",
            permission = "storm.volcano.command",
            sender = ReflectCommand.Sender.PLAYER
    )
    public static boolean volcano(Player p, String radius) {
        volcano(p.getTargetBlock(null, 200).getLocation(), Integer.parseInt(radius));
        return true;
    }

    public static void volcano(Location loc, int radius) {
        VolcanoWorker volcano = new VolcanoWorker(loc, radius, 0);
        volcano.start();
    }
}

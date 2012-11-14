package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Earthquake {

    static List<Quake> quakes = new ArrayList<Quake>();

    public static void load() {
        Storm.commandRegistrator.register(Earthquake.class);
    }

    @ReflectCommand.Command(
            name = "earthquake",
            alias = "quake",
            permission = "storm.earthquake.command"
    )
    public static boolean quake(Player sender) {
        Location l = sender.getLocation().clone();
        Location elk = l.clone();
        l.add(40, 0, 40);
        if (!Earthquake.isQuaked(l)) {
            elk.subtract(40, 0, 40);
            if (!isQuaked(elk)) {
                loadQuake(l, elk);
                return true;
            }
        }
        return false;
    }

    public static int loadQuake(Location one, Location two) {
        quakes.add(new Quake(Storm.instance, quakes.size(), one, two));
        return quakes.size() - 1;
    }

    public static boolean isQuaked(Player p) {
        return isQuaked(p.getLocation());
    }

    public static boolean isQuaked(Location location) {
        for (Quake quake : quakes) {
            if (quake == null || (!quake.isLoading && !quake.isRunning))
                continue;
            if (quake.isQuaking(location))
                return true;
        }
        return false;
    }

    public static void stopQuake(int id) {
        Quake quake = quakes.get(id);
        if (quake == null)
            return;
        quake.stop();
        quakes.set(id, null);
    }

    public static boolean isBounceable(Block b) {
        switch (b.getType()) {
            case BED_BLOCK:

            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:

            case FIRE:

            case GRASS:

            case IRON_DOOR_BLOCK:

            case LADDER:
            case LAVA:
            case LEVER:

            case PAINTING:
            case PISTON_BASE:
            case PISTON_STICKY_BASE:
            case POWERED_RAIL:

            case SAPLING:
            case SIGN_POST:
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
            case STONE_BUTTON:
            case STONE_PLATE:

            case TORCH:
            case TRAP_DOOR:
            case TRIPWIRE:
            case TRIPWIRE_HOOK:

            case RAILS:
            case REDSTONE_WIRE:
            case REDSTONE_TORCH_ON:

            case WALL_SIGN:
            case WATER:
            case WOOD_PLATE:
            case WOODEN_DOOR:
                return true;
            default:
                return false;
        }
    }
}

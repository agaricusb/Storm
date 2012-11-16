package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Cuboid;
import com.github.StormTeam.Storm.Math.Cracker;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Verbose;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

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

    @ReflectCommand.Command(name = "crack", usage = "/<command>")
    public static boolean crack(Player p) {
        crack(p.getTargetBlock(null, 200).getLocation(), 90, 10, 60);
        return true;
    }

    @ReflectCommand.Command(name = "crack", usage = "/<command>")
    public static boolean crack(Player p, String length, String width, String depth) {
        crack(p.getTargetBlock(null, 200).getLocation(), Integer.parseInt(length), Integer.parseInt(width), Integer.parseInt(depth));
        return true;
    }

    public static void crack(Location location, int length, int width, int depth) {
        Cuboid area = new Cuboid(location, location);
        area = area.expand(BlockFace.UP, 256).expand(BlockFace.DOWN, 256);
        area = area.expand(BlockFace.NORTH, length);
        area = area.expand(BlockFace.EAST, length);
        area = area.expand(BlockFace.SOUTH, length);
        area = area.expand(BlockFace.WEST, length);

        Cracker cracker = new Cracker(length, location.getBlockX(), location.getBlockY(), location.getBlockZ(), width, depth);
        cracker.plot();

        World w = location.getWorld();
        for (int i = 0; ; ++i) {
            Verbose.log("Cracking layer " + i);
            List<Vector> layer = cracker.get(i);
            if (layer.size() == 0)
                break;
            for (Vector block : layer) {
                BlockIterator bi = new BlockIterator(w, block, new Vector(0, 1, 0), 0, (256 - block.getBlockY()));
                while (bi.hasNext()) {
                    Block toInspect = bi.next();
                    int id = toInspect.getTypeId();
                    if (id != 0 && id != 7)
                        area.setBlockFast(toInspect, 0);
                    if ((id & 0xFE) == 8) // 8 or 9
                        toInspect.setTypeId(9, true);
                    else if ((id & 0xFE) == 10) // 10 or 11
                        toInspect.setTypeId(10, true);
                }
            }
            Storm.util.playSoundNearby(location, length * width + 500, "ambient.weather.thunder", 3F, Storm.random.nextInt(3) + 1);
            area.sendClientChanges();
            area.loadChunks();
        }
    }
}

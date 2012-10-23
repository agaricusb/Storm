package com.github.StormTeam.Storm.Meteors;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Meteors.Entities.EntityMeteor;
import com.github.StormTeam.Storm.Meteors.Listener.SafeExplosion;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * A class for loading meteors.
 */

public class Meteor {

    public static Set<Integer> meteors = new HashSet();

    /**
     * Enables meteors.
     */

    public static void load() {

        try {
            Storm.pm.registerEvents(new SafeExplosion(), Storm.instance);
            patchMeteor();
            Storm.manager.registerWeather(MeteorWeather.class, "storm_meteor");

            for (World w : Bukkit.getWorlds()) {
                loadWorld(w);
            }
            Storm.manager.registerWorldLoadHandler(Meteor.class.getDeclaredMethod("loadWorld", World.class));

        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if (sender instanceof Player) {
                    Player snd = (Player) sender;
                    GlobalVariables glob = Storm.wConfigs.get(snd.getWorld().getName());
                    if (glob.Features_Meteor) {
                        Location senderLocation = snd.getLocation();
                        trajectoryMeteor(snd.getTargetBlock(null, 0).getLocation(),
                                senderLocation.toVector().add(senderLocation.getDirection().normalize()).toLocation(senderLocation.getWorld()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Meteors not enabled in specified world or are conflicting with another weather!");
                    }
                } else {

                    if (args.length > 0 && !StringUtils.isEmpty(args[0])) {
                        if (consoleMeteor(args[0])) {
                            sender.sendMessage(ChatColor.RED + "Meteors not enabled in specified world or are conflicting with another weather!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Must specify world when executing from console!");
                    }
                }
                return true;
            }
        };

        Storm.instance.getCommand("meteor").setExecutor(exec);
    }

    private static void loadWorld(World world) throws WeatherNotFoundException {
        String name = world.getName();
        GlobalVariables temp = Storm.wConfigs.get(name);
        if (temp.Features_Meteor) {
            Storm.manager.enableWeatherForWorld("storm_meteor", name,
                    temp.Natural__Disasters_Meteor_Chance__To__Spawn,
                    temp.Natural__Disasters_Meteor_Meteor__Base__Interval);
        }
    }

    private static boolean consoleMeteor(String world) {
        try {
            if (Storm.manager.getActiveWeathers(world).contains("storm_meteor")) {
                Storm.manager.stopWeather("storm_meteor", world);
                return false;
            } else {
                return Storm.manager.startWeather("storm_meteor", world);
            }
        } catch (Exception ex) {
            return true;
        }
    }

    private static void patchMeteor() {
        try {
            Method a = net.minecraft.server.EntityTypes.class
                    .getDeclaredMethod("a", Class.class, String.class, int.class);
            a.setAccessible(
                    true);
            a.invoke(a, EntityMeteor.class, "StormMeteor", 12);

        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    private static void trajectoryMeteor(Location targetLoc, Location spawnLoc) {
        net.minecraft.server.WorldServer mcWorld = ((CraftWorld) (spawnLoc.getWorld())).getHandle();

        GlobalVariables glob = Storm.wConfigs.get(mcWorld.getWorld().getName());
        EntityMeteor mm = new EntityMeteor(
                mcWorld, 15, 15, 15, 60, 100,
                glob.Natural__Disasters_Meteor_Messages_On__Meteor__Crash, 9,
                glob.Natural__Disasters_Meteor_Shockwave_Damage__Radius,
                glob.Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave, 0, false, false, 0);

        mm.spawn();

        Fireball meteor = (Fireball) mm.getBukkitEntity();
        meteor.teleport(spawnLoc);
        meteor.setDirection(targetLoc.toVector().subtract(spawnLoc.toVector()));
        meteor.setBounce(false);
        meteor.setIsIncendiary(true);
    }
}

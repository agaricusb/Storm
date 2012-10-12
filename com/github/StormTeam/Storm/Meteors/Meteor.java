package com.github.StormTeam.Storm.Meteors;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Meteors.Entities.EntityMeteor;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * A class for loading meteors.
 */

public class Meteor {

    /**
     * Enables meteors.
     *
     * @param storm The Storm plugin, used for CommandExecutor registration
     */

    public static void load(Storm storm) {

        try {
            Storm.manager.registerWeather(MeteorWeather.class, "storm_meteor");

            for (World w : Bukkit.getWorlds()) {
                String name = w.getName();
                GlobalVariables temp = Storm.wConfigs.get(name);
                if (temp.Features_Meteor) {
                    Storm.manager.enableWeatherForWorld("storm_meteor", name,
                            temp.Natural__Disasters_Meteor_Chance__To__Spawn,
                            temp.Natural__Disasters_Meteor_Meteor__Base__Interval);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        CommandExecutor exec = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                if ((sender instanceof Player)) {
                    Player snd = (Player) sender;
                    GlobalVariables glob = Storm.wConfigs.get(snd.getWorld().getName());
                    if (glob.Features_Meteor) {
                        Location senderLocation = snd.getLocation();
                        trajectoryMeteor(snd.getTargetBlock(null, 0).getLocation(),
                                senderLocation.toVector().add(senderLocation.getDirection().normalize()).toLocation(senderLocation.getWorld()));
                    } else {
                        sender.sendMessage("Meteors not enabled in specified world or are conflicting with another weather!");
                    }
                    return true;
                } else {

                    if (args[0] != null) {
                        try {
                            if (!Storm.manager.startWeather("storm_meteor", args[0])) {
                                sender.sendMessage("Meteors not enabled in specified world or are conflicting with another weather!");
                            }
                            return true;

                        } catch (Exception e) {
                            sender.sendMessage("Meteors not enabled in specified world or are conflicting with another weather!");
                        }
                    }
                }
                return false;
            }
        };


        storm.getCommand("meteor").setExecutor(exec);
    }

    private static void patchMeteor() {
        try {
            Method a = net.minecraft.server.EntityTypes.class
                    .getDeclaredMethod("a", Class.class, String.class, int.class);
            a.setAccessible(
                    true);
            a.invoke(a, EntityMeteor.class, "Fireball", 12);

        } catch (Exception e) {
            Storm.util.log(Level.SEVERE, "Failed to create meteor entity!");
        }
    }

    private static void trajectoryMeteor(Location targetLoc, Location spawnLoc) {
        net.minecraft.server.WorldServer mcWorld = ((CraftWorld) (spawnLoc.getWorld())).getHandle();

        GlobalVariables glob = Storm.wConfigs.get(mcWorld.getWorld().getName());
        EntityMeteor mm = new EntityMeteor(
                mcWorld, 15, 15, 15, 60, 100,
                glob.Natural__Disasters_Meteor_Messages_On__Meteor__Crash, 9,
                glob.Natural__Disasters_Meteor_Shockwave_Damage__Radius,
                glob.Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave, 0, false, 0);

        mm.spawn();

        Fireball meteor = (Fireball) mm.getBukkitEntity();
        meteor.teleport(spawnLoc);
        meteor.setDirection(targetLoc.toVector().subtract(spawnLoc.toVector()));
        meteor.setBounce(false);
        meteor.setIsIncendiary(true);
    }
}

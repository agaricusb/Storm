package com.github.StormTeam.Storm;

import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EntityShelteringTask {

    private int id;
    private World affectedWorld;
    private Storm storm;
    private Field selector;
    private Method register;
    private ArrayList<Integer> registered = new ArrayList<Integer>();
    private Set<Biome> filter = new HashSet();
    private String name;

    public EntityShelteringTask(Storm storm, String affectedWorld, String name, Set<Biome> biomeFilter) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(affectedWorld);
        this.name = name;
        this.filter = biomeFilter;
        try {
            selector = EntityLiving.class.getDeclaredField("goalSelector");
            register = PathfinderGoalSelector.class.getDeclaredMethod("a", int.class, PathfinderGoal.class);
            selector.setAccessible(true);
            register.setAccessible(true);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

    }

    public void run() {
        id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(storm, new Runnable() {
            public void run() {
                try {
                    for (Entity en : affectedWorld.getEntities()) {
                        net.minecraft.server.Entity notchMob = ((CraftEntity) en).getHandle();
                        if (notchMob instanceof EntityLiving) {
                            if (!(notchMob instanceof EntityPlayer) &&
                                    !(notchMob instanceof EntitySlime)) {
                                int eid = en.getEntityId();
                                if (!registered.contains(eid) && filter.contains(en.getLocation().getBlock().getBiome())) {
                                    register.invoke(selector.get(notchMob), 1, new PathfinderGoalFleeSky((EntityCreature) notchMob, 0.25F, name));
                                    registered.add(eid);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    ErrorLogger.generateErrorLog(e);
                }
            }
        }
                , 0, 80);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

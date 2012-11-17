package com.github.StormTeam.Storm;

import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EntityShelterer implements Runnable {

    private int id;
    private final World affectedWorld;
    private final Storm storm;
    private Field selector;
    private Method register;
    private final ArrayList<Integer> registered = new ArrayList<Integer>();
    private Set<Biome> filter = new HashSet();
    private final Set<Class<?>> filteredEntities = new HashSet() {{
        add(EntityPlayer.class);
        add(EntitySlime.class);
        add(EntityEnderDragon.class);
        if (Storm.version > 1.3) {
            add(EntityBat.class);
            add(EntityWitch.class);
        }
    }};
    private final String name;
    private Method vec3DCreate;


    public EntityShelterer(Storm storm, String affectedWorld, String name, Set<Biome> biomeFilter) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(affectedWorld);
        this.name = name;
        this.filter = biomeFilter;
        try {
            if (Storm.version == 1.2)
                vec3DCreate = Vec3D.class.getDeclaredMethod("b", double.class, double.class, double.class);
            selector = EntityLiving.class.getDeclaredField("goalSelector");
            register = PathfinderGoalSelector.class.getDeclaredMethod("a", int.class, PathfinderGoal.class);
            selector.setAccessible(true);
            register.setAccessible(true);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    @Override
    public void run() {
        try {
            for (Entity en : affectedWorld.getEntities()) {
                net.minecraft.server.Entity notchMob = ((CraftEntity) en).getHandle();
                if (notchMob instanceof EntityLiving) {
                    if (!filteredEntities.contains(notchMob.getClass())) {
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

    public void start() {
        id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(storm, this, 0, 80);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

    public class PathfinderGoalFleeSky extends PathfinderGoal {

        private final EntityCreature entity;
        private double x;
        private double y;
        private double z;
        private final float speed;
        private final net.minecraft.server.World world;
        private final String name;

        public PathfinderGoalFleeSky(EntityCreature creature, float fast, String weather) {
            entity = creature;
            speed = fast;
            world = creature.world;
            name = weather;
            setMutexBits(1);
        }

        //1.3/1.2 check if can start
        public boolean a() {
            return setup();
        }

        //1.4/1.3/1.2 checks if can continue
        public boolean b() {
            return canEnd();
        }

        //Check if can move or can start. MC 1.2 compatibility.
        //1.2, e is if it can continue
        //1.3, e is to start
        //Nasty workaround
        public boolean e() {
            if (Storm.version == 1.2)
                return canEnd();
            else if (Storm.version == 1.3) {
                start();
                return true;
            }
            return false;
        }

        //1.3/1.2 starting/ending
        public void c() {
            if (Storm.version == 1.2) {
                start();
                return;
            }
            end();
        }

        private void setMutexBits(int bit) {
            a(1); //Sets the mutex bits in PathfinderGoal
        }

        private boolean canEnd() {
            for (org.bukkit.entity.Entity en : entity.getBukkitEntity().getNearbyEntities(3, 3, 3)) {
                if (en instanceof Player) {
                    if (((Player) en).getGameMode() != GameMode.CREATIVE)
                        return true;
                }
            }
            return !Storm.manager.getActiveWeathers(world.getWorld().getName()).contains(name);
        }

        private void start() {
            if (isUnderSky() && (((Storm.version == 1.3 || Storm.version == 1.4) && entity.getNavigation().f()) || (Storm.version == 1.2 && entity.getNavigation().e()))) {
                entity.getNavigation().a(x, y, z, speed);
                entity.getNavigation().d(true);
            }
        }

        private void end() {
            if (setup())
                start();
            else
                entity.getNavigation().d(false);
        }

        private boolean setup() {
            try {
                if (!Storm.manager.getActiveWeathers(world.getWorld().getName()).contains(name) || !isUnderSky())
                    return false;
                Vec3D path = getPathToShelter();
                if (path == null) {
                    return false;
                }
                setup(path);
                return true;
            } catch (Exception e) {
                ErrorLogger.generateErrorLog(e);
                return false;
            }
        }

        private void setup(Vec3D path) {

            if (Storm.version <= 1.3) {
                x = (Integer) ((Object) path.a);
                y = (Integer) ((Object) path.b);
                z = (Integer) ((Object) path.c);
                Verbose.log("(1.3)Moving entity to " + x + "|" + y + "|" + z + ".");
                return;
            }

            //NASTY!
            x = (int) path.c;
            y = (int) path.d;
            z = (int) path.e;
            Verbose.log("(1.4)Moving entity to " + x + "|" + y + "|" + z + ".");

        }

        private Vec3D getPathToShelter() throws InvocationTargetException, IllegalAccessException {
            for (int i = 0; i < 20; i++) {
                int px = MathHelper.floor((entity.locX + (double) Storm.random.nextInt(20)) - 10D),
                        py = MathHelper.floor((entity.boundingBox.b + (double) Storm.random.nextInt(6)) - 6D),
                        pz = MathHelper.floor((entity.locZ + (double) Storm.random.nextInt(20)) - 10D);
                if (!isUnderSky(world, px, py, pz))
                    if (Storm.version <= 1.3)
                        return (Vec3D) vec3DCreate.invoke(null, px, py, pz);
                    else
                        return world.getVec3DPool().create(px, py, pz);
            }
            return null;
        }

        private boolean isUnderSky() {
            return isUnderSky(world, entity.locX, entity.locY, entity.locZ);
        }

        public boolean isUnderSky(net.minecraft.server.World world, double x, double y, double z) {
            return StormUtil.isLocationUnderSky(new Location(world.getWorld(), x, y, z));
        }
    }
}

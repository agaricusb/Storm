package com.github.StormTeam.Storm;

import net.minecraft.server.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PathfinderGoalFleeSky extends PathfinderGoal {

    private EntityCreature entity;
    private double x;
    private double y;
    private double z;
    private float speed;
    private World world;
    private String name;

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

    //1.3/1.2 checks if can continue
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
        if ((Storm.version == 1.3 && entity.getNavigation().f()) || (Storm.version == 1.2 && entity.getNavigation().e())) {
            entity.getNavigation().a(x, y, z, speed);
            entity.getNavigation().d(true);
            return;
        }
    }

    private void end() {
        if (setup())
            start();
        else
            entity.getNavigation().d(false);
    }

    private boolean setup() {
        if (!Storm.manager.getActiveWeathers(world.getWorld().getName()).contains(name) || !isUnderSky())
            return false;
        Vec3D path = getPathToShelter();
        if (path == null) {
            return false;
        } else {
            x = path.a;
            y = path.b;
            z = path.c;
            return true;
        }
    }

    private Vec3D getPathToShelter() {
        for (int i = 0; i < 10; i++) {
            int px = MathHelper.floor((entity.locX + (double) Storm.random.nextInt(20)) - 10D),
                    py = MathHelper.floor((entity.boundingBox.b + (double) Storm.random.nextInt(6)) - 3D),
                    pz = MathHelper.floor((entity.locZ + (double) Storm.random.nextInt(20)) - 10D);
            if (!isUnderSky(world, px, py, pz)) //TODO If not work, invert
                return Vec3D.a().create(px, py, pz);
        }
        return null;
    }

    private boolean isUnderSky() {
        return isUnderSky(world, entity.locX, entity.boundingBox.b, entity.locZ);
    }

    public boolean isUnderSky(World world, double x, double y, double z) {
        return Storm.util.isLocationUnderSky(new Location(world.getWorld(), x, y, z));
    }
}
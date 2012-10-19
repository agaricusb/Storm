package com.github.StormTeam.Storm;

import net.minecraft.server.*;

import java.util.Random;

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
        a(1);
    }

    public boolean a() {
        if (!entity.world.J())
            return false;
        if (!Storm.manager.getActiveWeathers(world.getWorld().getName()).contains(name))
            return false;
        if (!world.j(MathHelper.floor(entity.locX), (int) entity.boundingBox.b, MathHelper.floor(entity.locZ)))
            return false;
        Vec3D vec3d = f();
        if (vec3d == null) {
            return false;
        } else {
            x = vec3d.a;
            y = vec3d.b;
            z = vec3d.c;
            return true;
        }
    }

    public boolean b() {
        return !entity.getNavigation().f();
    }

    public void e() {
        entity.getNavigation().a(x, y, z, speed);
        entity.getNavigation().d(true);
    }

    public void c() {
        entity.getNavigation().d(false);
        if (a())
            e();
    }

    private Vec3D f() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int j = MathHelper.floor((entity.locX + (double) random.nextInt(20)) - 10D);
            int k = MathHelper.floor((entity.boundingBox.b + (double) random.nextInt(6)) - 3D);
            int l = MathHelper.floor((entity.locZ + (double) random.nextInt(20)) - 10D);
            if (!world.j(j, k, l) && entity.a(j, k, l) < 0.0F)
                return Vec3D.a().create(j, k, l);
        }

        return null;
    }
}


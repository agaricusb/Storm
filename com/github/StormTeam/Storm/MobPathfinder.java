package com.github.StormTeam.Storm;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class MobPathfinder {

    private EntityCreature entity;
    private float speed = 0.05F;

    public MobPathfinder(Entity e) {
        entity = (EntityCreature) ((CraftEntity) e).getHandle();
    }

    public void setTarget(Location loc) {
        if (!entity.dead)
            moveMob(loc);
    }

    private void moveMob(Location loc) {
        PathEntity path = entity.world.a(entity, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 32.0F, true, false, false, true);
        entity.setPathEntity(path);
        entity.getNavigation().a(path, speed);
    }
}

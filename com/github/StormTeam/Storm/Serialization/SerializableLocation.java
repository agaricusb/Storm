package com.github.StormTeam.Storm.Serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class SerializableLocation extends Object implements Serializable {
    private double x, y, z;
    private String world;

    public SerializableLocation(Location loc) {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        world = loc.getWorld().getName();
    }

    public Location getLocation() {
        World w = Bukkit.getWorld(world);
        if (w == null)
            return null;
        Location toRet = new Location(w, x, y, z);
        return toRet;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public int getBlockX() {
        return (int) x;
    }

    public int getBlockY() {
        return (int) y;
    }

    public int getBlockZ() {
        return (int) z;
    }

    public void setY(int v) {
        this.y = v;
    }

    public void setX(int v) {
        this.x = v;
    }

    public void setZ(int v) {
        this.x = v;
    }

}
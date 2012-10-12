package com.github.StormTeam.Storm.Meteors;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Meteors.Entities.EntityMeteor;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Collections;
import java.util.Set;

/**
 * A meteor weather object.
 */

public class MeteorWeather extends StormWeather {

    private GlobalVariables glob;
    private int killID;

    /**
     * Creates a meteor weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public MeteorWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
    }

    /**
     * Called when a meteor is called in the handled world.
     */

    @Override
    public void start() {

        if (!glob.Features_Meteor) {
            return;
        }

        Chunk chunk = Storm.util.pickChunk(Bukkit.getWorld(world));

        if (chunk == null) {
            return;
        }

        int x = Storm.random.nextInt(16);
        int z = Storm.random.nextInt(16);
        Block b = chunk.getBlock(x, 4, z);

        spawnMeteorNaturallyAndRandomly(chunk.getWorld(),
                b.getX(),
                b.getZ());

        //Abusing the API. Who cares?
        killID = Storm.manager.createAutoKillWeatherTask("storm_meteor", world, 1);
    }

    /**
     * Called to kill the meteor weather.
     */

    @Override
    public void end() {
        Bukkit.getScheduler().cancelTask(killID);
    }

    /**
     * Returns the texture to be used during this event.
     *
     * @return The path to the texture
     */

    @Override
    public String getTexture() {
        return null;
    }

    /**
     * Meteors don't conflict anything.
     *
     * @return Collections.EMPTY_SET; an empty set
     */

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }

    private void spawnMeteorNaturallyAndRandomly(World world, double x, double z) {
        net.minecraft.server.World meteoriteWorld = ((CraftWorld) world).getHandle();

        EntityMeteor meteor = new EntityMeteor(
                meteoriteWorld,
                Storm.random.nextInt(7) + 1,
                10,
                Storm.random.nextInt(5) + 5,
                Storm.random.nextInt(50) + 25,
                100,
                glob.Natural__Disasters_Meteor_Messages_On__Meteor__Crash,
                9,
                80,
                glob.Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave,
                Storm.random.nextInt(100) + 200,
                glob.Natural__Disasters_Meteor_Meteor_Spawn,
                Storm.random.nextInt(6) + 3);

        meteor.spawn();

        meteor.setPosition(
                x,
                Storm.random.nextInt(100) + 156,
                z);
        meteor.yaw = (float) Storm.random.nextInt(360);
        meteor.pitch = -9;
        meteoriteWorld.addEntity(meteor, CreatureSpawnEvent.SpawnReason.DEFAULT);

        Fireball fireMeteor = (Fireball) meteor.getBukkitEntity();
        fireMeteor.setDirection(fireMeteor.getDirection().setY(-1));
    }
}

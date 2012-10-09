package com.github.StormTeam.Storm.Meteors;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Meteors.Entities.EntityMeteor;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author Tudor
 */
public class MeteorWeather extends StormWeather {

    private GlobalVariables glob;
    private Random rand = new Random();
    private int killID;

    public MeteorWeather(Storm storm, String world) {
        super(storm, world);
    }

    @Override
    public void start() {

        if (!glob.Features_Meteor) {
            return;
        }

        Chunk chunk = Storm.util.pickChunk(Bukkit.getWorld(world));

        if (chunk == null) {
            return;
        }

        int x = rand.nextInt(16);
        int z = rand.nextInt(16);
        Block b = chunk.getBlock(x, 4, z);
        spawnMeteorNaturallyAndRandomly(chunk.getWorld(),
                b.getX(),
                b.getZ());

        killID = Bukkit.getScheduler()
                .scheduleAsyncDelayedTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Storm.manager.stopWeather("storm_meteor", world);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100);
    }

    @Override
    public void end() {
        Bukkit.getScheduler().cancelTask(killID);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }

    private void spawnMeteorNaturallyAndRandomly(World world, double x, double z) {
        net.minecraft.server.World meteoriteWorld = ((CraftWorld) world).getHandle();

        EntityMeteor meteor = new EntityMeteor(
                meteoriteWorld,
                rand.nextInt(7) + 1,
                10,
                rand.nextInt(5) + 5,
                rand.nextInt(50) + 25,
                100,
                glob.Natural__Disasters_Meteor_Message__On__Meteor__Crash,
                9,
                80,
                glob.Natural__Disasters_Meteor_Shockwave_Damage__Message,
                rand.nextInt(100) + 200,
                glob.Natural__Disasters_Meteor_Meteor_Spawn,
                rand.nextInt(6) + 3);

        meteor.spawn();

        meteor.setPosition(
                x,
                rand.nextInt(100) + 156,
                z);
        meteor.yaw = (float) rand.nextInt(360);
        meteor.pitch = -9;
        meteoriteWorld.addEntity(meteor, CreatureSpawnEvent.SpawnReason.DEFAULT);

        Fireball fireMeteor = (Fireball) meteor.getBukkitEntity();
        fireMeteor.setDirection(fireMeteor.getDirection().setY(-1));
    }
}

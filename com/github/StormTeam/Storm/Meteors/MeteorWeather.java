package com.github.StormTeam.Storm.Meteors;

import com.github.StormTeam.Storm.Meteors.Entities.EntityMeteor;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * A meteor weather object.
 */

public class MeteorWeather extends StormWeather {

    private final WorldVariables glob;

    /**
     * Creates a meteor weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public MeteorWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
        autoKillTicks = 1;
    }

    @Override
    public boolean canStart() {
        return glob.Weathers__Enabled_Natural__Disasters_Meteors;
    }

    /**
     * Called when a meteor is called in the handled world.
     */

    @Override
    public void start() {

        Chunk chunk = StormUtil.pickChunk(Bukkit.getWorld(world));

        if (chunk == null) {
            return;
        }
        Block b = chunk.getBlock(Storm.random.nextInt(16), 4, Storm.random.nextInt(16));
        spawnMeteorNaturallyAndRandomly(chunk.getWorld(), b.getX(), b.getZ());
    }

    @Override
    public void end() {

    }

    private void spawnMeteorNaturallyAndRandomly(World world, double x, double z) {
        net.minecraft.server.v1_4_6.World meteoriteWorld = ((CraftWorld) bukkitWorld).getHandle();

        EntityMeteor meteor = new EntityMeteor(
                meteoriteWorld,
                Storm.random.nextInt(7) + 1,
                10,
                Storm.random.nextInt(5) + 5,
                Storm.random.nextInt(50) + 25,
                100,
                glob.Natural__Disasters_Meteor_Messages_On__Meteor__Crash,
                glob.Natural__Disasters_Meteor_Shockwave_Damage,
                80,
                glob.Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave,
                Storm.random.nextInt(100) + 200,
                glob.Natural__Disasters_Meteor_Do__Winter,
                glob.Natural__Disasters_Meteor_Meteor__Spawn,
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

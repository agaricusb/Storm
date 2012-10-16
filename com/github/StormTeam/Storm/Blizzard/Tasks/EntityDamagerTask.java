package com.github.StormTeam.Storm.Blizzard.Tasks;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * An object for damaging entities during blizzards.
 *
 * @author Tudor
 */

public class EntityDamagerTask {

    private int id;
    private final World affectedWorld;
    private final Storm storm;
    private final GlobalVariables glob;

    /**
     * Creates a damager object for given world.
     *
     * @param storm         The Storm plugin used for config retrieving
     * @param affectedWorld The world to handle
     */

    public EntityDamagerTask(Storm storm, String affectedWorld) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(affectedWorld);
        glob = Storm.wConfigs.get(affectedWorld);
    }

    /**
     * Starts the task.
     */

    public void run() {
        id = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                        storm,
                        new Runnable() {
                            @Override
                            public void run() {
                                for (Entity damagee : affectedWorld.getEntities()) {
                                    if (Storm.util.isEntityUnderSky(damagee) && Storm.util.isRainy(damagee.getLocation().getBlock().getBiome())) {
                                        if (!Storm.util.isLocationNearBlock(damagee.getLocation(), glob.Blizzard_Heating__Blocks, glob.Blizzard_Heat__Radius)) {
                                            ((LivingEntity) (damagee)).damage(glob.Blizzard_Entity_Damage__From__Exposure);
                                        }
                                    }
                                }
                            }
                        },
                        glob.Blizzard_Scheduler_Damager__Calculation__Intervals__In__Ticks,
                        glob.Blizzard_Scheduler_Damager__Calculation__Intervals__In__Ticks);

    }

    /**
     * Ends the task.
     */

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

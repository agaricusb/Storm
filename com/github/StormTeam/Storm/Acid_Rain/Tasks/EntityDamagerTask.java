package com.github.StormTeam.Storm.Acid_Rain.Tasks;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * An object for damaging entities during acid rain.
 *
 * @author Tudor
 */

public class EntityDamagerTask {

    private int id;
    private final World affectedWorld;
    private final Storm storm;
    private final GlobalVariables glob;
    private final PotionEffect hunger;

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
        hunger = new PotionEffect(
                PotionEffectType.POISON,
                glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks + 60,
                1);
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
                                        if (Storm.util.isLocationNearBlock(damagee.getLocation(), glob.Acid__Rain_Absorbing__Blocks, glob.Acid__Rain_Absorbing__Radius)) {
                                            if (glob.Features_Acid__Rain_Entity__Damaging && damagee instanceof LivingEntity && !(damagee instanceof Player))
                                                ((LivingEntity) (damagee)).damage(glob.Acid__Rain_Entity_Damage__From__Exposure);
                                            else if (glob.Features_Acid__Rain_Player__Damaging && damagee instanceof Player) {
                                                Player dam = (Player) damagee;
                                                if (!dam.getGameMode().equals(GameMode.CREATIVE) && !dam.hasPermission("storm.acidrain.immune") && !glob.Acid__Rain_Absorbing__Blocks.contains(dam.getItemInHand().getTypeId())) {
                                                    if (dam.getHealth() > 0) {
                                                        dam.addPotionEffect(hunger, true);
                                                        dam.damage(glob.Acid__Rain_Player_Damage__From__Exposure);
                                                        dam.sendMessage(glob.Acid__Rain_Messages_On__Player__Damaged__By__Acid__Rain);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks,
                        glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks);

    }

    /**
     * Ends the task.
     */

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

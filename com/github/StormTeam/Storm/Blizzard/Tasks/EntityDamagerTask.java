package com.github.StormTeam.Storm.Blizzard.Tasks;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
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
 * @author Icyene
 */

public class EntityDamagerTask implements Runnable {

    private int id;
    private final World affectedWorld;
    private final Storm storm;
    private final GlobalVariables glob;
    private final PotionEffect blindness;

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
        blindness = new PotionEffect(
                PotionEffectType.BLINDNESS,
                glob.Blizzard_Scheduler_Damager__Calculation__Intervals__In__Ticks + 60,
                0);
    }

    @Override
    public void run() {
        for (Entity damagee : affectedWorld.getEntities()) {
            if (StormUtil.isEntityUnderSky(damagee) && StormUtil.isSnowy(damagee.getLocation().getBlock().getBiome())) {
                if (StormUtil.isLocationNearBlock(damagee.getLocation(), glob.Blizzard_Heating__Blocks, glob.Blizzard_Heat__Radius)) {
                    if (glob.Features_Blizzards_Entity__Damaging && damagee instanceof LivingEntity && !(damagee instanceof Player))
                        ((LivingEntity) (damagee)).damage(glob.Blizzard_Entity_Damage__From__Exposure);
                    else if (glob.Features_Blizzards_Player__Damaging && damagee instanceof Player) {
                        Player dam = (Player) damagee;
                        if (!dam.getGameMode().equals(GameMode.CREATIVE) && !dam.hasPermission("storm.blizzard.immune") && !glob.Blizzard_Heating__Blocks.contains(dam.getItemInHand().getTypeId())) {
                            if (dam.getHealth() > 0) {
                                dam.addPotionEffect(blindness, true);
                                dam.damage(glob.Blizzard_Player_Damage__From__Exposure);
                                dam.sendMessage(glob.Blizzard_Messages_On__Player__Damaged__Cold);
                                StormUtil.playSound(dam, "random.breath", 1F, 1F);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts the task.
     */
    public void start() {
        id = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                        storm,
                        this,
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

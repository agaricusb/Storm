package com.github.StormTeam.Storm.Blizzard.Tasks;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamagerTask {

    private int id;
    private World affectedWorld;
    private Storm storm;
    private GlobalVariables glob;
    private PotionEffect blindness;

    /**
     * Creates a damager object for given world.
     *
     * @param storm      The Storm plugin used for config retrieving
     * @param spawnWorld The world to handle
     */

    public DamagerTask(Storm storm, String spawnWorld) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(spawnWorld);
        glob = Storm.wConfigs.get(spawnWorld);
        blindness = new PotionEffect(
                PotionEffectType.BLINDNESS,
                glob.Blizzard_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks + 60,
                glob.Blizzard_Player_Blindness__Amplitude);

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

                                for (Player damagee : affectedWorld
                                        .getPlayers()) {
                                    if (!damagee.getGameMode().equals(
                                            GameMode.CREATIVE) && Storm.util
                                            .isPlayerUnderSky(damagee) && Storm.util.isSnowy(damagee.getLocation().getBlock().getBiome())
                                            && !damagee.hasPermission("storm.blizzard.immune")) {

                                        if (glob.Blizzard_Player_Heating__Blocks.contains(damagee.getItemInHand().getTypeId()) || Storm.util.isLocationNearBlock(damagee.getLocation(),
                                                glob.Blizzard_Player_Heating__Blocks, glob.Blizzard_Player_Heat__Radius)) {
                                            return;
                                        }

                                        damagee.addPotionEffect(blindness, true);
                                        Storm.util.damagePlayer(damagee, glob.Blizzard_Messages_On__Player__Damaged__Cold, glob.Blizzard_Player_Damage__From__Exposure);
                                    }
                                }
                            }
                        },
                        glob.Blizzard_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks,
                        glob.Blizzard_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks);

    }

    /**
     * Ends the task.
     */

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

package com.github.StormTeam.Storm.Acid_Rain.Tasks;

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
    private PotionEffect hunger;

    public DamagerTask(Storm storm, String affectedWorld) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(affectedWorld);
        glob = Storm.wConfigs.get(affectedWorld);
        hunger = new PotionEffect(
                PotionEffectType.HUNGER,
                glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks + 60,
                1);
    }

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
                                            .isPlayerUnderSky(damagee) && Storm.util.isRainy(damagee.getLocation().getBlock().getBiome())
                                            && !damagee.hasPermission("storm.acidrain.immune")) {

                                        if (glob.Acid__Rain_Player_Absorbing__Blocks.contains(damagee.getItemInHand().getTypeId()) || Storm.util.isLocationNearBlock(damagee.getLocation(),
                                                glob.Acid__Rain_Player_Absorbing__Blocks, glob.Acid__Rain_Player_Absorbing__Radius)) {
                                            return;
                                        }
                                        damagee.addPotionEffect(hunger, true);
                                        Storm.util.damagePlayer(damagee, glob.Acid__Rain_Messages_On__Player__Damaged__By__Acid__Rain, glob.Acid__Rain_Player_Damage__From__Exposure);
                                    }
                                }
                            }
                        },
                        glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks,
                        glob.Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

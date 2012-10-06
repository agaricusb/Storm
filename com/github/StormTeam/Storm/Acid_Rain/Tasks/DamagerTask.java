package com.github.StormTeam.Storm.Acid_Rain.Tasks;

import com.github.StormTeam.Storm.Acid_Rain.AcidRain;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;

public class DamagerTask {

    private int id;
    private World affectedWorld;
    private Storm storm;
    private GlobalVariables glob;
    private PotionEffect hunger;

    public DamagerTask(Storm storm, World affectedWorld) {
        this.storm = storm;
        this.affectedWorld = affectedWorld;
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
                                    .isPlayerUnderSky(damagee) && Storm.util.isRainy(damagee.getLocation().getBlock().getBiome())) {

                                if (glob.Acid__Rain__Absorbing__Blocks.contains(damagee.getItemInHand().getTypeId()) || Storm.util.isLocationNearBlock(damagee.getLocation(),
                                        glob.Acid__Rain__Absorbing__Blocks, glob.Acid__Rain__Absorbing__Radius)) {
                                    return;
                                }
                                damagee.addPotionEffect(hunger, true);
                                Storm.util.damagePlayer(damagee, glob.Acid__Rain_Message_On__Player__Damaged__By__Acid__Rain, glob.Acid__Rain_Player_Damage__From__Exposure);
                                if(damagee.getHealth() <= 0)
                                    AcidRain.deadPlayers.add(damagee.getName());
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

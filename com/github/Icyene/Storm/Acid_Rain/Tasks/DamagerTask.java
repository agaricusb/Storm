package com.github.Icyene.Storm.Acid_Rain.Tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.Icyene.Storm.GlobalVariables;
import com.github.Icyene.Storm.Storm;

public class DamagerTask {

    private int id;
    private Random rand = new Random();
    private World affectedWorld;
    private Storm storm;

    private GlobalVariables glob;

    public DamagerTask(Storm storm, World affectedWorld) {
	this.storm = storm;
	this.affectedWorld = affectedWorld;
	glob = Storm.wConfigs.get(affectedWorld.getName());
    }

    public void run() {

	id = Bukkit.getScheduler()
		.scheduleSyncRepeatingTask(
			storm,
			new Runnable()
			{
			    @Override
			    public void run()
			    {

				for (Player damagee : affectedWorld
					.getPlayers())
				{
				    if (!damagee.getGameMode().equals(
					    GameMode.CREATIVE))
				    {

					if (!Storm.util
						.isPlayerUnderSky(damagee)) {
					    return;
					} else {

					}

					damagee.damage(glob.Acid__Rain_Player_Damage__From__Exposure * 2);

					damagee.addPotionEffect(
						new PotionEffect(
							PotionEffectType.HUNGER,
							rand.nextInt(600) + 300,
							1), true);
					damagee.addPotionEffect(
						new PotionEffect(
							PotionEffectType.BLINDNESS,
							300, 1), true);

					Storm.util
						.message(
							damagee,
							glob.Acid__Rain_Damager_Message__On__Player__Damaged__By__Acid__Rain);

				    }
				}
			    }

			},
			glob.Acid__Rain_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks,
			glob.Acid__Rain_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks);

    }

    public void stop() {
	Bukkit.getScheduler().cancelTask(id);
    }

}
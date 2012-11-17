package com.github.StormTeam.Storm.Lightning.Listeners;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Lightning.LightningUtils;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

/**
 * Handles lightning strikes to enchance them.
 */

public class StrikeListener implements Listener {

    /**
     * Handles LightningStrike events.
     *
     * @param strike The LighningStrikeEvent to complement
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void strikeLightningListener(LightningStrikeEvent strike) {
        GlobalVariables glob = Storm.wConfigs.get(strike.getWorld().getName());

        Location strikeLocation = strike.getLightning().getLocation();

        if (glob.Features_Lightning_Greater__Range__And__Damage) {
            StormUtil.damageNearbyPlayers(
                    strikeLocation,
                    glob.Lightning_Damage_Damage__Radius,
                    glob.Lightning_Damage_Damage,
                    glob.Lightning_Messages_On__Player__Hit
            );
        }

        if (glob.Features_Lightning_Block__Attraction) {
            if (Storm.random.nextInt(100) < glob.Lightning_Attraction_Blocks_AttractionChance) {
                strikeLocation = LightningUtils.hitMetal(strike.getLightning().getLocation());
                strike.getLightning().teleport(strikeLocation);
            } else {
                if (glob.Features_Lightning_Player__Attraction && Storm.random.nextInt(100) < glob.Lightning_Attraction_Players_AttractionChance) {
                    strikeLocation = LightningUtils.hitPlayers(strike.getLightning().getLocation());
                    strike.getLightning().teleport(strikeLocation);
                }
            }
        }

        if (glob.Features_Lightning_Block__Transformations) {
            StormUtil.transform(strikeLocation.getBlock(), glob.Lightning_Melter_Block__Transformations);
        }
        StormUtil.playSoundNearby(strikeLocation, 10F, "mob.enderman.scream", 3F, Storm.random.nextInt(3) + 1F);
    }
}

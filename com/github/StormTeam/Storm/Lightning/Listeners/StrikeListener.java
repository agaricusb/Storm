package com.github.StormTeam.Storm.Lightning.Listeners;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Lightning.LightningUtils;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

/**
 * Handles lightning strikes to enchance them.
 */

public class StrikeListener implements Listener {

    /**
     * A LightningUtil method for... utility...
     */
    private final LightningUtils util;

    /**
     * Constructs a StrikeListener object.
     */
    public StrikeListener() {
        this.util = new LightningUtils();
    }

    /**
     * Handles LightningStrike events.
     *
     * @param strike The LighningStrikeEvent to complement
     */

    @EventHandler
    public void strikeLightningListener(LightningStrikeEvent strike) {

        if (strike.isCancelled()) {
            return;
        }

        GlobalVariables glob = Storm.wConfigs.get(strike.getWorld().getName());

        Location strikeLocation = strike.getLightning().getLocation();

        if (glob.Features_Lightning_Greater__Range__And__Damage) {
            Storm.util.damageNearbyPlayers(strikeLocation,
                    glob.Lightning_Damage_Damage__Radius,
                    glob.Lightning_Damage_Damage,
                    glob.Lightning_Messages_On__Player__Hit);
        }

        if (glob.Features_Lightning_Block__Attraction) {
            if (Storm.random.nextInt(100) < glob.Lightning_Attraction_Blocks_AttractionChance) {
                strikeLocation = util.hitMetal(strike.getLightning()
                        .getLocation());
                strike.getLightning().teleport(strikeLocation);
            }
        } else {
            if (glob.Features_Lightning_Player__Attraction) {
                if (Storm.random.nextInt(100) < glob.Lightning_Attraction_Players_AttractionChance) {
                    strikeLocation = util.hitPlayers(strike.getLightning()
                            .getLocation());
                    strike.getLightning().teleport(strikeLocation);

                }
            }
        }

        if (glob.Features_Lightning_Block__Transformations) {

            Storm.util.transform(strikeLocation.getBlock(),
                    glob.Lightning_Melter_Block__Transformations);
        }
        Storm.util.playSoundNearby(strikeLocation, 10F, "mob.enderman.scream", 3F, Storm.random.nextInt(3) + 1F);

    }

}

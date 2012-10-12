package com.github.StormTeam.Storm.Lightning;

import com.github.StormTeam.Storm.Lightning.Listeners.StrikeListener;
import com.github.StormTeam.Storm.Storm;

/**
 * A class for loading better lightning.
 */

public class Lightning {

    /**
     * Enables better lightning.
     *
     * @param storm The Storm plugin, used for listener registration
     */

    public static void load(Storm storm) {
        Storm.pm.registerEvents(new StrikeListener(), storm);
    }
}

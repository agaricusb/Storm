package com.github.StormTeam.Storm.Lightning;

import com.github.StormTeam.Storm.Lightning.Listeners.StrikeListener;
import com.github.StormTeam.Storm.Storm;

/**
 * @author hammale
 */
public class Lightning {

    public static void load(Storm storm) {
        Storm.pm.registerEvents(new StrikeListener(storm), storm);
    }
}

package com.github.StormTeam.Storm.Lightning;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Lightning.Listeners.StrikeListener;
import com.github.StormTeam.Storm.Storm;

/**
 * A class for loading better lightning.
 */

public class Lightning {

    /**
     * Enables better lightning.
     */

    public static void load() {
        try {
            Storm.pm.registerEvents(new StrikeListener(), Storm.instance);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

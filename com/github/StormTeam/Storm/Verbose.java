package com.github.StormTeam.Storm;

import java.util.logging.Level;

public class Verbose {

    public static void log(Level lev, String mes) {
        if (Storm.debug)
            Storm.util.log(lev, mes);
    }

    public static void log(String mes) {
        if (Storm.debug)
            Storm.util.log(mes);
    }
}

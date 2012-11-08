package com.github.StormTeam.Storm.Volcano;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;

import java.io.File;

public class Volcano {
    public static File vulkanos;

    public static void load() {
        try {
            vulkanos = new File(Storm.instance.getDataFolder() + File.separator + "volcanoes.bin");
            if (vulkanos.exists() || vulkanos.createNewFile())
                VolcanoControl.load(vulkanos);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }
}

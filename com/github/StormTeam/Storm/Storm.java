/*
 * Storm Copyright (C) 2012 Icyene, Xiaomao, Thidox
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.StormTeam.Storm;

import com.github.StormTeam.Storm.Acid_Rain.AcidRain;
import com.github.StormTeam.Storm.Blizzard.Blizzard;
import com.github.StormTeam.Storm.Lightning.Lightning;
import com.github.StormTeam.Storm.Meteors.Meteor;
import com.github.StormTeam.Storm.Thunder_Storm.ThunderStorm;
import com.github.StormTeam.Storm.Weather.WeatherManager;
import com.github.StormTeam.Storm.Wildfire.Wildfire;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

/*
 * Dear BukkitDev administrator(s):
 *
 * Thank you for your time in reviewing this project! If you find anything
 * in it that makes you cry inside, will you please let us know so we can
 * fix/improve it? Aside from horrible formatting, we're working on that :)
 *
 * Thanks in advance, The-people-who-made-this-plugin
 */

/**
 * The main Storm class.
 */

public class Storm extends JavaPlugin {

    /**
     * A HashMap containing world name and configuration object.
     */
    public static HashMap<String, GlobalVariables> wConfigs = new HashMap<String, GlobalVariables>();
    /**
     * A StormUtil object.
     */
    public static StormUtil util;
    /**
     * A global Random object, to avoid needless construction.
     */
    public static final Random random = new Random();
    /**
     * The server's plugin manager, to avoid fetching each use.
     */
    public static PluginManager pm;
    /**
     * The MC version.
     */
    public static double version = 0.0;
    /**
     * The Storm WeatherManager.
     */
    public static WeatherManager manager;

    /**
     * Called to enable Storm.
     */

    @Override
    public void onEnable() {
        try {
            pm = getServer().getPluginManager();
            util = new StormUtil(this);

            configureVersion();
            initConfiguration();
            ErrorLogger.register(this, "Storm", "com.github.StormTeam.Storm", "https://github.com/StormTeam/Storm/issues");

            pm.registerEvents((manager = new WeatherManager(this)), this); //Register texture events

            new MetricsLite(this).start();

            AcidRain.load(this);
            Lightning.load(this);
            Wildfire.load(this);
            Blizzard.load(this);
            Meteor.load(this);
            ThunderStorm.load(this);

        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    private void configureVersion() {
        String vers = getServer().getVersion();
        if (vers.contains("1.2.")) {
            version = 1.2;
        } else {
            if (vers.contains("1.3.")) {
                version = 1.3;
            } else {
                getLogger().log(Level.SEVERE, "Unsupported MC version detected! Bad things may happen!");
                return;
            }
        }
        getLogger().log(Level.INFO, "Loading with MC {0}.X compatibility.", version);
    }

    private void initConfiguration() {

        // Make per-world configuration files           
        for (World world : Bukkit.getWorlds()) {
            String name = world.getName();
            GlobalVariables config = new GlobalVariables(this, name);
            config.load();
            wConfigs.put(name, config);
        }

        pm.registerEvents(new WorldConfigLoader(this), this); //For late loading worlds loaded by world plugins al a MultiVerse
    }
}
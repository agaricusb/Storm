/*
 * Storm
 * Copyright (C) 2012 Icyene, Thidox
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.StormTeam.Storm;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.StormTeam.Storm.Acid_Rain.AcidRain;
import com.github.StormTeam.Storm.Blizzard.Blizzard;
import com.github.StormTeam.Storm.Database.Database;
import com.github.StormTeam.Storm.Lightning.Lightning;
import com.github.StormTeam.Storm.Thunder_Storm.ThunderStorm;
import com.github.StormTeam.Storm.Weather.WeatherManager;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.plugin.PluginManager;

public class Storm extends JavaPlugin {

    public static HashMap<String, GlobalVariables> wConfigs = new HashMap<String, GlobalVariables>();
    public static BiomeGroups biomes;
    public static StormUtil util;
    public static final Random random = new Random();
    private Database db;
    public static PluginManager pm;
    public static double version;
    public static WeatherManager manager;

    @Override
    public void onEnable() {
        try {

            pm = getServer().getPluginManager();

            String v = getServer().getVersion();
            if (v.contains("1.2.")) {
                version = 1.2;
                getLogger().log(Level.INFO, "Loading with MC 1.2.X compatibility.");
            } else {
                if (v.contains("1.3.")) {
                    version = 1.3;
                    getLogger().log(Level.INFO, "Loading with MC 1.3.X compatibility.");
                } else {
                    getLogger().log(Level.SEVERE, "Unsupported MC version detected!");
                }
            }

            manager = new WeatherManager(this);
            pm.registerEvents(manager, this); //Register texture events

            util = new StormUtil(this);
            biomes = new BiomeGroups();
            db = Database.Obtain(this, null);

            // Make per-world configuration files
            System.out.println(Bukkit.getWorlds());
            for (World w : Bukkit.getWorlds()) {
                String world = w.getName();
                GlobalVariables config = new GlobalVariables(this, world);
                config.load();
                wConfigs.put(world, config);
            }

            // Stats
            try {
                new MetricsLite(this).start();
            } catch (Exception e) {
            }

            //Wildfires and meteors not NAPI-compatible yet
            AcidRain.load(this);
            Lightning.load(this);
//            Wildfire.load(this);
            Blizzard.load(this);
//            Meteor.load(this);
            ThunderStorm.load(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.db.getEngine().close();
    }

    public void crashDisable(String crash) {
        util.log(Level.SEVERE, crash + " Storm disabled.");
        this.setEnabled(false);
    }
}
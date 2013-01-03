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

import com.github.StormTeam.Storm.Weather.WeatherManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * The main Storm class.
 */

public class Storm extends JavaPlugin implements Listener {

    private String _ =
            "Dear BukkitDev administrator(s):\n" +
                    "Thank you for your time in reviewing this project! If you find anything " +
                    "in it that makes you cry inside, will you please let us know so we can " +
                    "fix/improve it? Aside from horrible formatting, we're working on that :-)\n" +
                    "Thanks in advance, The-people-who-made-this-plugin";

    private String __ =
            "Dear (Non-Bukkit-Admin) decompiler(s):\nThere is no point in decompiling this plugin: " +
                    "all the source is already up at Github (github.com/StormTeam/Storm). Besides, neither JAD nor JD-GUI nor " +
                    "any decompiler can decompile source code to how it was before compilation. Save yourself time :-) " +
                    "BTW, if you decompile the code, you will not get the comments, " +
                    "which will render our bitmasks impossible to understand. Enjoy.";

    /**
     * A HashMap containing world name and configuration object.
     */
    public static final HashMap<String, WorldVariables> wConfigs = new HashMap<String, WorldVariables>();
    /**
     * A global Random object, to avoid needless construction.
     */
    //public static final java.util.Random random = new java.util.Random();
    public static final com.github.StormTeam.Storm.Math.Random random = new com.github.StormTeam.Storm.Math.Random();
    /**
     * The server's plugin manager, to avoid fetching each use.
     */
    public static PluginManager pm;

    /**
     * The Storm WeatherManager.
     */
    public static WeatherManager manager;

    public static Storm instance = null;

    public static boolean verbose = false;

    public static ReflectCommand commandRegistrator = null;

    public static boolean incompatible = false;

    @Override
    public void onEnable() {
        try {

            GlobalVariables glob = new GlobalVariables(this, "global_variables");
            glob.load();
            verbose = glob.Verbose__Logging;

            commandRegistrator = new ReflectCommand(this);
            ErrorLogger.register(this, "Storm", "com.github.StormTeam.Storm", "http://www.stormteam.tk/projects/storm/issues");

            if (instance != null) {
                getLogger().log(Level.SEVERE, "Error! Only one instance of Storm may run at once! Storm detected running version: " +
                        instance.getDescription().getVersion() + ". Please disable that version of Storm, and restart your server. Storm disabled.");
                setEnabled(false);
            }
            instance = this;
            commandRegistrator.register(this.getClass());

            configureVersion();

            pm = getServer().getPluginManager();
            pm.registerEvents((manager = new WeatherManager(this)), this); //Register texture/world events

            initConfiguration();
            if (glob.Auto__Updating)
                initUpdater();

            Statistics.graph(new Metrics(this));

            //For the modular builder later on
            com.github.StormTeam.Storm.Acid_Rain.AcidRain.load();
            com.github.StormTeam.Storm.Lightning.Lightning.load();
            com.github.StormTeam.Storm.Wildfire.Wildfire.load();
            com.github.StormTeam.Storm.Blizzard.Blizzard.load();
            com.github.StormTeam.Storm.Meteors.Meteor.load();
            com.github.StormTeam.Storm.Thunder_Storm.ThunderStorm.load();
            com.github.StormTeam.Storm.Volcano.Volcano.load();
            com.github.StormTeam.Storm.Earthquake.Earthquake.load();

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Storm failed to start.");
            setEnabled(false);
            e.printStackTrace();
        }
    }

    private void configureVersion() {
        String v = Bukkit.getBukkitVersion().substring(0, 5);
        if (!v.equals("1.4.6")) {
            getLogger().log(Level.WARNING, "Storm is not compatible with MC version " + v + ". Storm suspended.");
            incompatible = true;
        }
    }

    private void initConfiguration() {

        // Make per-world configuration files           
        for (World world : Bukkit.getWorlds()) {
            String name = world.getName();
            WorldVariables config = new WorldVariables(this, name, ".worlds");
            config.load();
            wConfigs.put(name, config);
        }

        pm.registerEvents(new WorldConfigLoader(this), this); //For late loading worlds loaded by world plugins al a MultiVerse
    }

    private void initUpdater() {
        StormUtil.log("Checking for a new update...");
        Updater updater = new Updater(this, "storm", this.getFile(), Updater.UpdateType.DEFAULT, false);
        if (updater.getResult() != Updater.UpdateResult.NO_UPDATE) {
            StormUtil.log("Update found! Downloading...");
            StormUtil.log(updater.getLatestVersionString() + " will be enabled on reload!");
        } else {
            StormUtil.log("No update found: running latest version.");
        }
    }


    class GlobalVariables extends ReflectConfiguration {
        public GlobalVariables(Plugin plugin, String name) {
            super(plugin, name);
        }

        public boolean Auto__Updating = true;
        public boolean Verbose__Logging = false;
    }

}

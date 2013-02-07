package com.github.StormTeam.Storm;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Metrics;
import com.github.StormTeam.Storm.ReflectCommand;
import com.github.StormTeam.Storm.ReflectConfiguration;
import com.github.StormTeam.Storm.Statistics;
import com.github.StormTeam.Storm.WorldConfigLoader;
import com.github.StormTeam.Storm.WorldVariables;
import com.github.StormTeam.Storm.Acid_Rain.AcidRain;
import com.github.StormTeam.Storm.Blizzard.Blizzard;
import com.github.StormTeam.Storm.Earthquake.Earthquake;
import com.github.StormTeam.Storm.Lightning.Lightning;
import com.github.StormTeam.Storm.Math.Random;
import com.github.StormTeam.Storm.Meteors.Meteor;
import com.github.StormTeam.Storm.Thunder_Storm.ThunderStorm;
import com.github.StormTeam.Storm.Volcano.Volcano;
import com.github.StormTeam.Storm.Weather.WeatherManager;
import com.github.StormTeam.Storm.Wildfire.Wildfire;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Storm extends JavaPlugin implements Listener {

   private String _ = "Dear BukkitDev administrator(s):\nThank you for your time in reviewing this project! If you find anything in it that makes you cry inside, will you please let us know so we can fix/improve it? Aside from horrible formatting, we\'re working on that :-)\nThanks in advance, The-people-who-made-this-plugin";
   private String __ = "Dear (Non-Bukkit-Admin) decompiler(s):\nThere is no point in decompiling this plugin: all the source is already up at Github (github.com/StormTeam/Storm). Besides, neither JAD nor JD-GUI nor any decompiler can decompile source code to how it was before compilation. Save yourself time :-) BTW, if you decompile the code, you will not get the comments, which will render our bitmasks impossible to understand. Enjoy.";
   public static final HashMap wConfigs = new HashMap();
   public static final Random random = new Random();
   public static PluginManager pm;
   public static WeatherManager manager;
   public static Storm instance = null;
   public static boolean verbose = false;
   public static ReflectCommand commandRegistrator = null;


   public void onEnable() {
      try {
         Storm.GlobalVariables e = new Storm.GlobalVariables(this, "global_variables");
         e.load();
         verbose = e.Verbose__Logging;
         commandRegistrator = new ReflectCommand(this);
         //ErrorLogger.register(this, "Storm", "com.github.StormTeam.Storm", "http://www.stormteam.tk/projects/storm/issues");
         if(instance != null) {
            this.getLogger().log(Level.SEVERE, "Error! Only one instance of Storm may run at once! Storm detected running version: " + instance.getDescription().getVersion() + ". Please disable that version of Storm, and restart your server. Storm disabled.");
            this.setEnabled(false);
         }

         instance = this;
         commandRegistrator.register(this.getClass());
         this.configureVersion();
         pm = this.getServer().getPluginManager();
         pm.registerEvents(manager = new WeatherManager(this), this);
         this.initConfiguration();
         Statistics.graph(new Metrics(this));
         AcidRain.load();
         Lightning.load();
         Wildfire.load();
         Blizzard.load();
         Meteor.load();
         ThunderStorm.load();
         Volcano.load();
         Earthquake.load();
      } catch (Exception var2) {
         this.getLogger().log(Level.SEVERE, "Storm failed to start.");
         this.setEnabled(false);
         var2.printStackTrace();
      }

   }

   private void configureVersion() {
      try {
         Class.forName("org.surgeproject.Main");
         this.getLogger().log(Level.WARNING, "Storm running off Surge compatibility patch. Stability not guaranteed.");
      } catch (Exception var4) {
         try {
            Class.forName("net.minecraft.server.v1_4_R1.World");
         } catch (Exception var3) {
            this.getLogger().log(Level.WARNING, "Storm is not compatible with current MC version. Storm suspended.");
            this.setEnabled(false);
         }
      }

   }

   private void initConfiguration() {
      Iterator i$ = Bukkit.getWorlds().iterator();

      while(i$.hasNext()) {
         World world = (World)i$.next();
         String name = world.getName();
         WorldVariables config = new WorldVariables(this, name, ".worlds");
         config.load();
         wConfigs.put(name, config);
      }

      pm.registerEvents(new WorldConfigLoader(this), this);
   }


   class GlobalVariables extends ReflectConfiguration {

      public boolean Verbose__Logging = false;


      public GlobalVariables(Plugin plugin, String name) {
         super(plugin, name);
      }
   }
}

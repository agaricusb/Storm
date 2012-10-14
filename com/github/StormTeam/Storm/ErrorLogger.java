package com.github.StormTeam.Storm;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static java.lang.System.getProperty;

/**
 * Custom logger to save errors. Multiple-instance safe
 *
 * @author Icyene, Xiaomao
 */

public class ErrorLogger extends PluginLogger {
    public static Map<String, List<String>> registry = new HashMap<String, List<String>>();
    public static boolean inited = false;
    private static Field logger_mc, cb_mcs, logger;

    public ErrorLogger(Plugin context) {
        super(context);
    }

    public ErrorLogger(final Plugin context, final String name, final String pack, final String tracker) {
        super(context);
    }

    @Override
    public void log(LogRecord logRecord) {
        if (!generateErrorLog(logRecord))
            super.log(logRecord);
    }

    private static void getFields() {
        try {
            logger_mc = MinecraftServer.class.getDeclaredField("log");
            logger_mc.setAccessible(true);
            cb_mcs = CraftServer.class.getDeclaredField("console");
            cb_mcs.setAccessible(true);
            logger = JavaPlugin.class.getDeclaredField("logger");
            logger.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void register(final String name, final String pack, final String tracker, final String endl) {
        registry.put(name, Arrays.asList(pack, tracker, endl));
    }

    public static void register(Plugin context, String name, String pack, String tracker) {
        getFields();
        try {
            ErrorLogger log = new ErrorLogger(context, name, pack, tracker);
            if (!ErrorLogger.inited) //If Thread.setDefaultUncaughtExceptionHandler hasn't been used
                initErrorHandler(); //Use it
            if (!(logger.get(context) instanceof ErrorLogger))
                logger.set(context, log);
            if (!(logger_mc.get(cb_mcs) instanceof ErrorLogger))
                logger_mc.set(cb_mcs, log);
            register(name, pack, tracker, getEndline(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getEndline(String name) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < name.length() + 54; ++i) //54 ='s + =*name.length
            temp.append('=');
        return "\n" + temp.toString(); // You actually did the same thing, but it's not intended
    }

    private static boolean generateErrorLog(LogRecord record) {
        Server server = Bukkit.getServer();
        Throwable thrown;
        if ((thrown = record.getThrown()) == null)
            return false;
        String ERROR = ExceptionUtils.getStackTrace(thrown), NAME = "", TICKETS = "", ENDL = "";
        Plugin PLUGIN = null;

        for (Map.Entry<String, List<String>> entry : registry.entrySet()) {
            List<String> data = entry.getValue();
            if (ERROR.contains(data.get(0))) { //If the ERROR contains the package
                NAME = entry.getKey();
                PLUGIN = Bukkit.getPluginManager().getPlugin(NAME);
                TICKETS = data.get(1);
                ENDL = data.get(2);
                break;
            }
        }

        if (ERROR.contains(NAME + " has encountered an error!") && ERROR.contains(ErrorLogger.class.getName()))  //Check if its not our own
            return false;

        PluginDescriptionFile pdf = PLUGIN.getDescription();
        StringBuilder err = new StringBuilder();
        boolean disable = false;
        err.append("\n=============" + NAME + " has encountered an error!=============")
                .append("\nStacktrace:\n" + ERROR)
                .append("\n" + NAME + " version: " + pdf.getVersion())
                .append("\nBukkit message: " + record.getMessage())
                .append("\nPlugins loaded: " + Arrays.asList(server.getPluginManager().getPlugins()))
                .append("\nCraftBukkit version: " + server.getBukkitVersion())
                .append("\nJava version: " + getProperty("java.version"))
                .append("\nOS info: " + getProperty("os.arch") + " " + getProperty("os.name") + ", " + getProperty("os.version"))
                .append("\nPlease report this error to the " + NAME + " ticket tracker (" + TICKETS + ")!");
        ERROR = ERROR.toLowerCase();
        if (ERROR.contains("nullpointerexception") || ERROR.contains("stackoverflowexception")) {
            err.append("\nA critical error has been thrown. " + NAME + " has been disabled to prevent further damage.");
            disable = true;
        } else {
            err.append("\nError was minor; " + NAME + " will continue operating.");
        }
        String name = NAME + "_" + md5(err).substring(0, 6) + ".error.log"; //Name is PLUGIN_NAME with first 6 chars of md5 appended
        File root = new File(PLUGIN.getDataFolder(), "errors");
        if (!root.exists())
            root.mkdir();
        File dump = new File(root.getAbsoluteFile(), name);

        if (!dump.exists()) {
            try {
                dump.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                writer.write((err.toString() + ENDL).substring(1)); //Remove the extra /n
                writer.close();
                err.append("\nThis has been saved to the file ./" + PLUGIN.getName() + "/errors/" + name);
            } catch (Exception e) {
                System.err.println("\nEhm, errors occured while displaying an error >.< Stacktrace:\n");
                e.printStackTrace();
            }
        }
        err.append(ENDL);
        System.err.println(err);

        if (disable)
            Bukkit.getServer().getPluginManager().disablePlugin(PLUGIN);
        return true;
    }

    private static void initErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                LogRecord o_u_screwd = new LogRecord(Level.SEVERE, "Oh, yea. You're screwed."); //Forced to set a name, you say?
                o_u_screwd.setMessage("Bukkit did not catch this, so no additional info is available.");
                o_u_screwd.setThrown(throwable);
                generateErrorLog(o_u_screwd);
            }
        });
        ErrorLogger.inited = true;
    }

    private static String md5(StringBuilder builder) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(builder.toString().getBytes());
            StringBuilder hash = new StringBuilder();
            String temp = new BigInteger(1, m.digest()).toString(16);
            int iter = 32 - temp.length();
            while (iter > 0) {
                hash.append('0');
                --iter;
            }
            hash.append(temp);
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }
}

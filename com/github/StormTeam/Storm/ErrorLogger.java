package com.github.StormTeam.Storm;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;

/**
 * Custom logger to save errors.
 *
 * @author Icyene
 */

public class ErrorLogger extends PluginLogger {

    final static String PLUGIN_NAME = "Storm", TICKET_TRACKER = "http://github.com/StormTeam/Storm/issues";
    static Plugin PLUGIN = null;

    final static String TASK = "Task \\#\\d+ for " + PLUGIN_NAME + " v[\\d.\\w]+ generated an exception";
    final static Pattern TASK_REGEX = Pattern.compile(TASK.toLowerCase());

    final static String ENABLE = "Error occurred while enabling " + PLUGIN_NAME + " v[\\d\\w.]";
    final static Pattern ENABLE_REGEX = Pattern.compile(ENABLE.toLowerCase());

    final static String COMMAND = "Unhandled exception executing command '\\w+' in plugin " + PLUGIN_NAME + " v[\\d\\w.]";
    final static Pattern COMMAND_REGEX = Pattern.compile(COMMAND.toLowerCase());

    final static String EVENT = "Could not pass event [\\d\\w.] to " + PLUGIN_NAME;
    final static Pattern EVENT_REGEX = Pattern.compile(EVENT.toLowerCase());

    public ErrorLogger(Plugin context) {
        super(context);
        this.PLUGIN = context;
    }

    @Override
    public void log(LogRecord logRecord) {
        String message = logRecord.getMessage().toLowerCase(), location = "";

        if (TASK_REGEX.matcher(message).find())
            location = "Error occured in task.";
        if (ENABLE_REGEX.matcher(message).find())
            location = "Error while enabling/disabling.";
        if (COMMAND_REGEX.matcher(message).find())
            location = "Error while parsing command.";
        if (EVENT_REGEX.matcher(message).find())
            location = "Error while handling event.";

        if (!StringUtils.isEmpty(location)) {
            generateErrorLog(logRecord, location);
            return;
        }
        super.log(logRecord);
    }

    private static void generateErrorLog(LogRecord record, String location) {
        PluginDescriptionFile pdf = PLUGIN.getDescription();
        Server server = Bukkit.getServer();
        String error = getStackTrace(record.getThrown());
        if (!error.contains(PLUGIN_NAME))
            return;
        StringBuilder err = new StringBuilder();
        boolean disable = false;
        err.append("\n=============" + PLUGIN_NAME + " has encountered an error!=============");
        err.append("\nStacktrace:\n" + error);
        err.append("\n" + PLUGIN_NAME + " version: " + pdf.getVersion());
        err.append("\nProblematic location: " + location);
        err.append("\nPlugins loaded: " + Arrays.asList(server.getPluginManager().getPlugins()));
        err.append("\nCraftBukkit version: " + server.getBukkitVersion());
        err.append("\nJava version: " + getProperty("java.version"));
        err.append("\nOS info: " + getProperty("os.arch") + " " + getProperty("os.name") + ", " + getProperty("os.version"));
        err.append("\nPlease report this error to the " + PLUGIN_NAME + " ticket tracker (" + TICKET_TRACKER + ")!");
        error = error.toLowerCase();
        if (error.contains("nullpointerexception") || error.contains("stackoverflowexception")) {
            err.append("\nA critical error has been thrown. " + PLUGIN_NAME + " has been disabled to prevent further damage.");
            disable = true;
        } else {
            err.append("\nError was minor; " + PLUGIN_NAME + " will continue operating.");
        }
        String name = PLUGIN_NAME + "_" + md5(err).substring(0, 6) + ".error.log";
        File root = new File(PLUGIN.getDataFolder(), "errors");
        if (!root.exists())
            root.mkdir();
        File dump = new File(root.getAbsoluteFile(), name);

        if (!dump.exists()) {
            try {
                dump.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                writer.write((err.toString() + "\n=========================================================").substring(1)); //Remove the extra /n
                writer.close();
            } catch (Exception e) {
                System.err.println("Ehm, errors occured while displaying an error >.< Stacktrace:\n");
                e.printStackTrace();
            }
        }
        err.append("\nThis has been saved to the file ./" + PLUGIN.getName() + "/errors/" + name);
        err.append("\n==========================================================");
        System.err.println(err);

        if (disable)
            ((Storm) PLUGIN).disable();
    }

    private static String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public static void initErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                LogRecord uhoh = new LogRecord(Level.SEVERE, "Uhoh");
                uhoh.setThrown(throwable);
                generateErrorLog(uhoh, "Error occured outside of Bukkit catchers.");
            }
        });
    }

    private static String md5(StringBuilder builder) {
        String hash = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(builder.toString().getBytes());
            hash = new BigInteger(1, m.digest()).toString(16);
            while (hash.length() < 32) {
                hash = 0 + hash;
            }
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }
}

package com.github.StormTeam.Storm;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;

public class ErrorLogger extends PluginLogger {

    String PLUGIN_NAME = "";
    String REGEX = "Task \\#\\d+ for Storm v[\\d.\\w]+ generated an exception";
    Pattern ERROR_REGEX = Pattern.compile(REGEX.toLowerCase());
    Plugin PLUGIN;

    public ErrorLogger(Plugin context) {
        super(context);
        this.PLUGIN = context;
        this.PLUGIN_NAME = PLUGIN.getName();
    }

    @Override
    public void log(LogRecord logRecord) {
        if (ERROR_REGEX.matcher(logRecord.getMessage().toLowerCase()).find()) {
            generateErrorLog(logRecord);
            return;
        }
        super.log(logRecord);
    }

    private void generateErrorLog(LogRecord record) {
        PluginDescriptionFile pdf = PLUGIN.getDescription();
        Server server = Bukkit.getServer();
        String error = "", name = PLUGIN_NAME + "_" + (record.hashCode() << 1024) + ".error.log";
        StringBuilder err = new StringBuilder();
        boolean disable = false;

        File root = new File(PLUGIN.getDataFolder(), "errors");
        if (!root.exists())
            root.mkdir();
        File dump = new File(root.getAbsoluteFile(), name);

        err.append("\n=============Storm has encountered an error!=============");
        err.append("\nStacktrace:\n");
        err.append((error = getStackTrace(record.getThrown())));
        err.append("\nStorm version: " + pdf.getVersion());
        err.append("\nPlugins loaded: " + Arrays.asList(server.getPluginManager().getPlugins()));
        err.append("\nCraftBukkit version: " + server.getBukkitVersion());
        err.append("\nStorm detected MC version: " + Storm.version);
        err.append("\nJava version: " + getProperty("java.version"));
        err.append("\nOS info: " + getProperty("os.arch") + " " + getProperty("os.name") + ", " + getProperty("os.version"));
        err.append("\nPlease report this error to the Storm ticket tracker (http://github.com/StormTeam/Storm/issues)!");
        error = error.toLowerCase();
        if (error.contains("nullpointerexception") || error.contains("stackoverflowexception")) {
            err.append("\nA critical error has been thrown. " + PLUGIN_NAME + " has been disabled to prevent further damage.");
            disable = true;
        } else {
            err.append("\nError was minor; " + PLUGIN_NAME + " will continue operating.");
        }
        err.append("\nThis has been saved to the file ./" + PLUGIN.getName() + "/errors/" + name);
        err.append("\n==========================================================");

        System.err.println(err);

        if (!dump.exists()) {
            try {
                Semaphore mutex = new Semaphore(1);
                mutex.acquire();
                dump.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                mutex.release();
                writer.write(err.toString().substring(1));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (disable)
            ((Storm) PLUGIN).disable();
    }

    private String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}

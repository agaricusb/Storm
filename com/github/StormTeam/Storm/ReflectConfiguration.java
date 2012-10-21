package com.github.StormTeam.Storm;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Semaphore;

/**
 * A class for saving/loading lazy configuration classes/files.
 * Based on codename_B's non static config 'offering' :-)
 */

public class ReflectConfiguration {

    /**
     * The plugin object to be used in config saving.
     */
    private final Plugin plugin;
    /**
     * The name of the configuration file.
     */
    private final String name;
    /**
     * A mutex to avoid file corruption when saving.
     */
    private final Semaphore mutex = new Semaphore(1);

    /**
     * Creates a ReflectConfiguration file based on given name
     *
     * @param plugin The plugin
     * @param name   The name of the file to write to
     */

    public ReflectConfiguration(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Loads the object.
     */

    public void load() {

        try {
            mutex.acquire();
            Storm.util.log("Loading configuration file: " + name);
            onLoad(plugin);
            mutex.release();
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

    }

    private void onLoad(Plugin plugin) throws Exception {

        File worldFolder = new File(plugin.getDataFolder() + File.separator + "worlds");
        if (!worldFolder.exists()) {
            if (!worldFolder.mkdir()) {
                ErrorLogger.generateErrorLog(new RuntimeException("Failed to create configuration directory!"));
            }
        }
        File worldFile = new File(worldFolder.getAbsoluteFile(), File.separator + name + ".yml");

        YamlConfiguration worlds = YamlConfiguration
                .loadConfiguration(worldFile);

        for (Field field : getClass().getDeclaredFields()) {
            String path = "Storm."
                    + field.getName().replaceAll("__", " ")
                    .replaceAll("_", ".");
            if (doSkip(field)) {
            } else if (worlds.isSet(path)) {
                field.set(this, worlds.get(path));
            } else {
                worlds.set(path, field.get(this));
            }
        }

        worlds.save(worldFile);
    }

    private boolean doSkip(Field field) {
        int mod = field.getModifiers();
        return Modifier.isTransient(mod)
                || Modifier.isStatic(mod)
                || Modifier.isFinal(mod)
                || Modifier.isPrivate(mod);
    }
}

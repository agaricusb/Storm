package com.github.StormTeam.Storm;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
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
            System.err.println("[Storm]Loading configuration file: " + name);
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

        YamlConfiguration worlds = YamlConfiguration.loadConfiguration(worldFile);

        for (Field field : getClass().getDeclaredFields()) {

            String path = "Storm."
                    + field.getName().replaceAll("__", " ")
                    .replaceAll("_", ".");

            if (worlds.isSet(path)) {
                LimitInteger lim;
                if ((lim = field.getAnnotation(LimitInteger.class)) != null) {
                    int limit = lim.limit();
                    boolean doCorrect = false;
                    try {
                        if (worlds.getInt(path) > limit) {
                            doCorrect = true;
                        }
                    } catch (Exception e) {
                        doCorrect = true;
                    }
                    if (doCorrect) {
                        System.err.println("[Storm]" + lim.warning().replace("%node", "'" + path.substring(6) + "'").replace("%limit", limit + ""));
                        if (lim.correct())
                            worlds.set(path, lim.limit());
                    }
                }
                Warn warm;
                if ((warm = field.getAnnotation(Warn.class)) != null) {
                    int kazi = warm.threshold();
                    if (worlds.getInt(path) > kazi) {
                        try {
                            System.err.println("[Storm]Node '" + path.substring(6) + "' is not reccomended to have a value above " + kazi + ".");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                field.set(this, worlds.get(path));
            } else
                worlds.set(path, field.get(this));
        }

        worlds.save(worldFile);
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Warn {
        int threshold() default 100;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LimitInteger {
        int limit() default 100;

        String warning() default "%node cannot be over %limit! Defaulted to value of %limit.";

        boolean correct() default true;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Comment {
        String value();

        CommentLocation location() default CommentLocation.INLINE;

        public enum CommentLocation {
            INLINE(1),
            TOP(2),
            BOTTOM(3);
            private int id;

            CommentLocation(int location) {
                this.id = location;
            }

            public int getID() {
                return id;
            }
        }
    }

}

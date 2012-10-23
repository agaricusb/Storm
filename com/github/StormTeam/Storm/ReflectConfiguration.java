/*
 * This file is part of Storm.
 *
 * Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Storm.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of Storm.
 *
 * Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Storm.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.github.StormTeam.Storm;

import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

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
            synchronized (this) {
                System.err.println("[Storm]Loading configuration file: " + name);
                onLoad(plugin);
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }

    }

    private void onLoad(Plugin plugin) throws Exception {
        synchronized (this) {
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
            Files.write(StringUtils.join(addComments(Files.toString(worldFile, Charset.defaultCharset()).split("\n")), "\n"), worldFile, Charset.defaultCharset());
        }
    }

    Collection<String> addComments(String[] lines) {
        try {
            int prevlevel = 0;
            final int indent = 2;
            LinkedList<String> outlines = new LinkedList<String>();
            Stack<String> hierarchy = new Stack<String>();
            for (String line : lines) {
                String content = StringUtils.stripStart(line, " ");
                int spaces = line.length() - content.length();
                int level = spaces / indent + 1;
                String[] tokens = content.split(":", 1);
                String name = tokens[0];
                if (level <= prevlevel) {
                    for (int i = 0; i <= prevlevel - level; ++i) {
                        hierarchy.pop();
                    }
                }
                hierarchy.push(name);
                prevlevel = level;
                String id = StringUtils.join(hierarchy, "_").replaceAll(" ", "__");

                Comment comment = this.getClass().getDeclaredField(id).getAnnotation(Comment.class);
                if (comment == null) {
                    outlines.add(line);
                    continue;
                }
                String indentPrefix = StringUtils.repeat(" ", spaces);
                if (comment.location() == Comment.CommentLocation.TOP) {
                    for (String data : comment.value())
                        outlines.add(indentPrefix + "# " + data);
                }
                if (comment.location() == Comment.CommentLocation.INLINE) {
                    String[] comments = comment.value();
                    outlines.add(line + " # " + comments[0]);
                    for (int i = 1; i < comments.length; ++i)
                        outlines.add(StringUtils.repeat(" ", line.length() + 1) + "# " + comments[i]);
                } else {
                    outlines.add(line);
                }
                if (comment.location() == Comment.CommentLocation.BOTTOM) {
                    for (String data : comment.value())
                        outlines.add(indentPrefix + "# " + data);
                }
            }
            return outlines;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        String[] value();

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

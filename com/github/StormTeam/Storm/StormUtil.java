package com.github.StormTeam.Storm;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.minecraft.server.Packet250CustomPayload;
import net.minecraft.server.WorldData;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An object that provides a plethora of utility functions used in Storm.
 *
 * @author Tudor, xiaomao
 */

public class StormUtil {

    /**
     * A WorldGuard plugin instance for block protection checking.
     */
    private WorldGuardPlugin wg;
    /**
     * Denotes whether the server has WorldGuard.
     */
    private boolean hasWG = false;
    /**
     * A HashMap with BlockTickSelectors for each loaded world.
     */
    private HashMap<String, BlockTickSelector> blockTickers = new HashMap<String, BlockTickSelector>();
    /**
     * Fields for weather control from net.minecraft.server.WorldData.
     */
    private Field isRaining, isThundering, rainTicks, thunderTicks;
    /**
     * The Storm Logger object.
     */
    private Logger log;

    /**
     * Creates a util object.
     *
     * @param plugin The plugin.
     */
    public StormUtil(Plugin plugin) {

        final Plugin wgp = plugin.getServer().getPluginManager().getPlugin(
                "WorldGuard");
        hasWG = wgp != null; // Short and sweet
        if (hasWG) {
            wg = (WorldGuardPlugin) wgp;
        }

        for (World w : Bukkit.getWorlds()) {
            String world = w.getName();
            BlockTickSelector ticker;
            try {
                ticker = new BlockTickSelector(w, 16);
                blockTickers.put(world, ticker);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            isRaining = WorldData.class.getDeclaredField("isRaining");
            isRaining.setAccessible(true);
            isThundering = WorldData.class.getDeclaredField("isThundering");
            isThundering.setAccessible(true);
            rainTicks = WorldData.class.getDeclaredField("rainTicks");
            rainTicks.setAccessible(true);
            thunderTicks = WorldData.class.getDeclaredField("rainTicks");
            thunderTicks.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log = plugin.getLogger();

    }

    /**
     * Controls minecraft's rain flag in world data.
     *
     * @param world Bukkit world object
     * @param flag  whether to set it to raining or not
     */
    public void setRainNoEvent(World world, boolean flag) {
        try {
            WorldData data = ((CraftWorld) world).getHandle().worldData;
            isRaining.set(data, flag);
            rainTicks.setInt(data, flag ? Integer.MAX_VALUE : 0);
        } catch (Exception ex) {
            world.setStorm(true); //Can still set the storm
        }
    }

    /**
     * Controls minecraft's thunderstorm flag in world data.
     *
     * @param world Bukkit world object
     * @param flag  whether to set it to thundering or not
     */
    public void setThunderNoEvent(World world, boolean flag) {
        try {
            WorldData data = ((CraftWorld) world).getHandle().worldData;
            isThundering.set(data, flag);
            thunderTicks.setInt(data, flag ? Integer.MAX_VALUE : 0);
        } catch (Exception ex) {
            world.setStorm(true); //Can still set the storm
        }
    }

    /**
     * Logs a message generated by Storm, with level INFO.
     *
     * @param logM Log message
     */
    public void log(String logM) {
        log.log(Level.INFO, logM);
    }

    /**
     * Logs a message generated by Storm.
     *
     * @param level Log level
     * @param logM  Log message
     */
    public void log(Level level, String logM) {
        log.log(level, logM);
    }

    /**
     * Broadcasts a message to all players.
     *
     * @param message message to send
     */
    public void broadcast(String message) {
        if (!message.isEmpty()) {
            Bukkit.getServer().broadcastMessage(parseColors(message));
        }
    }

    /**
     * Broadcasts a message to all players in a world.
     *
     * @param message message to send
     * @param world   Bukkit world object
     */
    public void broadcast(String message, World world) {
        for (Player p : world.getPlayers()) {
            message(p, message);
        }
    }

    /**
     * Broadcasts a message to all players in a world.
     *
     * @param message message to send
     * @param world   world name
     */
    public void broadcast(String message, String world) {
        broadcast(message, Bukkit.getWorld(world));
    }

    /**
     * Messages a player.
     *
     * @param player  player name
     * @param message message to send
     */
    void message(Player player, String message) {
        if (!message.isEmpty()) {
            player.sendMessage(parseColors(message));
        }
    }

    /**
     * Translates the color codes in given string.
     *
     * @param msg the string to convert
     * @return the converted string
     */
    String parseColors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Damages a player within range.
     *
     * @param location the centre of the range
     * @param radius   the radius where players within are damaged
     * @param damage   the amount of damage to cause
     * @param message  the message to notify the player about the cause of the damage
     */
    public void damageNearbyPlayers(Location location, double radius, int damage, String message) {
        for (Player p : getNearbyPlayers(location, radius)) {
            damagePlayer(p, message, damage);
        }
    }

    /**
     * Damages a player within range, if the player doesn't have a permission.
     *
     * @param location   the centre of the range
     * @param radius     the radius where players within are damaged
     * @param damage     the amount of damage to cause
     * @param message    the message to notify the player about the cause of the damage
     * @param permission the permission the user must have to avoid damage
     */
    public void damageNearbyPlayers(Location location, double radius, int damage, String message, String permission) {
        for (Player p : getNearbyPlayers(location, radius)) {
            if (!p.hasPermission(permission)) {
                damagePlayer(p, message, damage);
            }
        }
    }

    /**
     * Damages a player.
     *
     * @param player  Bukkit player object
     * @param message message to send to player
     * @param damage  damage to cause on player, in hearts
     */
    public void damagePlayer(Player player, String message, int damage) {
        if (player.getGameMode() != GameMode.CREATIVE && player.getHealth() != 0) {
            player.damage(damage * 2);
            this.message(player, message);
        }
    }

    /**
     * Checks if a block is protected by WorldGuard.
     *
     * @param block Bukkit block object to check
     * @return boolean of wheather it's protected
     */
    public boolean isBlockProtected(Block block) {
        return hasWG && wg.getGlobalRegionManager().get(block.getWorld()).getApplicableRegions(BukkitUtil.toVector(block.getLocation())).size() > 0;
    }

    /**
     * Gets player within range.
     *
     * @param location the centre of the range
     * @param radius   the radius of the range
     * @return a set of Bukkit player objects
     */
    Set<Player> getNearbyPlayers(Location location, double radius) {
        Set<Player> playerList = new HashSet<Player>();
        World locWorld = location.getWorld();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getWorld().equals(locWorld)) {
                if (p.getLocation().distance(location) <= radius) {
                    playerList.add(p);
                }
            }
        }

        return playerList;
    }

    /**
     * Applies a list of transformation on a block, if the block is not protected.
     *
     * @param toTransform     the Bukkit block object to transform
     * @param transformations the list if transformations to apply
     */
    public void transform(Block toTransform, List<List<String>> transformations) {
        if (isBlockProtected(toTransform)) {
            return;
        }

        for (List<String> toCheck : transformations) {
            ArrayList<String[]> stateIndex = new ArrayList<String[]>();

            for (int i = 0; i != 2; ++i) {
                String got = toCheck.get(i);

                if (got.contains(":")) { // Check for data value appended.
                    stateIndex.add(got.split(":"));
                } else {
                    stateIndex.add(new String[]{got, "0"});
                }
            }

            String[] curState = stateIndex.get(0), toState = stateIndex.get(1);

            if (Integer.valueOf(curState[0]) == toTransform.getTypeId()
                    && Integer.valueOf(curState[1]) == toTransform.getData()) {
                toTransform.setTypeIdAndData(Integer.valueOf(toState[0]), Byte
                        .parseByte(toState[1]), true);
                return;
            }

        }

    }

    /**
     * Selects a random loaded chunk in world.
     *
     * @param world Bukkit world object
     * @return the chunk selected
     */
    public Chunk pickChunk(World world) {
        Chunk[] loadedChunks = world.getLoadedChunks();
        return loadedChunks[Storm.random.nextInt(loadedChunks.length)];
    }

    /**
     * Sets a texture on a player, if the server is 1.3 or above
     *
     * @param player  Player object
     * @param texture URI to texture
     */
    public void setTexture(Player player, String texture) {
        if (Storm.version >= 1.3) {
            ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet250CustomPayload("MC|TPack", (texture + "\0" + 16).getBytes()));
        }
    }

    /**
     * Clears the texture on a player. In reality, sets the texture to default.
     *
     * @param player Bukkit player object
     */
    public void clearTexture(Player player) {
        setTexture(player, Storm.wConfigs.get(player.getWorld().getName()).Textures_Default__Texture__Path);
    }

    /**
     * Checks if a player is exposed to the sky, i.e. aboive ground.
     *
     * @param player Bukkit player object
     * @return boolean
     */
    public boolean isPlayerUnderSky(Player player) {
        Location loc = player.getLocation();
        return player.getWorld().getHighestBlockYAt(loc) <= loc.getBlockY();

    }

    /**
     * Gets a list of ticked blocks.
     *
     * @param world Bukkit world object
     * @return a List of blocks
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<Block> getRandomTickedBlocks(World world)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return blockTickers.get(world.getName()).getRandomTickedBlocks();
    }

    /**
     * Checks if the location is near a block in the list on the same layer.
     *
     * @param location the location
     * @param blocks   the collection of blocks to search
     * @param radius   the radius to search
     * @return boolean
     */
    public boolean isLocationNearBlock(Location location, Collection<Integer> blocks, int radius) {
        World world = location.getWorld();
        int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();

        for (int ox = 0; ox > -radius; ox--)
            for (int oz = 0; oz > -radius; oz--)
                if (blocks.contains(world.getBlockAt(x + ox, y, z + oz).getTypeId()))
                    return true;
        return false;
    }

    /**
     * Returns a HashSet containing all the arguments passed in.
     *
     * @param objects All the objects to include.
     * @param <T>     The type of all the objects.
     * @return a Set of all the elements
     */
    public static <T> Set<T> asSet(T... objects) {
        Set<T> set = new HashSet<T>();
        Collections.addAll(set, objects);
        return set;
    }

    /**
     * All biomes that rain can fall in.
     */
    private final Set<Biome> rainBiomes = asSet(Biome.EXTREME_HILLS,
            Biome.FOREST, Biome.FOREST_HILLS,
            Biome.JUNGLE,
            Biome.JUNGLE_HILLS, Biome.MUSHROOM_ISLAND,
            Biome.MUSHROOM_SHORE,
            Biome.PLAINS, Biome.OCEAN, Biome.RIVER,
            Biome.SWAMPLAND, Biome.SKY,
            Biome.SMALL_MOUNTAINS);
    /**
     * All desert biomes.
     */
    private final Set<Biome> desertBiomes = asSet(Biome.DESERT, Biome.DESERT_HILLS);
    /**
     * All forest biomes.
     */
    private final Set<Biome> forestBiomes = asSet(Biome.FOREST, Biome.FOREST_HILLS);
    /**
     * All jungle biomes.
     */
    private final Set<Biome> jungleBiomes = asSet(Biome.JUNGLE_HILLS, Biome.MUSHROOM_ISLAND);
    /**
     * All snow biomes.
     */
    private final Set<Biome> snowyBiomes = asSet(Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER,
            Biome.ICE_MOUNTAINS,
            Biome.ICE_PLAINS, Biome.TAIGA,
            Biome.TAIGA_HILLS);

    /**
     * Can the biome rain?
     *
     * @param b biome to check
     * @return boolean
     */
    public boolean isRainy(Biome b) {
        return rainBiomes.contains(b);
    }

    /**
     * Is the biome a forest?
     *
     * @param b biome to check
     * @return boolean
     */
    public boolean isForest(Biome b) {
        return forestBiomes.contains(b) || jungleBiomes.contains(b);
    }

    /**
     * Is the biome a desert?
     *
     * @param b biome to check
     * @return boolean
     */
    public boolean isDesert(Biome b) {
        return desertBiomes.contains(b);
    }

    /**
     * Is the biome covered with snow?
     *
     * @param b biome to check
     * @return boolean
     */
    public boolean isTundra(Biome b) {
        return snowyBiomes.contains(b);
    }

    /**
     * Can the biome snow?
     *
     * @param b biome to check
     * @return boolean
     */
    public boolean isSnowy(Biome b) {
        return isTundra(b);
    }
}

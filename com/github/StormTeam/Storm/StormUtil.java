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

import com.bekvon.bukkit.residence.Residence;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.minecraft.server.*;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that provides a plethora of utility functions used in Storm.
 *
 * @author Icyene, xiaomao
 */

public class StormUtil {

    private static WorldGuardPlugin wg;
    private static boolean hasWG = false;
    private static boolean hasResidence = false;
    private static Field isRaining, isThundering, rainTicks, thunderTicks;
    private static Method explode;
    private static Logger log;

    static {
        try {
            Plugin wgp = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
            hasWG = wgp != null; // Short and sweet
            if (hasWG)
                wg = (WorldGuardPlugin) wgp;
            hasResidence = Bukkit.getServer().getPluginManager().getPlugin("Residence") != null;

            isRaining = WorldData.class.getDeclaredField("isRaining");
            isRaining.setAccessible(true);
            isThundering = WorldData.class.getDeclaredField("isThundering");
            isThundering.setAccessible(true);
            rainTicks = WorldData.class.getDeclaredField("rainTicks");
            rainTicks.setAccessible(true);
            thunderTicks = WorldData.class.getDeclaredField("thunderTicks");
            thunderTicks.setAccessible(true);

            if (Storm.version >= 1.4D) {
                explode = net.minecraft.server.World.class.getDeclaredMethod("createExplosion",
                        net.minecraft.server.Entity.class, double.class, double.class, double.class, float.class, boolean.class, boolean.class);
            } else {
                explode = net.minecraft.server.World.class.getDeclaredMethod("createExplosion",
                        net.minecraft.server.Entity.class, double.class, double.class, double.class, float.class, boolean.class);
            }

            log = Storm.instance.getLogger();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls minecraft's rain flag in world data.
     *
     * @param world Bukkit world object
     * @param flag  whether to set it to raining or not
     */
    public static void setRainNoEvent(World world, boolean flag) {
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
    public static void setThunderNoEvent(World world, boolean flag) {
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
    public static void log(String logM) {
        log.log(Level.INFO, logM);
    }

    /**
     * Logs a message generated by Storm.
     *
     * @param level Log level
     * @param logM  Log message
     */
    public static void log(Level level, String logM) {
        log.log(level, logM);
    }

    /**
     * Broadcasts a message to all players.
     *
     * @param message message to send
     */
    public static void broadcast(String message) {
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
    public static void broadcast(String message, World world) {
        log("(" + world.getName().toUpperCase() + ") " + message);
        if (world != null)
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
    public static void broadcast(String message, String world) {
        World bw;
        if ((bw = Bukkit.getWorld(world)) == null)
            return;
        log(message);
        broadcast(message, bw);
    }

    /**
     * Messages a player.
     *
     * @param player  player name
     * @param message message to send
     */
    public static void message(Player player, String message) {
        player.sendMessage(parseColors(message));
    }

    /**
     * Translates the color codes in given string.
     *
     * @param msg the string to convert
     * @return the converted string
     */
    public static String parseColors(String msg) {
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
    public static void damageNearbyPlayers(Location location, double radius, int damage, String message) {
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
    public static void damageNearbyPlayers(Location location, double radius, int damage, String message, String permission) {
        for (Player p : getNearbyPlayers(location, radius)) {
            if (!p.hasPermission(permission)) {
                damagePlayer(p, message, damage);
            }
        }
    }

    public static void playSound(Player to, String sound, Location loc, float pitch, float volume) {
        ((CraftPlayer) to).getHandle().netServerHandler.sendPacket(new Packet62NamedSoundEffect(sound, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pitch, volume));
    }

    public static void playSound(Player to, String sound, float pitch, float volume) {
        playSound(to, sound, to.getLocation(), pitch, volume);
    }

    public static void playSoundNearby(Location origin, double radius, String sound, float pitch, float volume) {
        for (Player p : getNearbyPlayers(origin, radius)) {
            playSound(p, sound, origin, pitch, volume);
        }
    }

    /**
     * Damages a player.
     *
     * @param player  Bukkit player object
     * @param message message to send to player
     * @param damage  damage to cause on player, in hearts
     */
    public static void damagePlayer(Player player, String message, int damage) {
        if (player.getGameMode() != GameMode.CREATIVE && player.getHealth() != 0) {
            player.damage(damage * 2);
            message(player, message);
        }
    }


    public static void setRenderDistance(World world, int distance) {
        try {

            net.minecraft.server.WorldServer cWorld = ((CraftWorld) world).getHandle();

            Field viewDistance = PlayerManager.class.getDeclaredField("e");
            viewDistance.setAccessible(true);


            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(viewDistance, viewDistance.getModifiers() & ~Modifier.FINAL);

            Field manager = WorldServer.class.getDeclaredField("manager");
            manager.setAccessible(true);

            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(manager, manager.getModifiers() & ~Modifier.FINAL);

            viewDistance.set(manager.get(cWorld), distance);


        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * Checks if a block is protected by WorldGuard.
     *
     * @param block Bukkit block object to check
     * @return boolean of wheather it's protected
     */
    public static boolean isBlockProtected(Block block) {
        return (hasWG && wg.getGlobalRegionManager().get(block.getWorld()).getApplicableRegions(BukkitUtil.toVector(block.getLocation())).size() > 0) ||
                (hasResidence && Residence.getResidenceManager().getByLoc(block.getLocation()) != null);
    }

    /**
     * Gets player within range.
     *
     * @param location the centre of the range
     * @param radius   the radius of the range
     * @return a set of Bukkit player objects
     */
    public static Set<Player> getNearbyPlayers(Location location, double radius) {
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

    public static void createExplosion(net.minecraft.server.Entity entity, double locX, double locY, double locZ, float power, boolean incendiary) {
        try {
            if (Storm.version > 1.3D)
                explode.invoke(entity.world, entity, locX, locY, locZ, power, incendiary, true);
            else
                explode.invoke(entity.world, entity, locX, locY, locZ, power, incendiary);
        } catch (Exception e) {
            entity.world.getWorld().createExplosion(locX, locY, locZ, power);
            ErrorLogger.generateErrorLog(e);
        }
    }

    /**
     * Applies a list of transformation on a block, if the block is not protected.
     *
     * @param toTransform     the Bukkit block object to transform
     * @param transformations the list if transformations to apply
     */
    public static void transform(Block toTransform, List<List<String>> transformations) {
        if (isBlockProtected(toTransform)) {
            return;
        }

        for (List<String> toCheck : transformations) {
            ArrayList<String[]> stateIndex = new ArrayList<String[]>();

            for (int i = 0; i != 2; ++i) {
                String got = toCheck.get(i);

                if (got.contains(":")) { // Check for data _ appended.
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
     * Selects a random isFromFile chunk in world.
     *
     * @param world Bukkit world object
     * @return the chunk selected
     */
    public static Chunk pickChunk(World world) {
        Chunk[] loadedChunks = world.getLoadedChunks();
        return loadedChunks[Storm.random.nextInt(loadedChunks.length)];
    }

    /**
     * Sets a texture on a player, if the server is 1.3 or above
     *
     * @param player  Player object
     * @param texture URI to texture
     */
    public static void setTexture(Player player, String texture) {
        if (Storm.version >= 1.3 && Storm.wConfigs.get(player.getWorld().getName()).Features_Force__Weather__Textures) {
            ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet250CustomPayload("MC|TPack", (texture + "\0" + 16).getBytes()));
        }
    }

    /**
     * Clears the texture on a player. In reality, sets the texture to default.
     *
     * @param player Bukkit player object
     */
    public static void clearTexture(Player player) {
        setTexture(player, Storm.wConfigs.get(player.getWorld().getName()).Textures_Default__Texture__Path);
    }

    /**
     * Checks if a player is exposed to the sky, i.e. above ground.
     *
     * @param player Bukkit player object
     * @return boolean
     */
    public static boolean isPlayerUnderSky(Player player) {
        return isLocationUnderSky(player.getLocation());
    }

    /**
     * Checks if a entity is exposed to the sky, i.e. above ground.
     *
     * @param entity Bukkit entity object
     * @return boolean
     */
    public static boolean isEntityUnderSky(Entity entity) {
        return isLocationUnderSky(entity.getLocation());
    }

    /**
     * Checks if a location is exposed to sky, i.e. above ground.
     *
     * @param loc Bukkit location object
     * @return boolean
     */

    public static boolean isLocationUnderSky(Location loc) {
        return loc.getWorld().getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    /**
     * Checks if the location is near a block in the list on the same layer.
     *
     * @param location the location
     * @param blocks   the collection of blocks to search
     * @param radius   the radius to search
     * @return boolean
     */
    public static boolean isLocationNearBlock(Location location, Collection<Integer> blocks, int radius) {
        World world = location.getWorld();
        int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();

        for (int ox = radius; ox > -radius; ox--)
            for (int oz = radius; oz > -radius; oz--)
                if (blocks.contains(world.getBlockAt(x + ox, y, z + oz).getTypeId()))
                    return false;
        return true;
    }

    public static Location getSurface(Location location) {
        // TODO finish
        List surfaceMaterials = Arrays.asList(0, 2, 3);
        Location out = location.clone();
        out.setY(80);
        Block u = location.getBlock();
        //new BlockIterator(world, block, new Vector(0, 1, 0), 0, (256 - block.getBlockY()));
        BlockIterator bi = new BlockIterator(out.getWorld(), out.toVector(), new org.bukkit.util.Vector(0, 1, 0), 0, (256 - out.getBlockY()));

        HashMap<Integer, Location> bestMatch = new HashMap<Integer, Location>();

        while (bi.hasNext()) {
            Block b = bi.next();
            int c = 0;
            if (b.getY() > 64)
                c++;
            if (StormUtil.isLocationNearBlock(b.getLocation(), surfaceMaterials, 2))
                c += 2;
        }

        return location;
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
    public static final Set<Biome> rainBiomes = asSet(Biome.EXTREME_HILLS,
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
    public static final Set<Biome> desertBiomes = asSet(Biome.DESERT, Biome.DESERT_HILLS);
    /**
     * All forest biomes.
     */
    public static final Set<Biome> forestBiomes = asSet(Biome.FOREST, Biome.FOREST_HILLS);
    /**
     * All jungle biomes.
     */
    public static final Set<Biome> jungleBiomes = asSet(Biome.JUNGLE_HILLS, Biome.MUSHROOM_ISLAND);
    /**
     * All snow biomes.
     */
    public static final Set<Biome> snowyBiomes = asSet(Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER,
            Biome.ICE_MOUNTAINS,
            Biome.ICE_PLAINS, Biome.TAIGA,
            Biome.TAIGA_HILLS);

    /**
     * Can the biome rain?
     *
     * @param b biome to check
     * @return boolean
     */
    public static boolean isRainy(Biome b) {
        return rainBiomes.contains(b);
    }

    /**
     * Is the biome a forest?
     *
     * @param b biome to check
     * @return boolean
     */
    public static boolean isForest(Biome b) {
        return forestBiomes.contains(b) || jungleBiomes.contains(b);
    }

    /**
     * Is the biome a desert?
     *
     * @param b biome to check
     * @return boolean
     */
    public static boolean isDesert(Biome b) {
        return desertBiomes.contains(b);
    }

    /**
     * Is the biome covered with snow?
     *
     * @param b biome to check
     * @return boolean
     */
    public static boolean isTundra(Biome b) {
        return snowyBiomes.contains(b);
    }

    /**
     * Can the biome snow?
     *
     * @param b biome to check
     * @return boolean
     */
    public static boolean isSnowy(Biome b) {
        return isTundra(b);
    }
}

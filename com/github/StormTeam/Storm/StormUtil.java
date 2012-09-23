package com.github.StormTeam.Storm;

import com.github.StormTeam.Storm.Storm;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.Packet250CustomPayload;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class StormUtil extends BiomeGroups
{
	public final Logger log = Logger.getLogger("Storm");
	private final Random rand = new Random();
	private WorldGuardPlugin wg;
	private boolean hasWG = false;
	private HashMap<String, BlockTickSelector> blockTickers = new HashMap<String, BlockTickSelector>();

	/** Creates a util object.
	 * @param plugin
	 *            The plugin. */
	public StormUtil(Plugin plugin)
	{

		final Plugin wgp = plugin.getServer().getPluginManager().getPlugin(
		        "WorldGuard");
		hasWG = wgp == null ? false : true; // Short and sweet
		if (hasWG)
		{
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

	}

	/** Logs.
	 * @param logM
	 *            The message. */

	public void log(String logM)
	{
		log.log(Level.INFO, logM);
	}

	/** Logs.
	 * @param level
	 *            The severity level.
	 * @param logM
	 *            The log message. */

	public void log(Level level, String logM)
	{
		log.log(level, logM);
	}

	/** Broadcasts a message.
	 * @param message
	 *            The message. */

	public void broadcast(String message)
	{
		if (message.isEmpty())
		{
			return;
		}
		Bukkit.getServer().broadcastMessage(parseColors(message));
	}

	/** Send ChatColor formatted message to player.
	 * @param player
	 *            The player.
	 * @param message
	 *            The message. */

	public void message(Player player, String message)
	{
		if (message.isEmpty())
		{
			return;
		}

		player.sendMessage(parseColors(message));
	}

	/** Parses colors in string.
	 * @param msg
	 *            The string.
	 * @return The formatted string. */

	public String parseColors(String msg)
	{
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/** Damages players in radius from given location.
	 * @param location
	 *            The location.
	 * @param radius
	 *            The radius.
	 * @param damage
	 *            These values are obvious... The damage.
	 * @param message
	 *            Message given to the player when damaged. */

	public void damageNearbyPlayers(Location location, double radius,
	        int damage, String message)
	{

		ArrayList<Player> damagees = getNearbyPlayers(location, radius);

		for (Player p : damagees)
		{

			if (p.getGameMode() != GameMode.CREATIVE)
			{

				p.damage(damage * 2);

				if (message.isEmpty())
				{
					return;
				}

				this.message(p, message);

			}
		}
	}

	/** Wrapper method around WG to check if any regions apply to given block.
	 * @param b
	 *            The block.
	 * @return
	 *         True if protected, false otherwise. */

	public boolean isBlockProtected(Block b)
	{

		if (!hasWG)
		{
			return false;
		}

		RegionManager mgr = wg.getGlobalRegionManager().get(b.getWorld());
		if (mgr.getApplicableRegions(BukkitUtil.toVector(b.getLocation()))
		        .size() > 0)
		{
			return true;

		} else
		{
			return false;
		}

	}

	public ArrayList<Player> getNearbyPlayers(Location location,
	        double radius)
	{

		ArrayList<Player> playerList = new ArrayList<Player>();

		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getWorld().equals(location.getWorld()))
			{
				Location ploc = p.getLocation();
				ploc.setY(location.getY());
				if (ploc.distance(location) <= radius)
				{
					playerList.add(p);

				}
			}
		}

		return playerList;
	}

	public void transform(Block toTransform, List<List<String>> transformations)
	{

		if (isBlockProtected(toTransform))
			return;

		for (List<String> toCheck : transformations)
		{
			ArrayList<String[]> stateIndex = new ArrayList<String[]>();

			for (int i = 0; i != 2; ++i)
			{
				String got = toCheck.get(i);

				if (got.contains(":")) // Check for data value appended.
				{
					stateIndex.add(got.split(":"));
				} else
				{
					stateIndex.add(new String[] { got, "0" });
				}
			}

			final String[] curState = stateIndex.get(0), toState = stateIndex
			        .get(1);

			if (Integer.valueOf(curState[0]) == toTransform.getTypeId()
			        && Integer.valueOf(curState[1]) == toTransform.getData())
			{
				toTransform.setTypeIdAndData(Integer.valueOf(toState[0]), Byte
				        .parseByte(toState[1]), true);
				return;
			}

		}

	}

	/** Gets random chunk in given world.
	 * @param w
	 *            The world.
	 * @return a chunk. */

	public Chunk pickChunk(World w)
	{
		final Chunk[] loadedChunks = w.getLoadedChunks();
		return loadedChunks[rand.nextInt(loadedChunks.length)];

	}

	/** Sets a texture on given player.
	 * @param toSetOn
	 *            The player.
	 * @param pathToTexture
	 *            A URL to texture pack. Won't work with https. */

	public void setTexture(Player toSetOn, String pathToTexture)
	{
		((CraftPlayer) toSetOn).getHandle().netServerHandler
		        .sendPacket(new Packet250CustomPayload("MC|TPack",
		                (pathToTexture + "\0" + 16).getBytes()));
	}

	/** Sets player texture pack to default.
	 * @param toClear
	 *            The player to set. */

	public void clearTexture(Player toClear)
	{
		setTexture(
		        toClear,
		        Storm.wConfigs.get(toClear.getWorld().getName()).Textures_Default__Texture__Path);
	}

	/** Checks if a player is visible to sky.
	 * @param player
	 *            The player to check.
	 * @return True if visible, false otherwise. */

	public boolean isPlayerUnderSky(Player player)
	{
		final World world = player.getWorld();
		if (world.hasStorm())
		{
			final Location loc = player.getLocation();
			final Biome biome = world
			        .getBiome(loc.getBlockX(), loc.getBlockZ());
			if (Storm.biomes.isRainy(biome)
			        && world.getHighestBlockYAt(loc) <= loc.getBlockY())
			{
				return true;
			}
		}
		return false;
	}

	/** Interface method against MineCraft block selection. Damn fast.
	 * @param world
	 *            The world to return blocks of.
	 * @return Returns an ArrayList<Block> of the ticked blocks. Only 1/16 of
	 *         these should be modified.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException */

	public ArrayList<Block> getRandomTickedBlocks(World world)
	        throws IllegalArgumentException, IllegalAccessException,
	        InvocationTargetException {

		return blockTickers.get(world.getName()).getRandomTickedBlocks();

	}

}
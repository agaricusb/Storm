package com.github.StormTeam.Storm.Lightning;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An object for utility methods used by better lightning.
 */
public class LightningUtils {

    /**
     * Creates a lightning utility object.
     */

    public LightningUtils() {
    }

    /**
     * Gets a location of a metallic block near given location.
     *
     * @param oldLoc The given location to search around
     * @return A Location of a block
     */

    public Location hitMetal(Location oldLoc) {
        Chunk chunk = pickChunk(oldLoc.getWorld());
        if (chunk != null) {
            Location loc = pickLightningRod(chunk);
            if (loc != null) {
                return loc;
            }
        }
        return oldLoc;
    }

    /**
     * Gets a location of a player near given location.
     *
     * @param oldLoc The given location to search around
     * @return A Location of a player
     */

    public Location hitPlayers(Location oldLoc) {
        final Location chunk = pickChunk(oldLoc.getWorld()).getBlock(8, 255, 8)
                .getLocation();
        GlobalVariables glob = Storm.wConfigs.get(oldLoc.getWorld().getName());

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Location playerLocation = new Location(p.getWorld(), p.getLocation().getX(),
                    255, p.getLocation().getZ());
            if (chunk.distance(playerLocation) <= 40 && !p.hasPermission("storm.lightning.immune")) {
                for (int id : glob.Lightning_Attraction_Players_Attractors) {
                    if (p.getInventory().getItemInHand().getTypeId() == id
                            || Arrays.asList(
                            p.getInventory().getArmorContents())
                            .contains(new ItemStack(id))) {
                        return p.getLocation();
                    }
                }
            }
        }
        return oldLoc;
    }

    private Location pickLightningRod(Chunk chunk) {
        ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, false, false);
        List<Location> list = findLightningRods(chunk);
        if ((list != null) && (!list.isEmpty())) {
            return list.get(Storm.random.nextInt(list.size()));
        }
        Entity[] entities = chunk.getEntities();
        if (entities != null) {
            for (Entity entity : entities) {
                Location loc = entity.getLocation();
                int y = snapshot.getHighestBlockYAt(loc.getBlockX() & 0xF,
                        loc.getBlockZ() & 0xF);
                if (loc.getBlockY() < y - 1) {
                    continue;
                }

                if (entity instanceof Minecart) {
                    return loc;
                }
            }
        }

        return null;
    }

    private List<Location> findLightningRods(Chunk chunk) {
        ArrayList<Location> list = new ArrayList<Location>();
        ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, false, false);
        GlobalVariables glob = Storm.wConfigs.get(chunk.getWorld().getName());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = snapshot.getHighestBlockYAt(x, z);
                int type = snapshot.getBlockTypeId(x, y, z);

                if (glob.Lightning_Attraction_Blocks_Attractors
                        .contains(type)) {
                    list.add(chunk.getBlock(x, y, z).getLocation());
                } else {
                    if (y <= 0) {
                        continue;
                    }
                    y--;
                    type = snapshot.getBlockTypeId(x, y, z);
                    if (glob.Lightning_Attraction_Blocks_Attractors
                            .contains(type)) {
                        list.add(chunk.getBlock(x, y, z).getLocation());
                    }
                }
            }
        }
        return list;
    }

    private Chunk pickChunk(World world) {
        List<Player> players = world.getPlayers();
        if ((players == null) || (players.isEmpty())) {
            return null;
        }
        Player player = players.get(Storm.random.nextInt(players.size()));

        List<Block> blocks = player.getLastTwoTargetBlocks(null, 100);
        if ((blocks != null) && (Storm.random.nextInt(100) < 30)) {
            return world.getChunkAt(
                    (blocks.get(Storm.random.nextInt(blocks.size()))).getLocation());
        }
        return world.getChunkAt(player.getLocation());
    }
}

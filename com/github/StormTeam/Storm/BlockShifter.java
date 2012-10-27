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

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockShifter {

    public static boolean setBlockFast(Block b, int typeId) {
        Chunk c = b.getChunk();
        net.minecraft.server.Chunk chunk = ((CraftChunk) c).getHandle();
        return chunk.a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId);
    }

    public static boolean setBlockFast(Block b, int typeId, byte data) {
        Chunk c = b.getChunk();
        net.minecraft.server.Chunk chunk = ((CraftChunk) c).getHandle();
        return chunk.a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
    }

    public static void sendClientChanges(World world) {
        List<ChunkCoordIntPair> pairs = new ArrayList<ChunkCoordIntPair>();
        for (Chunk c : world.getLoadedChunks()) {
            pairs.add(new ChunkCoordIntPair(c.getX(), c.getZ()));
        }
        for (Player player : world.getPlayers()) {
            queueChunks(((CraftPlayer) player).getHandle(), pairs);
        }
    }

    private static void queueChunks(EntityPlayer ep, List<ChunkCoordIntPair> pairs) {
        Set<ChunkCoordIntPair> queued = new HashSet<ChunkCoordIntPair>();
        for (Object o : ep.chunkCoordIntPairQueue) {
            queued.add((ChunkCoordIntPair) o);
        }
        for (ChunkCoordIntPair pair : pairs) {
            if (!queued.contains(pair)) {
                ep.chunkCoordIntPairQueue.add(pair);
            }
        }
    }
}


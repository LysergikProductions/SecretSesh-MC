package secretmc.backend.utils;

/* *
 *
 *  About: Chunk-related utils for RVAS-Core
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import secretmc.backend.Config;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Chunks {

    public static int banBlockCounter(Chunk chunk) {
        int counter = 0;

        try {
            for (int y = 255; y >= 0; y--) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        Material thisMat = chunk.getBlock(x, y, z).getType();

                        if (
                                thisMat.equals(Material.FURNACE) ||
                                thisMat.equals(Material.BLAST_FURNACE) ||
                                thisMat.equals(Material.SMOKER) ||
                                thisMat.equals(Material.ENCHANTING_TABLE)) {

                            counter++;
                        }
                    }
                }
            } return counter;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return counter;
        }
    }

    public static int banBlockRemover(Chunk chunk, int limiter) {
        int counter = 0;

        for (int y = 255; y >= 0; y--) {
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    Block thisBlock = chunk.getBlock(x, y, z);

                    if (
                            thisBlock.getType().equals(Material.FURNACE) ||
                            thisBlock.getType().equals(Material.BLAST_FURNACE) ||
                            thisBlock.getType().equals(Material.SMOKER) ||
                            thisBlock.getType().equals(Material.ENCHANTING_TABLE)) {

                        thisBlock.setType(Material.AIR);
                        counter++;
                    }
                    if (counter >= limiter) return counter;
                }
            }
        }
        return counter;
    }

    public static int blockCounter(Chunk chunk, Material block) {
        int counter = 0;

        try {
            for (int y = 0; y <= 255; y++) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {
                        if(chunk.getBlock(x, y, z).getType() == block) counter++;
                    }
                }
            } return counter;

        } catch (Exception e) {
            if (Config.debug) e.printStackTrace();
            return counter;
        }
    }

    public static int getExitFloor(Chunk chunk) {
        int y = 257;

        while (y > 1) {
            Material topBlock = chunk.getWorld().getBlockAt(1, y, 1).getType();
            Material bottomBlock = chunk.getWorld().getBlockAt(1, y-1, 1).getType();

            if (topBlock.equals(Material.AIR) ||
                    bottomBlock.equals(Material.AIR)) y--;
            else if (topBlock.equals(Material.BEDROCK) &&
                    !bottomBlock.equals(Material.BEDROCK)) return y;
            else y--;
        }
        return -1;
    }

    private static Map<String, Chunk> getChunk3x3(Chunk chunk) {
        @NotNull World world = chunk.getWorld();

        Map<String, Chunk> out = new HashMap<>();{
            out.put("C", chunk);
            out.put("N", world.getChunkAt(chunk.getX(), chunk.getZ() - 1));
            out.put("NE", world.getChunkAt(chunk.getX() + 1, chunk.getZ() - 1));
            out.put("E", world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
            out.put("SE", world.getChunkAt(chunk.getX() + 1, chunk.getZ() + 1));
            out.put("S", world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
            out.put("SW", world.getChunkAt(chunk.getX() - 1, chunk.getZ() + 1));
            out.put("W", world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
            out.put("NW", world.getChunkAt(chunk.getX() - 1, chunk.getZ() - 1));
        }
        return out;
    }

    // clears a 3x3 chunk grid around the provided chunk
    public static int clearChunkItems(Chunk chunk) {
        Map<String, Chunk> chunks_3x3 = getChunk3x3(chunk);

        int counter = 0;
        for (Chunk thisChunk: chunks_3x3.values()) {
            for (Entity entity: thisChunk.getEntities()) {
                if (entity.getType().equals(EntityType.DROPPED_ITEM)) {

                    entity.remove();
                    counter++;
                }
            }
        }
        return counter;
    }

    // TODO: make sure to change to counting lag blocks and not ban blocks
    public static int countChunkLagBlocks(Player thisPlayer) {

        Chunk playerChunk = thisPlayer.getChunk();
        Map<String, Chunk> chunks_3x3 = getChunk3x3(playerChunk);

        int count = 0;
        for (Chunk thisChunk: chunks_3x3.values()) {
            count += banBlockCounter(thisChunk);
        }
        return count;
    }

    @Deprecated
    public static int blocksCounter(Chunk chunk, Material[] blocks) {
        int counter = 0;

        try {
            for (int y = 255; y >= 0; y--) {
                for (int x = 0; x <= 15; x++) {
                    for (int z = 0; z <= 15; z++) {

                        if (Arrays.stream(blocks).parallel()
                                .anyMatch(Predicate.isEqual(chunk.getBlock(x, y, z).getType()))) {
                            counter++;
                        }
                    }
                }
            } return counter;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return counter;
        }
    }

    @Deprecated
    public static int blockRemover(Chunk chunk, Material blockType, int limiter, boolean doPop) {
        int counter = 0;

        for (int y = 255; y >= 0; y--) {
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {

                    Block thisBlock = chunk.getBlock(x, y, z);
                    Location thisLoc = thisBlock.getLocation();

                    if (thisBlock.getType() == blockType) {
                        counter++;

                        if (doPop) thisLoc.getWorld().dropItem(thisLoc, new ItemStack(thisBlock.getType(),1));
                        thisBlock.setType(Material.AIR);
                    }
                    if (counter >= limiter) return counter;
                }
            }
        }
        return counter;
    }
}

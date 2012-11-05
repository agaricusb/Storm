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

import net.minecraft.server.Block;
import net.minecraft.server.Item;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Container class for all configurable Storm variables.
 */

public class GlobalVariables extends ReflectConfiguration {

    /**
     * Creates a GlobalVariable object with the given name.
     *
     * @param plugin Plugin: used to know which folder to save to
     * @param name   The name of the file
     */

    public GlobalVariables(Plugin plugin, String name) {
        super(plugin, name);
    }

    //Acid Rain
    @LimitInteger
    @Comment(_ = "The chance for acid rain to occur.")
    public int Acid__Rain_Acid__Rain__Chance = 5;
    @Comment(_ = "The base intervals between acid rain tries to start.")
    public int Acid__Rain_Acid__Rain__Base__Interval = 72000;
    @Comment(_ = "The message broadcast when acid rain starts.")
    public String Acid__Rain_Messages_On__Acid__Rain__Start = "Acid has started to fall from the sky!";
    @Comment(_ = "The message broadcast when acid rain ends.")
    public String Acid__Rain_Messages_On__Acid__Rain__Stop = "Acid rain ceases to fall!";
    @Comment(_ = "The message sent to a player when they are damaged by acid rain.")
    public String Acid__Rain_Messages_On__Player__Damaged__By__Acid__Rain = "You have been hurt by the acidic downfall!";
    @Comment(_ = "The damage a player is dealt when they are acid rain, measured in half hearts.")
    public int Acid__Rain_Player_Damage__From__Exposure = 1;
    @Comment(_ = "The damage an entity is dealt when it is in acid rain, measured in half hearts.")
    public int Acid__Rain_Entity_Damage__From__Exposure = 1;
    @Comment(_ = {"The list of block transformations that occur during acid rain. Key is the FROM block, value is TO block.", "18 -> " +
            "0 means leaves (ID of 18) will get turned into air (ID of 0)"})
    public List<List<String>> Acid__Rain_Dissolver_Block__Transformations = new ArrayList<List<String>>() {
        {
            add(Arrays.asList("18", "0"));
            add(Arrays.asList("102", "0"));
            add(Arrays.asList("111", "0"));
            add(Arrays.asList("12", "0"));
            add(Arrays.asList("20", "0"));
            add(Arrays.asList("2", "3"));
            add(Arrays.asList("1", "4"));
            add(Arrays.asList("4", "48"));
            add(Arrays.asList("6", "31"));
            add(Arrays.asList("31", "31"));
            add(Arrays.asList("37", "31"));
            add(Arrays.asList("38", "31"));
            add(Arrays.asList("39", "31"));
            add(Arrays.asList("40", "31"));
            add(Arrays.asList("59", "31"));
            add(Arrays.asList("60", "3"));

        }
    };
    @Comment(_ = "A list of blocks that entities will not take damage when within specified radius.")
    public List<Integer> Acid__Rain_Absorbing__Blocks = new ArrayList<Integer>() {
        {
            add(Block.GOLD_BLOCK.id);
        }
    };
    @Comment(_ = "The radius the absorbing blocks protect.")
    public int Acid__Rain_Absorbing__Radius = 2;
    @LimitInteger
    @Comment(_ = "The chance for a block to be deteriorated.")
    public int Acid__Rain_Dissolver_Block__Deterioration__Chance = 60;
    @Comment(_ = "The delay between sets of block deteriorations, in ticks.")
    public int Acid__Rain_Scheduler_Dissolver__Calculation__Intervals__In__Ticks = 10;
    @Comment(_ = "The delay between damaging entities, in ticks.")
    public int Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks = 40;
    //Thunder Storms
    @LimitInteger
    @Newline
    @Comment(_ = "The chance for thunder storm to occur.")
    public int Thunder__Storm_Thunder__Storm__Chance = 4;
    @Comment(_ = "The base intervals between thunder storm tries to start.")
    public int Thunder__Storm_Thunder__Storm__Base__Interval = 72000;
    @Comment(_ = "The message to send when thunder storm is started.")
    public String Thunder__Storm_Messages_On__Thunder__Storm__Start = "An electrical storm has started! Get inside for safety!";
    @Comment(_ = "The message to send when thunder storm is stopped.")
    public String Thunder__Storm_Messages_On__Thunder__Storm__Stop = "Zeus has stopped bowling!";
    @LimitInteger
    @Comment(_ = "The chance of being stroke by thunder storm.")
    public int Thunder__Storm_Strike__Chance = 5;
    @Comment(_ = "The delay between strikes, in ticks.")
    public int Thunder__Storm_Scheduler_Striker__Calculation__Intervals__In__Ticks = 10;
    //Blizzards
    @LimitInteger
    @Newline
    @Comment(_ = "The chance for blizzard to occur.")
    public int Blizzard_Blizzard__Chance = 20;
    @Comment(_ = "The base intervals between blizzard tries to start.")
    public int Blizzard_Blizzard__Base__Interval = 72000;
    @Comment(_ = "The message to send when blizzard is started.")
    public String Blizzard_Messages_On__Blizzard__Start = "It has started to snow violently! Seek a warm biome for safety!";
    @Comment(_ = "The message to send when blizzard is stopped.")
    public String Blizzard_Messages_On__Blizzard__Stop = "The blizzard has stopped!";
    @Comment(_ = "The message to send when a player is damaged by cold.")
    public String Blizzard_Messages_On__Player__Damaged__Cold = "You are freezing!";
    @Comment(_ = "The list of blocks that can prevent damage by blizzard when the player is close.")
    public List<Integer> Blizzard_Heating__Blocks = Arrays.asList(
            Block.FIRE.id, Block.LAVA.id, Block.STATIONARY_LAVA.id,
            Block.BURNING_FURNACE.id);
    @Comment(_ = "The effective radius of blocks that can prevent damage by blizzard.")
    public int Blizzard_Heat__Radius = 2;
    @Comment(_ = "The amount of damage experienced by players in a blizzard, in hearts.")
    public int Blizzard_Player_Damage__From__Exposure = 2;
    @Comment(_ = "The amount of damage experienced by entities in a blizzard, in hearts.")
    public int Blizzard_Entity_Damage__From__Exposure = 2;
    @Comment(_ = "The speed loss experienced by players in a blizzard.")
    public double Blizzard_Player_Speed__Loss__While__In__Snow = 0.4D;
    @Comment(_ = "The delay between damages by blizzard, in ticks.")
    public int Blizzard_Scheduler_Damager__Calculation__Intervals__In__Ticks = 40;
    //Better Lightning
    @Newline
    @Comment(_ = "The amount of damage caused by lightning in hearts.")
    public int Lightning_Damage_Damage = 5;
    @Comment(_ = "The radius when players and entities can be damaged by a lightning strike.")
    public int Lightning_Damage_Damage__Radius = 10;
    @Comment(_ = "The message to send when zapped by lightning.")
    public String Lightning_Messages_On__Player__Hit = "You were zapped by lightning. Ouch!";
    @LimitInteger
    @Comment(_ = "The chance of lightning being attracted to a block specified below.")
    public int Lightning_Attraction_Blocks_AttractionChance = 80;
    @Comment(_ = "The blocks that can attract lightning.")
    public List<Integer> Lightning_Attraction_Blocks_Attractors = Arrays
            .asList(Block.IRON_BLOCK.id, Block.DIAMOND_BLOCK.id, Block.GOLD_BLOCK.id,
                    Block.RAILS.id, Block.CAULDRON.id, Block.DETECTOR_RAIL.id, Block.GOLDEN_RAIL.id,
                    Block.IRON_DOOR_BLOCK.id, Block.IRON_FENCE.id);
    @LimitInteger
    @Comment(_ = "The chance of lightning being attracted to a player holding the items specified below.")
    public int Lightning_Attraction_Players_AttractionChance = 80;
    @Comment(_ = "The items that can attract lightning when held.")
    public List<Integer> Lightning_Attraction_Players_Attractors = Arrays
            .asList(Item.IRON_AXE.id, Item.BUCKET.id, Item.CHAINMAIL_BOOTS.id, Item.CHAINMAIL_CHESTPLATE.id, Item.CHAINMAIL_HELMET.id,
                    Item.CHAINMAIL_LEGGINGS.id, Item.IRON_BOOTS.id, Item.IRON_CHESTPLATE.id, Item.IRON_HELMET.id, Item.IRON_LEGGINGS.id,
                    Item.DIAMOND_BOOTS.id, Item.DIAMOND_CHESTPLATE.id, Item.DIAMOND_HELMET.id, Item.DIAMOND_LEGGINGS.id,
                    Item.GOLD_BOOTS.id, Item.GOLD_CHESTPLATE.id, Item.GOLD_HELMET.id, Item.GOLD_LEGGINGS.id,
                    Item.IRON_AXE.id, Item.IRON_HOE.id, Item.IRON_PICKAXE.id, Item.IRON_SPADE.id, Item.IRON_SWORD.id,
                    Item.DIAMOND_AXE.id, Item.DIAMOND_HOE.id, Item.DIAMOND_PICKAXE.id, Item.DIAMOND_SPADE.id, Item.DIAMOND_SWORD.id,
                    Item.GOLD_AXE.id, Item.GOLD_HOE.id, Item.GOLD_PICKAXE.id, Item.GOLD_SPADE.id, Item.GOLD_SWORD.id,
                    Item.MINECART.id);
    @Comment(_ = "The blocks to be transformed when melted by lightning.")
    public List<List<String>> Lightning_Melter_Block__Transformations = new ArrayList<List<String>>() {
        {
            add(Arrays.asList("12", "20"));

        }
    };
    //Natural Disasters
    //-Meteors
    @LimitInteger
    @Newline(num = 2)
    @Comment(_ = "The chance that meteor will occur.")
    public int Natural__Disasters_Meteor_Chance__To__Spawn = 8;
    @Comment(_ = "The base intervals between meteor tries to start.")
    public int Natural__Disasters_Meteor_Meteor__Base__Interval = 72000;
    @Comment(_ = "The message sent when a meteor hits ground. %x, %y, and %z is replaced with coordinates.")
    public String Natural__Disasters_Meteor_Messages_On__Meteor__Crash = "A meteor has exploded at %x, %y, %z.";
    @Comment(_ = "The message sent when a player is damaged by meteor.")
    public String Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave = "You have been flattened by a meteor!";
    @Comment(_ = "The number of hearts worth of damage that will be dealt when flattened by a meteor")
    public int Natural__Disasters_Meteor_Shockwave_Damage = 10;
    @Comment(_ = "The radius where players and entities alike will be damaged.")
    public int Natural__Disasters_Meteor_Shockwave_Damage__Radius = 100;
    public boolean Natural__Disasters_Meteor_Do__Winter = true;
    @Comment(_ = "Will solid meteor spawn?")
    public boolean Natural__Disasters_Meteor_Meteor__Spawn = true;
    @Comment(_ = "The ores to deposit and the chances of the ores being deposited.")
    public List<List<String>> Natural__Disasters_Meteor_Ore__Chance__Percentages = new ArrayList<List<String>>() {
        {
            //block ID, chance
            add(Arrays.asList("5", "100"));
        }
    };
    @LimitInteger
    @Newline
    public int Natural__Disasters_Wildfires_Chance__To__Start = 20;
    public int Natural__Disasters_Wildfires_Wildfire__Base__Interval = 72000;
    public int Natural__Disasters_Wildfires_Spread__Limit = 2;
    public int Natural__Disasters_Wildfires_Scan__Radius = 2;
    public String Natural__Disasters_Wildfires_Messages_On__Start = "A wildfire has been spotted around %x, %y, %z!";
    public int Natural__Disasters_Wildfires_Maximum__Fires = 100;
    //   public int Natural__Disasters_Earthquakes_Chance__To__Spawn = 1;
//   public String Natural__Disasters_Earthquakes_Message__On__Earthquake__Start = "The ground beneath you begins quaking! Run mortal, run!";
//   public List<Integer> Natural__Disasters_Earthquakes_Blocks__Can__Fall = Arrays.asList(Block.STONE.id, Block.COBBLESTONE.id);
//   public long Natural__Disasters_Earthquake_Scheduler_Recalculation__Intervals__In__Ticks = 72000;
    // Volcanoes
    //public List<Integer> Volcano_Composition = new ArrayList<Integer>() {{
       // for (int i = 0; i < 100; ++i)
         //   add(Material.STONE.getId());
        // for (int i = 0; i < 5; ++i)
        //    add(Material.COAL_ORE.getId());
        //for (int i = 0; i < 3; ++i)
        //    add(Material.IRON_ORE.getId());
        // for (int i = 0; i < 2; ++i)
        //     add(Material.GOLD_ORE.getId());
        //  add(Material.DIAMOND_ORE.getId());
        //  if (Storm.version > 1.2)
        //      add(Material.EMERALD_ORE.getId());
    //}};
    // Texture Packs
    public boolean Alpha__Features_Volcanoes_Enabled = true;
    @Newline(num = 2)
    public String Textures_Acid__Rain__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Acid_Rain.zip";
    public String Textures_Blizzard__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Blizzard.zip";
    public String Textures_Default__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Default.zip";
    // Features
    @Newline(num = 2)
    public boolean Features_Acid__Rain_Dissolving__Blocks = true;
    public boolean Features_Acid__Rain_Player__Damaging = true;
    public boolean Features_Acid__Rain_Entity__Damaging = true;
    public boolean Features_Acid__Rain_Entity__Shelter__Pathfinding = true;
    public boolean Features_Thunder__Storms_Thunder__Striking = true;
    public boolean Features_Thunder__Storms_Entity__Shelter__Pathfinding = true;
    public boolean Features_Lightning_Greater__Range__And__Damage = true;
    public boolean Features_Lightning_Player__Attraction = true;
    public boolean Features_Lightning_Block__Attraction = true;
    public boolean Features_Lightning_Block__Transformations = true;
    public boolean Features_Blizzards_Player__Damaging = true;
    public boolean Features_Blizzards_Entity__Damaging = true;
    public boolean Features_Blizzards_Entity__Shelter__Pathfinding = true;
    public boolean Features_Blizzards_Slowing__Snow = true;
    public boolean Features_Meteor = true;
    public boolean Features_Wildfires = true;
    public boolean Features_Force__Weather__Textures = true;
}

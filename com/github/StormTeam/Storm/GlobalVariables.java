package com.github.StormTeam.Storm;

import net.minecraft.server.Block;
import net.minecraft.server.Item;
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
    public int Acid__Rain_Acid__Rain__Chance = 5;
    public int Acid__Rain_Acid__Rain__Base__Interval = 72000;
    public String Acid__Rain_Messages_On__Acid__Rain__Start = "Acid has started to fall from the sky!";
    public String Acid__Rain_Messages_On__Acid__Rain__Stop = "Acid rain ceases to fall!";
    public String Acid__Rain_Messages_On__Player__Damaged__By__Acid__Rain = "You have been hurt by the acidic downfall!";
    public int Acid__Rain_Player_Damage__From__Exposure = 1;
    public List<List<String>> Acid__Rain_Dissolver_Block__Transformations = new ArrayList<List<String>>() {
        {
            add(Arrays.asList("18", "0"));
            add(Arrays.asList("2", "3"));
            add(Arrays.asList("1", "4"));
            add(Arrays.asList("4", "48"));

        }
    };
    public List<Integer> Acid__Rain_Player_Absorbing__Blocks = new ArrayList<Integer>() {
        {
            add(Block.GOLD_BLOCK.id);
        }
    };
    public int Acid__Rain_Player_Absorbing__Radius = 2;
    public int Acid__Rain_Dissolver_Block__Deterioration__Chance = 60;
    public int Acid__Rain_Scheduler_Dissolver__Calculation__Intervals__In__Ticks = 10;
    public int Acid__Rain_Scheduler_Damager__Calculation__Intervals__In__Ticks = 40;
    //Thunder Storms
    public int Thunder__Storm_Thunder__Storm__Chance = 4;
    public int Thunder__Storm_Thunder__Storm__Base__Interval = 72000;
    public String Thunder__Storm_Messages_On__Thunder__Storm__Start = "An electrical storm has started! Get inside for safety!";
    public String Thunder__Storm_Messages_On__Thunder__Storm__Stop = "Zeus has stopped bowling!";
    public int Thunder__Storm_Strike__Chance = 5;
    public int Thunder__Storm_Scheduler_Striker__Calculation__Intervals__In__Ticks = 10;
    //Blizzards
    public int Blizzard_Blizzard__Chance = 20;
    public int Blizzard_Blizzard__Base__Interval = 72000;
    public String Blizzard_Messages_On__Blizzard__Start = "It has started to snow violently! Seek a warm biome for safety!";
    public String Blizzard_Messages_On__Blizzard__Stop = "The blizzard has stopped!";
    public String Blizzard_Messages_On__Player__Damaged__Cold = "You are freezing!";
    public int Blizzard_Player_Blindness__Amplitude = 5;
    public List<Integer> Blizzard_Player_Heating__Blocks = Arrays.asList(
            Block.FIRE.id, Block.LAVA.id, Block.STATIONARY_LAVA.id,
            Block.BURNING_FURNACE.id);
    public int Blizzard_Player_Heat__Radius = 2;
    public int Blizzard_Player_Damage__From__Exposure = 2;
    public double Blizzard_Player_Speed__Loss__While__In__Snow = 0.4D;
    public int Blizzard_Scheduler_Player__Damager__Calculation__Intervals__In__Ticks = 200;
    //Better Lightning
    public int Lightning_Damage_Damage = 5;
    public int Lightning_Damage_Damage__Radius = 10;
    public String Lightning_Messages_On__Player__Hit = "You were zapped by lightning. Ouch!";
    public int Lightning_Attraction_Blocks_AttractionChance = 80;
    public List<Integer> Lightning_Attraction_Blocks_Attractors = Arrays
            .asList(Block.IRON_BLOCK.id, Block.DIAMOND_BLOCK.id, Block.GOLD_BLOCK.id,
                    Block.RAILS.id, Block.CAULDRON.id, Block.DETECTOR_RAIL.id, Block.GOLDEN_RAIL.id,
                    Block.IRON_DOOR_BLOCK.id, Block.IRON_FENCE.id);
    public int Lightning_Attraction_Players_AttractionChance = 80;
    public List<Integer> Lightning_Attraction_Players_Attractors = Arrays
            .asList(Item.IRON_AXE.id, Item.BUCKET.id, Item.CHAINMAIL_BOOTS.id, Item.CHAINMAIL_CHESTPLATE.id, Item.CHAINMAIL_HELMET.id,
                    Item.CHAINMAIL_LEGGINGS.id, Item.IRON_BOOTS.id, Item.IRON_CHESTPLATE.id, Item.IRON_HELMET.id, Item.IRON_LEGGINGS.id,
                    Item.DIAMOND_BOOTS.id, Item.DIAMOND_CHESTPLATE.id, Item.DIAMOND_HELMET.id, Item.DIAMOND_LEGGINGS.id,
                    Item.GOLD_BOOTS.id, Item.GOLD_CHESTPLATE.id, Item.GOLD_HELMET.id, Item.GOLD_LEGGINGS.id,
                    Item.IRON_AXE.id, Item.IRON_HOE.id, Item.IRON_PICKAXE.id, Item.IRON_SPADE.id, Item.IRON_SWORD.id,
                    Item.DIAMOND_AXE.id, Item.DIAMOND_HOE.id, Item.DIAMOND_PICKAXE.id, Item.DIAMOND_SPADE.id, Item.DIAMOND_SWORD.id,
                    Item.GOLD_AXE.id, Item.GOLD_HOE.id, Item.GOLD_PICKAXE.id, Item.GOLD_SPADE.id, Item.GOLD_SWORD.id,
                    Item.MINECART.id);
    public List<List<String>> Lightning_Melter_Block__Transformations = new ArrayList<List<String>>() {
        {
            add(Arrays.asList("12", "20"));

        }
    };
    //Natural Disasters
    //-Meteors
    public int Natural__Disasters_Meteor_Chance__To__Spawn = 8;
    public int Natural__Disasters_Meteor_Meteor__Base__Interval = 72000;
    public String Natural__Disasters_Meteor_Messages_On__Meteor__Crash = "A meteor has exploded at %x, %y, %z.";
    public String Natural__Disasters_Meteor_Messages_On__Damaged__By__Shockwave = "You have been flattened by a meteor!";
    public int Natural__Disasters_Meteor_Shockwave_Damage = 10;
    public int Natural__Disasters_Meteor_Shockwave_Damage__Radius = 100;
    public boolean Natural__Disasters_Meteor_Meteor_Spawn = true;
    //-Wildfires //TODO update to NAPI!
    public int Natural__Disasters_Wildfires_Chance__To__Start = 20;
    public int Natural__Disasters_Wildfires_Wildfire__Base__Interval = 72000;
    public int Natural__Disasters_Wildfires_Spread__Limit = 2;
    public int Natural__Disasters_Wildfires_Scan__Radius = 2;
    public String Natural__Disasters_Wildfires_Messages_On__Start = "A wildfire has been spotted around %x, %y, %z!";
    public int Natural__Disasters_Maximum__Fires = 100;
    //   public int Natural__Disasters_Earthquakes_Chance__To__Spawn = 1;
//   public String Natural__Disasters_Earthquakes_Message__On__Earthquake__Start = "The ground beneath you begins quaking! Run mortal, run!";
//   public List<Integer> Natural__Disasters_Earthquakes_Blocks__Can__Fall = Arrays.asList(Block.STONE.id, Block.COBBLESTONE.id);
//   public long Natural__Disasters_Earthquake_Scheduler_Recalculation__Intervals__In__Ticks = 72000;
    // Texture Packs
    public String Textures_Acid__Rain__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Acid_Rain.zip";
    public String Textures_Blizzard__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Blizzard.zip";
    public String Textures_Default__Texture__Path = "http://dl.dropbox.com/u/67341745/Storm/Default.zip";
    // Features
    public boolean Features_Acid__Rain_Dissolving__Blocks = true;
    public boolean Features_Acid__Rain_Player__Damaging = true;
    public boolean Features_Thunder__Storms = true;
    public boolean Features_Lightning_Greater__Range__And__Damage = true;
    public boolean Features_Lightning_Player__Attraction = true;
    public boolean Features_Lightning_Block__Attraction = true;
    public boolean Features_Lightning_Block__Transformations = true;
    public boolean Features_Snow_Slow__Players__Down = true;
    public boolean Features_Blizzards_Player__Damaging = true;
    public boolean Features_Blizzards_Slowing__Snow = true;
    public boolean Features_Blizzards_Piling__Snow = true;
    public boolean Features_Meteor = true;
    public boolean Features_Wildfires = true;
    public boolean Features_Force__Weather__Textures = true;
}

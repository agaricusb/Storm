package com.github.StormTeam.Storm.Volcano;

import com.github.StormTeam.Storm.BlockShifter;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LavaSolidifier {

    private int tps = 20;

    public LavaSolidifier(Block lava, int idTo) {
        int id = lava.getTypeId();
        if (id != 10 && id != 11)
            throw new UnsupportedOperationException("Can only solidify lava! Passed block ID: " + id + "(" + Material.getMaterial(id) + ").");

        //0x0 is a full block. Water goes up to 0x7, Lava goes up to 0x6
        // (using the steps 0x0, 0x2, 0x4 and 0x6). If bit 0x8 is set, this liquid is "falling" and only spreads downward.
        //http://redditpublic.com/wiki/Data_values#Water_and_Lava

        byte data = lava.getData();
        int multiplier = 0;

        switch (data) {
            case 0x00:
                multiplier = 4;
            case 0x02:
                multiplier = 3;
            case 0x04:
                multiplier = 2;
            case 0x06:
            case 0x08:
                multiplier = 1;
            default:
                multiplier = 2;
        }

        int solidTime = multiplier * tps * 2;
        BlockShifter.syncSetBlockDelayed(lava, idTo, solidTime);
    }
}

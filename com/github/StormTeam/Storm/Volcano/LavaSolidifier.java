package com.github.StormTeam.Storm.Volcano;

import com.github.StormTeam.Storm.BlockShifter;
import org.bukkit.block.Block;

public class LavaSolidifier {

    private int tps = 20;

    public LavaSolidifier(Block lava, int idTo) {
        //0x0 is a full block. Water goes up to 0x7, Lava goes up to 0x6
        //(using the steps 0x0, 0x2, 0x4 and 0x6). If bit 0x8 is set, this liquid is "falling" and only spreads downward.
        //http://redditpublic.com/wiki/Data_values#Water_and_Lava

        int data;

        if ((data = lava.getData()) == 0x9)
            return;
        BlockShifter.syncSetBlockDelayed(lava, idTo, ((data & 0x8) == 0x8 ? 1 : 4 - data / 2) * tps * 2);
    }
}
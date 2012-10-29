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

        int data = lava.getData();

        //m = 4-(b - (b/2))

        int solidTime = (data == 8 ? 1 : 4 - data - data / 2) * tps * 2;
        System.out.println("BDATA: " + data + ". DELAY: " + solidTime + ".");
        BlockShifter.syncSetBlockDelayed(lava, idTo, solidTime);
    }
}

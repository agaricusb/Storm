package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Blocks.SnowLayer;
import com.github.StormTeam.Storm.Storm;
import net.minecraft.server.Block;
import net.minecraft.server.StepSound;

import java.lang.reflect.Method;

/**
 * An object for easily modding MC snow to SnowLayer.
 */

public class SnowModder {

    private Class<?> bc = Block.class;
    private Method v, p, c, a, h, a_st;

    void mod13X() throws Exception {
        v = Block.class.getDeclaredMethod("v");
        p = bc.getDeclaredMethod("p");
        c = bc.getDeclaredMethod("c", float.class);
        a = bc.getDeclaredMethod("a", StepSound.class);
        h = bc.getDeclaredMethod("h", int.class);

        v.setAccessible(true);
        p.setAccessible(true);
        c.setAccessible(true);
        a.setAccessible(true);
        h.setAccessible(true);

        Block.byId[Block.SNOW.id] = null;
        Block.byId[Block.SNOW.id] = (Block) h.invoke(a.invoke(c.invoke(p.invoke(v.invoke((new SnowLayer()).b("snow"))), 0.1F), Block.k), 0); //Ugly, but relatively necessary to be compact
    }

    void mod12X() throws Exception {
        c = bc.getDeclaredMethod("c", float.class);
        a = bc.getDeclaredMethod("a", StepSound.class);
        h = bc.getDeclaredMethod("f", int.class);
        a_st = bc.getDeclaredMethod("a", String.class);

        a_st.setAccessible(true);
        c.setAccessible(true);
        a.setAccessible(true);
        h.setAccessible(true);

        Block.byId[Block.SNOW.id] = null;
        Block.byId[Block.SNOW.id] = (Block) h.invoke(a.invoke(c.invoke(a_st.invoke(new SnowLayer(), "snow"), 0.1F), Block.k), 0);
    }

    /**
     * Removes the modded aspect of snow layer.
     */

    public void reset() {
        Block.byId[Block.SNOW.id] = null;
        Block.byId[Block.SNOW.id] = Block.SNOW; //Resets to default snow
    }

    /**
     * Mods the snow based on current MC version. 1.2.X & 1.3.X compatible.
     */

    public void modBestFit() {
        try {
            if (Storm.version == 1.3) {
                mod13X();
            }
            if (Storm.version == 1.2) {
                mod12X();
            }
        } catch (Exception e) {
            //1. Doesn't matter if I catch ComputerIsOnFireException, and  
            //2. Even though it failed, blizzards will still run.
            e.printStackTrace(); //Let them know regardless.
        }
    }
}

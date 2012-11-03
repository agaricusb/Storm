package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Blocks.SnowLayer;
import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Storm;
import net.minecraft.server.Block;
import net.minecraft.server.StepSound;

import java.lang.reflect.Method;

/**
 * An object for easily modding MC snow to SnowLayer.
 */

public class SnowModder {

    private Class<?> bc = Block.class;
    private Method c;
    private Method a;
    private Method h;

    void mod14X() throws Exception {
        c = bc.getDeclaredMethod("c", float.class);
        a = bc.getDeclaredMethod("a", StepSound.class);
        Method b = bc.getDeclaredMethod("b", String.class);
        Method r = bc.getDeclaredMethod("r");
        h = bc.getDeclaredMethod("h", int.class);

        Block.byId[Block.SNOW.id] = null;
        //(new SnowLayer()).c(0.1F).a(m).b("snow").r().h(0);
        Block.byId[Block.SNOW.id] = (Block) h.invoke(r.invoke(b.invoke(a.invoke(c.invoke(new SnowLayer(), 0.1F), Block.k), "snow")), 0);
    }

    void mod13X() throws Exception {
        Method v = Block.class.getDeclaredMethod("v");
        Method p = bc.getDeclaredMethod("p");
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
        Method a_st = bc.getDeclaredMethod("a", String.class);

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
            switch ((int) (Storm.version * 100)) {
                case 140:
                    mod14X();
                    break;
                case 130:
                    mod13X();
                    break;
                case 120:
                    mod12X();
                    break;
                default:
                    throw new UnsupportedOperationException("Minecraft version " + Storm.version + " not supported");
            }
        } catch (Exception e) {
            //1. Doesn't matter if I catch ComputerIsOnFireException, and  
            //2. Even though it failed, blizzards will still run.
            ErrorLogger.generateErrorLog(e); //Let them know regardless.
        }
    }
}

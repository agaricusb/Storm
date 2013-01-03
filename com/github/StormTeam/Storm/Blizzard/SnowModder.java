package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Blocks.SnowLayer;
import net.minecraft.server.v1_4_6.Block;
import net.minecraft.server.v1_4_6.StepSound;

import java.lang.reflect.Method;

/**
 * An object for easily modding MC snow to SnowLayer.
 */

public class SnowModder {

    private final Class<?> bc = Block.class;
    private Method c;
    private Method a;
    private Method h;

    public void mod() throws Exception {
        c = bc.getDeclaredMethod("c", float.class);
        a = bc.getDeclaredMethod("a", StepSound.class);
        Method b = bc.getDeclaredMethod("b", String.class);
        Method r = bc.getDeclaredMethod("r");
        h = bc.getDeclaredMethod("h", int.class);

        b.setAccessible(true);
        r.setAccessible(true);
        c.setAccessible(true);
        a.setAccessible(true);
        h.setAccessible(true);

        Block.byId[Block.SNOW.id] = null;
        //(new SnowLayer()).c(0.1F).a(m).b("snow").r().h(0);
        Block.byId[Block.SNOW.id] = (Block) h.invoke(r.invoke(b.invoke(a.invoke(c.invoke(new SnowLayer(), 0.1F), Block.k), "snow")), 0);
    }

    /**
     * Removes the modded aspect of snow layer.
     */

    public void reset() {
        Block.byId[Block.SNOW.id] = null;
        Block.byId[Block.SNOW.id] = Block.SNOW; //Resets to default snow
    }
}

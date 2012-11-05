package com.github.StormTeam.Storm;

import org.bukkit.block.Block;

public class BlockTransformer {

    private final IDBlock from;
    private final IDBlock to;

    public BlockTransformer(IDBlock from, IDBlock to) {
        this.from = from;
        this.to = to;
    }

    public boolean transform(Block block) {
        if (from.isBlock(block)) {
            to.setBlock(block);
            return true;
        }
        return false;
    }
}



package com.github.StormTeam.Storm;

import org.bukkit.block.Block;

public class IDBlock {
    private int id, data = -1;

    public IDBlock(int id, int data) {
        this.id = id;
        this.data = data;
    }

    public IDBlock(String data) {
        if (data.contains(":")) {
            String[] split = data.split(":");
            this.id = Integer.parseInt(split[0]);
            this.data = Integer.parseInt(split[1]);
        } else {
            this.id = Integer.parseInt(data);
        }
    }

    ;

    public IDBlock(Block b) {
        this.id = b.getTypeId();
        this.data = b.getData();
    }

    public void setBlock(Block b) {
        b.setTypeId(id);
        if (data != -1)
            b.setData((byte) data);
    }

    public boolean isBlock(Block b) {
        return b.getTypeId() == id && data != -1 && b.getData() == data;
    }

}

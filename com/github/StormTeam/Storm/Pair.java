package com.github.StormTeam.Storm;

public class Pair<Left, Right> {

    public Left LEFT;
    public Right RIGHT;

    /**
     * Creates a Pair object.
     *
     * @param left  The value of the right part
     * @param right The value of the left part
     */
    public Pair(Left left, Right right) {
        this.LEFT = left;
        this.RIGHT = right;
    }

}
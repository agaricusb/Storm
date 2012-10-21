package com.github.StormTeam.Storm;

/**
 * An object for the Triplet data type.
 *
 * @param <X> The X value of the Triplet
 * @param <Y> The Y value of the Triplet
 * @param <Z> The Z value of the Triplet
 */
public class Triplet<X, Y, Z> {
    public X x;
    public Y y;
    public Z z;

    /**
     * Constructs a Triplet object based on given values
     *
     * @param x The X value of the Triplet
     * @param y The Y value of the Triplet
     * @param z The Z value of the Triplet
     */

    public Triplet(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

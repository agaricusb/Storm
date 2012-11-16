/*
 * This file is part of Storm.
 *
 * Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Storm.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.github.StormTeam.Storm.Math;

/**
 * A random number generator based on the simple and fast xor-shift pseudo
 * random number generator (RNG) specified in:
 * Marsaglia, George. (2003). Xorshift RNGs.
 * http://www.jstatsoft.org/v08/i14/xorshift.pdf
 * Translated from:
 * http://www.codeproject.com/Articles/9187/A-fast-equivalent-for-System-Random.
 */
@SuppressWarnings("SuspiciousNameCombination")
public class Random extends java.util.Random {
    final double REAL_UNIT_INT = 1.0 / (0x7FFFFFFFL);
    final double REAL_UNIT_UINT = 1.0 / (0xFFFFFFFFL);
    final long Y = 842502087L, Z = 3579807591L, W = 273326509L;
    long x, y, z, w;

    public Random() {
        seed((int) System.currentTimeMillis());
    }

    @Override
    public void setSeed(long seed) {
        seed((int) seed);
    }

    public void seed(int seed) {
        // The only stipulation stated for the xorshift RNG is that at least one of
        // the seeds x,y,z,w is non-zero. We fulfill that requirement by only allowing
        // resetting of the x seed
        x = seed;
        y = Y;
        z = Z;
        w = W;
    }

    long boolBuffer;
    int boolBufferBits = 0;

    @Override
    public boolean nextBoolean() {
        if (boolBufferBits == 0) {
            boolBuffer = nextUInt();
            boolBufferBits = 32;
        }
        boolBuffer >>= 1;
        boolean bit = (boolBuffer & 1) == 0;
        --boolBufferBits;
        return bit;
    }

    @Override
    public void nextBytes(byte[] buffer) {
        // Fill up the bulk of the buffer in chunks of 4 bytes at a time.
        long x = this.x, y = this.y, z = this.z, w = this.w;
        int i = 0;
        long t;
        for (int bound = buffer.length - 3; i < bound; ) {
            // Generate 4 bytes.
            // Increased performance is achieved by generating 4 random bytes per loop.
            // Also note that no mask needs to be applied to zero out the higher order bytes before
            // casting because the cast ignores thos bytes. Thanks to Stefan Trosch黷z for pointing this out.
            t = (x ^ (x << 11));
            x = y;
            y = z;
            z = w;
            w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));

            buffer[i++] = (byte) w;
            buffer[i++] = (byte) (w >> 8);
            buffer[i++] = (byte) (w >> 16);
            buffer[i++] = (byte) (w >> 24);
        }

        // Fill up any remaining bytes in the buffer.
        if (i < buffer.length) {
            // Generate 4 bytes.
            t = (x ^ (x << 11));
            x = y;
            y = z;
            z = w;
            w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));

            buffer[i++] = (byte) w;
            if (i < buffer.length) {
                buffer[i++] = (byte) (w >> 8);
                if (i < buffer.length) {
                    buffer[i++] = (byte) (w >> 16);
                    if (i < buffer.length) {
                        buffer[i] = (byte) (w >> 24);
                    }
                }
            }
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public double nextDouble() {
        long t = (x ^ (x << 11));
        x = y;
        y = z;
        z = w;

        // Here we can gain a 2x speed improvement by generating a value that can be cast to
        // an int instead of the more easily available uint. If we then explicitly cast to an
        // int the compiler will then cast the int to a double to perform the multiplication,
        // this final cast is a lot faster than casting from a uint to a double. The extra cast
        // to an int is very fast (the allocated bits remain the same) and so the overall effect
        // of the extra cast is a significant performance improvement.
        //
        // Also note that the loss of one bit of precision is equivalent to what occurs within
        // System.Random.
        return (REAL_UNIT_INT * (int) (0x7FFFFFFF & (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8)))));
    }

    public double random() {
        return nextDouble();
    }

    @Override
    public float nextFloat() {
        return (float) nextDouble();
    }

    @Override
    public int nextInt() {
        long t = (x ^ (x << 11));
        x = y;
        y = z;
        z = w;
        return (int) (0x7FFFFFFF & (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8))));
    }

    @Override
    public int nextInt(int upperBound) {
        if (upperBound < 0)
            throw new IllegalArgumentException("upperBound must be >=0");

        long t = (x ^ (x << 11));
        x = y;
        y = z;
        z = w;

        return (int) ((REAL_UNIT_INT * (int) (0x7FFFFFFF & (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8))))) * upperBound);
    }

    public int nextInt(int lowerBound, int upperBound) {
        if (lowerBound > upperBound)
            throw new IllegalArgumentException("upperBound must be >=lowerBound");

        long t = (x ^ (x << 11));
        x = y;
        y = z;
        z = w;

        // The explicit int cast before the first multiplication gives better performance.
        // See comments in NextDouble.
        int range = upperBound - lowerBound;
        if (range < 0) {
            // If range is <0 then an overflow has occured and must resort to using long integer arithmetic instead (slower).
            // We also must use all 32 bits of precision, instead of the normal 31, which again is slower.
            return lowerBound + (int) ((REAL_UNIT_UINT * (double) (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8)))) * (double) ((long) upperBound - (long) lowerBound));
        }
        // 31 bits of precision will suffice if range<=int.MaxValue. This allows us to cast to an int and gain
        // a little more performance.
        return lowerBound + (int) ((REAL_UNIT_INT * (double) (int) (0x7FFFFFFF & (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8))))) * (double) range);
    }

    public long nextUInt() {
        long t = (x ^ (x << 11));
        x = y;
        y = z;
        z = w;
        return (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8))) & (0xFFFFFFFFL);
    }

    @Override
    public long nextLong() {
        return nextUInt() << 32 + nextUInt();
    }

    double gaussNext;
    boolean hasGaussNext;
    final double TWOPI = Math.PI * 2;

    public double gauss() {
        // Java.util.random's implementation is better for this
        /*double current = gaussNext;
        hasGaussNext = false;
        if (!hasGaussNext) {
            double x2pi = nextDouble() * TWOPI;
            double g2rad = Math.sqrt(-2.0 * Math.log(1.0 - random()));
            current = Math.cos(x2pi) * g2rad;
            gaussNext = Math.sin(x2pi) * g2rad;
            hasGaussNext = true;
        }
        return current;*/
        return nextGaussian();
    }

    public double gauss(double mu, double sigma) {
        return mu + sigma * nextGaussian();
    }

    /**
     * Get a random number in the range [min, max) or [min, max] depending on rounding.
     *
     * @param min Low bound
     * @param max High bound
     * @return A uniformly distributed double
     */
    public double uniform(double min, double max) {
        return min + (max - min) * nextDouble();
    }

    /**
     * Triangular distribution.
     * <p/>
     * Continuous distribution bounded by given lower and upper limits,
     * and having a given mode value in-between.
     * http://en.wikipedia.org/wiki/Triangular_distribution
     *
     * @param low  Low bound
     * @param high High bound
     * @param mode Mode
     * @return
     */
    public double triangular(int low, int high, int mode) {
        double u = nextDouble();
        double c = (mode - low) / (high - low);
        if (u > c) {
            u = 1.0 - u;
            c = 1.0 - c;
            int k = low;
            low = high;
            high = k;
        }
        return low + (high - low) * Math.sqrt(u * c);
    }
}

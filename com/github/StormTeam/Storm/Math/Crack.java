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

import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Crack implements Iterable<Location>, Iterator<Location> {
    class Worker {
        int mean;
        int size;
        int i = 0;
        int x;
        int y;
        int z;
        int maxWidth;
        int maxDepth;

        public Worker(int size, int x, int y, int z, int maxWidth, int maxDepth) {
            this.size = size;
            this.x = x;
            this.y = y;
            this.z = z;
            this.maxWidth = maxWidth;
            this.maxDepth = maxDepth;
            this.mean = size / 2;
        }

        int intGauss(int mu, int sigma) {
            int temp = MathUtils.gauss(mu, sigma);
            return temp > 1 ? temp : 1;
        }

        public LinkedList<Vector> next() {
            if (i > size)
                return null;
            x += Storm.random.nextInt(3) - 1;
            int k = maxWidth + 2 - Math.abs(mean - i) / (mean / maxWidth + 1);
            LinkedList<Vector> out = new LinkedList<Vector>();
            int min = x - intGauss(k, 1), max = x + intGauss(k, 1);
            for (int j = min; j < max; ++j) {
                final int dy = maxDepth - Math.abs(j);
                out.add(new Vector(x + j, y - dy, z));
            }
            ++z;
            ++i;
            return out;
        }
    }

    Worker worker;
    LinkedList<Vector> cache = new LinkedList<Vector>();
    boolean done = false;
    final World world;

    public Crack(World world, int size, int x, int y, int z) {
        this(world, size, x, y, z, 20, 64);
    }

    public Crack(World world, int size, int x, int y, int z, int maxWidth, int maxDepth) {
        this.world = world;
        worker = new Worker(size, x, y, z, maxWidth, maxDepth);
    }

    @Override
    public Iterator<Location> iterator() {
        return this;
    }

    boolean getAnother() {
        if (done)
            return false;
        cache = worker.next();
        if (cache == null)
            done = true;
        return !done;
    }

    @Override
    public boolean hasNext() {
        if (done)
            return false;
        if (cache.size() > 0)
            return true;
        getAnother();
        return hasNext();
    }

    @Override
    public Location next() {
        if (cache.size() > 0)
            return cache.pop().toLocation(world);
        if (!hasNext())
            throw new NoSuchElementException();
        return next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

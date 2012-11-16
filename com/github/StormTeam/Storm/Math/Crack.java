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
import org.bukkit.util.Vector;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static com.github.StormTeam.Storm.Storm.random;

public class Cracker {
    Connection connection;
    PreparedStatement insert;
    PreparedStatement select;
    private final int size;
    private final int mean;
    private final int halfDepth;
    private int x;
    private int y;
    private int z;
    private final int maxWidth;
    private final int maxDepth;

    public Cracker(int size, int x, int y, int z, int maxWidth, int maxDepth) {
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxWidth = maxWidth;
        this.maxDepth = maxDepth;
        this.mean = size / 2;
        this.halfDepth = maxDepth / 2;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + File.createTempFile("StormCrack", ".db").getAbsolutePath());
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE points (id INT PRIMARY KEY AUTOINCREMENT, x INT, y INT, z INT, dx INT)");
            statement.executeUpdate("CREATE INDEX dz ON points (dx)");
            insert = connection.prepareStatement("INSERT INTO (x, y, z, dx) points VALUES (?, ?, ?, ?)");
            select = connection.prepareStatement("SELECT x, y, z FROM points WHERE dx = ?");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    int intGauss(int mu, int sigma) {
        int out = (int) Storm.random.gauss(mu, sigma);
        return out > 0 ? out : 1;
    }

    public void plot() {
        try {
            for (int i = 0; i < size; ++i) {
                x += Storm.random.nextInt(3) - 1;
                ++z;
                insert.setInt(2, z);
                int k = maxWidth + 2 - Math.abs(mean - i) / (mean / maxWidth);
                int min = -intGauss(k, 1), max = intGauss(k, 1);
                for (int dx = min; dx < max; ++dx) {
                    ///////////////////// Force the value to stay within half depth
                    int dy = Math.abs(dx) * halfDepth / (dx < 0 ? -min : max);
                    dy = maxDepth - (int) (dy * random.gauss(1, 0.25));
                    insert.setInt(0, x + dx);
                    insert.setInt(1, y - dy);
                    insert.setInt(3, Math.abs(dx));
                    insert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Vector> get(int dx) {
        try {
            select.setInt(0, dx);
            ResultSet result = select.executeQuery();
            List<Vector> out = new LinkedList<Vector>();
            while (result.next())
                out.add(new Vector(result.getInt("x"), result.getInt("y"), result.getInt("z")));
            return out;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
package com.github.StormTeam.Storm.Earthquake;

import com.github.StormTeam.Storm.Earthquake.Listeners.PlayerListener;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;

public class Earthquake {

    private static Storm storm;
    private static HashMap<Integer, Quake> quakes = new HashMap<Integer, Quake>();

    public static void load(Storm storm) {
        Earthquake.storm = storm;
        Storm.pm.registerEvents(new PlayerListener(), storm);
    }

    public static int loadQuake(Location one, Location two) {

        int id = quakes.size();
        Quake q = new Quake(storm, id, one, two);

        quakes.put(id, q);

        return id;

    }

    public static boolean isQuaked(Player p) {
        return isQuaked(p.getLocation());
    }

    public static boolean isQuaked(Location l) {
        Iterator<Quake> qI = quakes.values().iterator();
        while (qI.hasNext()) {
            Quake quake = qI.next();
            if (!quake.isLoading() && !quake.isRunning()) {
                qI.remove();
                continue;
            }

            if (quake.isQuaking(l))
                return true;
        }

        return false;
    }

    public static void stopQuake(int QuakeID) {
        if (quakes.containsKey(QuakeID)) {
            Quake q = quakes.remove(QuakeID);
            q.stop();
        }
    }
}

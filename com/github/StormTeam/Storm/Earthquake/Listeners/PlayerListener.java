package com.github.StormTeam.Storm.Earthquake.Listeners;

import com.github.StormTeam.Storm.Earthquake.Earthquake;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Location l = e.getPlayer().getLocation().clone();
        Location elkCloner = e.getPlayer().getLocation().clone();
        l.add(40, 0, 40);
        if (!Earthquake.isQuaked(l)) {
            elkCloner.subtract(40, 0, 40);
            if (!Earthquake.isQuaked(elkCloner)) {
                Earthquake.loadQuake(l, elkCloner);
            }
        }
    }
}

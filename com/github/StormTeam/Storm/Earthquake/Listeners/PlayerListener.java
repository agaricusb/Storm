package com.github.StormTeam.Storm.Earthquake.Listeners;

import com.github.StormTeam.Storm.Earthquake.Earthquake;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @SuppressWarnings("FieldCanBeLocal")
    private Storm storm;

    public PlayerListener(Storm storm) {
        this.storm = storm;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Location l = e.getPlayer().getLocation().clone();
        Location l2 = e.getPlayer().getLocation().clone();
        l.add(40, 0, 40);
        if (!Earthquake.isQuaked(l)) {
            l2.subtract(40, 0, 40);
            if (!Earthquake.isQuaked(l2)) {
                Earthquake.loadQuake(l, l2);
            }
        }
    }
}

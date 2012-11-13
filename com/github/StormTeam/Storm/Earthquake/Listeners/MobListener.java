package com.github.StormTeam.Storm.Earthquake.Listeners;

import com.github.StormTeam.Storm.Earthquake.Quake;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobListener implements Listener {

    private Quake quake;

    public MobListener(Quake quake) {
        this.quake = quake;
    }

    public void forget() {
        EntityTargetEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (e.getEntity().getType() != EntityType.CREEPER || !quake.isQuaking(e.getEntity().getLocation()))
            return;

        switch (e.getReason()) {
            case TARGET_ATTACKED_ENTITY:
            case FORGOT_TARGET:
            case TARGET_DIED:
                break;
            default:
                e.setCancelled(true);
        }
    }
}

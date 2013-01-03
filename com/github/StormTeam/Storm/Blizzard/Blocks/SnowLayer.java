package com.github.StormTeam.Storm.Blizzard.Blocks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import net.minecraft.server.v1_4_6.BlockSnow;
import net.minecraft.server.v1_4_6.Entity;
import net.minecraft.server.v1_4_6.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * A snow layer block that slows down players.
 */

public class SnowLayer extends BlockSnow {

    /**
     * Constructs the snow layer.
     */

    public SnowLayer() {
        super(78, 66);
    }

    /**
     * Called when an entity collides with this block: slow them down.
     *
     * @param w The net.minecraft.server.v1_4_6.v1_4_6.World this happened in
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param e The net.minecraft.server.v1_4_6.v1_4_6.Entity this collision occured with
     */

    @Override
    public void a(final World w, final int x, final int y, final int z, final Entity e) {

        String name = w.getWorld().getName();

        if (!Storm.manager.getActiveWeathers(name).contains("storm_blizzard") || (!Storm.wConfigs.containsKey(name) && !Storm.wConfigs.get(name).Blizzard_Features_Slowing__Snow)) {
            return;
        }

        org.bukkit.entity.Entity inSnow = e.getBukkitEntity();
        if ((inSnow instanceof Player && ((Player) (inSnow)).getGameMode() == GameMode.CREATIVE) || !StormUtil.isTundra(inSnow.getLocation().getBlock().getBiome())) {
            return;
        }

        inSnow.setVelocity(inSnow.getVelocity().multiply(Storm.wConfigs.get(name).Blizzard_Player_Speed__Loss__While__In__Snow));
    }
}

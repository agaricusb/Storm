package com.github.StormTeam.Storm.Blizzard.Blocks;

import com.github.StormTeam.Storm.Storm;
import net.minecraft.server.BlockSnow;
import net.minecraft.server.Entity;
import net.minecraft.server.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SnowLayer extends BlockSnow {

    public SnowLayer() {
        super(78, 66);
    }

    @Override
    public void a(final World w, final int x, final int y,
                  final int z, final Entity e) {

        String name = w.getWorld().getName();

        if (!Storm.manager.getActiveWeathers(name).contains("storm_blizzard")) {
            return;
        }

        if (!Storm.wConfigs.containsKey(name) && !Storm.wConfigs.get(name).Features_Blizzards_Slowing__Snow) {
            return;
        }

        final org.bukkit.entity.Entity inSnow = e.getBukkitEntity();
        if (inSnow instanceof Player && ((Player) (inSnow)).getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!Storm.util.isTundra(inSnow.getLocation().getBlock().getBiome())) {
            return;
        }

        inSnow.setVelocity(inSnow.getVelocity().clone()
                .multiply(Storm.wConfigs.get(name).Blizzard_Player_Speed__Loss__While__In__Snow));

    }
}

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

package com.github.StormTeam.Storm.Meteors.Entities;

import com.github.StormTeam.Storm.*;
import com.github.StormTeam.Storm.Meteors.Meteor;
import net.minecraft.server.v1_4_6.Block;
import net.minecraft.server.v1_4_6.EntityFireball;
import net.minecraft.server.v1_4_6.MovingObjectPosition;
import net.minecraft.server.v1_4_6.World;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A meteor entity.
 */

public class EntityMeteor extends EntityFireball {

    private float explosionRadius = 50F;
    private float trailPower = 20F;
    private float brightness = 10F;
    private String meteorCrashMessage;
    private int burrowCount = 5;
    private int burrowPower = 10;
    private boolean spawnMeteorOnImpact;
    private int radius = 5;
    private String damageMessage;
    private int shockwaveDamage;
    private int shockwaveDamageRadius;
    private boolean doSnow = false;
    private int snowRadius;
    private boolean h_lock, h_lock_2, h_lock_3;

    private final HashMap<IDBlock, Integer> ores = new HashMap<IDBlock, Integer>();
    private WorldVariables glob;

    /**
     * Constructs a meteor for the given world.
     *
     * @param world The net.minecraft.server.v1_4_6.World that this entity will be spawned in
     */

    public EntityMeteor(World world) {
        super(world);
        glob = Storm.wConfigs.get(world.getWorld().getName());
    }

    /**
     * Overload, constructs a meteor entity and modifies its properties.
     *
     * @param world                 The world to spawn in
     * @param burrowCount           How many times the meteor can burrow
     * @param burrowPower           With what explosion power to burrow
     * @param trailPower            The power of the explosive trail
     * @param explosionRadius       The power of the final, impact explosion
     * @param brightness            The brightness of the meteor
     * @param crashMessage          The message to broadcast on impact
     * @param shockwaveDamage       How much to damage players
     * @param shockwaveDamageRadius The radius to damage players within
     * @param damageMessage         The message to send to damaged players
     * @param snowRadius            The radius that will be plunged into meteoric winter
     * @param spawnOnImpact         Whether to spawn a meteor blob on impact: true = yes, false = no
     * @param radius                The radius of the blob
     * @param snow                  Whether or not to plunge area into meteoric winter
     */

    public EntityMeteor(World world, int burrowCount, int burrowPower,
                        float trailPower, float explosionRadius, float brightness,
                        String crashMessage, int shockwaveDamage,
                        int shockwaveDamageRadius, String damageMessage, int snowRadius,
                        boolean snow, boolean spawnOnImpact, int radius) {
        this(world);

        // Massive objects require massive initializations...
        this.burrowPower = burrowPower;
        this.burrowCount = burrowCount;
        this.trailPower = trailPower;
        this.explosionRadius = explosionRadius;
        this.brightness = brightness;
        this.meteorCrashMessage = crashMessage;
        this.shockwaveDamage = shockwaveDamage;
        this.shockwaveDamageRadius = shockwaveDamageRadius;
        this.damageMessage = damageMessage;
        this.snowRadius = snowRadius;
        this.damageMessage = damageMessage;
        this.doSnow = snow;
        this.spawnMeteorOnImpact = spawnOnImpact;
        this.radius = radius;
        this.glob = Storm.wConfigs.get(world.getWorld().getName());

        if (spawnMeteorOnImpact)
            for (List<String> ore : glob.Natural__Disasters_Meteor_Ore__Chance__Percentages)
                ores.put(new IDBlock(ore.get(0)), Integer.parseInt(ore.get(1)));
    }

    /**
     * Spawns the meteor.
     */
    public void spawn() {
        Meteor.meteors.add(this.getBukkitEntity().getEntityId());
        world.addEntity(this, SpawnReason.CUSTOM);
    }

    /**
     * Move method for 1.4.X.
     */
    @Override
    public void j_() {
        super.j_();
        do {
            h_lock = !h_lock;
            if (h_lock) {
                break;
            }
            h_lock_2 = !h_lock_2;
            if (h_lock_2) {
                break;
            }
            h_lock_3 = !h_lock_3;
            if (h_lock_3) {
                break;
            }

            int locY = (int) (this.locY);
            if ((locY & 0xFFFFFF00) != 0) { // !(0x00 < locY < 0xFF)
                this.dead = true; // Die silently
                return;
            }

            if ((locY & 0xFFFFFFE0) == 0) { // locy < 32
                try {
                    StormUtil.playSoundNearby(new Location(world.getWorld(), locX, locY, locZ), 100, glob.Natural__Disasters_Meteor_Sounds_Trail + Storm.random.nextInt(3) + 1, 10F, 1F);
                    explode();
                } catch (NullPointerException ignored) {
                    //Throws an NPE if explodes in unloaded chunk (locs are null). Can be ignored without consequence.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (!StormUtil.isBlockProtected(world.getWorld().getBlockAt((int) locX, locY, (int) locZ))) {
                StormUtil.playSoundNearby(new Location(world.getWorld(), locX, locY, locZ), 500, glob.Natural__Disasters_Meteor_Sounds_Trail, 10F, Storm.random.nextInt(3) + 1F);
                world.createExplosion(this, locX, this.locY, locZ, trailPower, true, true);
            }
        } while (false);
        motX *= 1.5F;
        motY *= 1.5F;
        motZ *= 1.5F;

    }

    /**
     * Called to burrow the meteor or explode
     *
     * @param movingobjectposition The position of the meteor
     */

    @Override
    public void a(MovingObjectPosition movingobjectposition) {
        Verbose.log("Burrowing meteor " + this);
        if (burrowCount > 0) {
            // Not yet dead, so burrow.
            if (!StormUtil.isBlockProtected(world.getWorld().getBlockAt((int) locX, (int) locY, (int) locZ))) {
                StormUtil.playSoundNearby(new Location(world.getWorld(), locX, locY, locZ), 500, glob.Natural__Disasters_Meteor_Sounds_Trail, 1F, Storm.random.nextInt(3) + 1F);
                world.createExplosion(this, locX, this.locY, locZ, trailPower, true, true);
            }
            --burrowCount;
            return;
        }
        if (!StormUtil.isBlockProtected(world.getWorld().getBlockAt((int) locX, (int) locY, (int) locZ))) {
            if (doSnow)
                makeWinter();
            try {
                StormUtil.playSoundNearby(new Location(world.getWorld(), locX, locY, locZ), 500, glob.Natural__Disasters_Meteor_Sounds_Trail, 1F, Storm.random.nextInt(3) + 1F);
                explode();
            } catch (NullPointerException ignored) {
                //Throws an NPE if explodes in unloaded chunk (locs are null). Can be ignored without consequence.
            }
        }
    }

    private void explode() {
        world.createExplosion(this, locX, this.locY, locZ, trailPower, true, true);

        Location origin = new Location(world.getWorld(), locX, locY, locZ);

        StormUtil.damageNearbyPlayers(new Location(this.world.getWorld(),
                locX, locY, locZ), shockwaveDamageRadius, shockwaveDamage,
                damageMessage, "storm.meteor.immune");

        StormUtil.broadcast(this.meteorCrashMessage.replace("%x", (int) locX + "")
                .replace("%z", (int) locZ + "")
                .replace("%y", (int) locY + ""), world.getWorld());

        StormUtil.playSoundNearby(new Location(world.getWorld(), locX, locY, locZ), 500, glob.Natural__Disasters_Meteor_Sounds_Trail, 1F, Storm.random.nextInt(3) + 1F);

        if (this.spawnMeteorOnImpact) {
            this.spawnMeteor(origin);
        }
        die();
    }

    private void addOres(ArrayList<IDBlock> result, IDBlock material, int percentage) {
        for (int i = 0; i < percentage; ++i) {
            if (result.size() >= 100)
                break;
            result.add(material);
        }
    }

    private void spawnMeteor(Location explosion) {
        ArrayList<IDBlock> orez = new ArrayList<IDBlock>();
        for (Map.Entry<IDBlock, Integer> en : ores.entrySet()) {
            addOres(orez, en.getKey(), en.getValue());
        }
        while (explosion.getBlock().getTypeId() == 0) {
            explosion.add(0, -1, 0);
        }
        explosion.add(0, radius + 1, 0);
        this.makeSphere(explosion, null, radius, true, true, orez);
        this.makeSphere(explosion, Block.OBSIDIAN.id, radius, false, false, null);
    }

    void makeSphere(Location pos, Integer block, double radius,
                    boolean filled, boolean random, ArrayList<IDBlock> m) {
        double radius_ = radius + 0.5;
        final int ceilRadiusX, ceilRadiusY, ceilRadiusZ;
        final double invRadiusX, invRadiusY, invRadiusZ;

        invRadiusX = invRadiusY = invRadiusZ = 1 / radius_;
        ceilRadiusX = ceilRadiusY = ceilRadiusZ = (int) radius_ + 1;

        double nextXn = 0;
        forX:
        for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY:
            for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break;
                    }

                    if (!filled && lengthSq(nextXn, yn, zn) <= 1
                            && lengthSq(xn, nextYn, zn) <= 1
                            && lengthSq(xn, yn, nextZn) <= 1) {
                        continue;
                    }

                    for (int i = 0; i < 8; ++i) {
                        (random ? chooseRandom(m) : new IDBlock(block.toString())).setBlock(pos.clone().add(
                                (i & 4) == 0 ? x : -x,
                                (i & 2) == 0 ? y : -y,
                                (i & 1) == 0 ? z : -z)
                                .getBlock());
                    }
                }
            }
        }

    }

    private IDBlock chooseRandom(ArrayList<IDBlock> mats) {
        return mats.get(Storm.random.nextInt(mats.size()));
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    void makeWinter() {
        CraftWorld craftworld = world.getWorld();
        int radiusSquared = snowRadius * snowRadius;

        for (int x = -snowRadius; x <= snowRadius; x++) {
            for (int z = -snowRadius; z <= snowRadius; z++) {
                if ((x * x) + (z * z) <= radiusSquared) {
                    craftworld.getHighestBlockAt((int) (x + locX),
                            (int) (z + locZ)).setBiome(Biome.TAIGA);
                }
            }
        }
    }

    /**
     * Returns the brightness of the meteor
     *
     * @param f Random _ added by MC
     * @return The brightness
     */
    @Override
    public float c(float f) {
        return this.brightness;
    }
}

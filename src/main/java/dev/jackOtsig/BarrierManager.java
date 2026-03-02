package dev.jackOtsig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Collection;

/**
 * Manages the shrinking border: tracks current radius, deals exponentially
 * scaling damage to players outside it, and shrinks on eliminations.
 */
public class BarrierManager {

    /** Horizontal distance (tiles) between consecutive particle spawn points on the ring. */
    private static final double BORDER_PARTICLE_H_SPACING = 8.0;
    /** Vertical step (tiles) between particle spawn points along the wall height. */
    private static final int    BORDER_PARTICLE_Y_STEP    = 2;
    /** Lowest particle Y offset relative to CENTER_Y (negative = below ground level). */
    private static final int    BORDER_PARTICLE_Y_MIN     = -2;
    /** Highest particle Y offset relative to CENTER_Y. */
    private static final int    BORDER_PARTICLE_Y_MAX     = 20;
    /** Particle effect ID used for the border wall visual. */
    private static final String BORDER_PARTICLE_ID = "ForgottenTemple_Circle";

    private double currentRadius;

    public BarrierManager() {
        this.currentRadius = GameConstants.INITIAL_BORDER_RADIUS;
    }

    /**
     * Called every second during ACTIVE state.
     *
     * @param alivePlayers current alive player set
     * @param entityStore  world EntityStore for ECS operations; may be null before world init
     */
    public void onSecondTick(Collection<PlayerData> alivePlayers, EntityStore entityStore) {
        currentRadius = Math.max(0, currentRadius - GameConstants.BORDER_SHRINK_RATE);
        updateBorderVisual(entityStore);

        if (entityStore == null) return;
        Store<EntityStore> store = entityStore.getStore();

        for (PlayerData pd : alivePlayers) {
            if (!pd.isAlive()) continue;
            if (isOutsideBorder(pd.getPlayer())) {
                pd.incrementSecondsOutsideBorder();
                int secondsOut = pd.getSecondsOutsideBorder();
                double damage = GameConstants.BORDER_DAMAGE_BASE
                        * Math.pow(2, secondsOut - 1);
                applyDamage(pd.getPlayer(), damage, store);
                warnPlayer(pd.getPlayer(), secondsOut, damage);
            } else {
                if (pd.getSecondsOutsideBorder() > 0) {
                    pd.getPlayer().sendMessage(Message.raw("§aYou are back inside the border."));
                }
                pd.resetSecondsOutsideBorder();
            }
        }
    }

    /** Shrinks the border by a fixed amount when a player is eliminated. */
    public void onPlayerEliminated() {
        currentRadius = Math.max(0, currentRadius - GameConstants.BORDER_SHRINK_PER_ELIMINATION);
    }

    /** Resets the border to its initial size for a new game. */
    public void reset() {
        currentRadius = GameConstants.INITIAL_BORDER_RADIUS;
    }

    public double getCurrentRadius() { return currentRadius; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isOutsideBorder(Player player) {
        Vector3d pos = player.getPlayerRef().getTransform().getPosition();
        double dx = pos.x - GameConstants.CENTER_X;
        double dz = pos.z - GameConstants.CENTER_Z;
        return (dx * dx + dz * dz) > currentRadius * currentRadius;
    }

    private void applyDamage(Player player, double amount, Store<EntityStore> store) {
        Ref<EntityStore> ref = player.getPlayerRef().getReference();
        Damage dmg = new Damage(
                new Damage.EnvironmentSource("barrier"),
                DamageCause.ENVIRONMENT,
                (float) amount);
        DamageSystems.executeDamage(ref, store, dmg);
    }

    private void warnPlayer(Player player, int secondsOut, double damage) {
        if (secondsOut == 1) {
            player.sendMessage(Message.raw(
                    "§cYou are outside the border! Return immediately or take increasing damage!"));
        } else if (secondsOut % 5 == 0) {
            player.sendMessage(Message.raw(String.format(
                    "§c[%ds outside border] Taking %.0f damage/s — get back now!", secondsOut, damage)));
        }
    }

    private void updateBorderVisual(EntityStore entityStore) {
        if (entityStore == null || currentRadius <= 0) return;
        Store<EntityStore> store = entityStore.getStore();

        double circumference = 2 * Math.PI * currentRadius;
        int hPoints = Math.max(1, (int) (circumference / BORDER_PARTICLE_H_SPACING));

        for (int i = 0; i < hPoints; i++) {
            double angle = (2 * Math.PI * i) / hPoints;
            double px = GameConstants.CENTER_X + Math.cos(angle) * currentRadius;
            double pz = GameConstants.CENTER_Z + Math.sin(angle) * currentRadius;

            for (int dy = BORDER_PARTICLE_Y_MIN; dy <= BORDER_PARTICLE_Y_MAX; dy += BORDER_PARTICLE_Y_STEP) {
                ParticleUtil.spawnParticleEffect(
                        BORDER_PARTICLE_ID,
                        new Vector3d(px, GameConstants.CENTER_Y + dy, pz),
                        store);
            }
        }
    }
}

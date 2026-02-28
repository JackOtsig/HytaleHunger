package dev.jackOtsig;

import com.hypixel.hytale.server.core.entity.entities.Player;

import java.util.Collection;

/**
 * Manages the shrinking border: tracks current radius, deals exponentially
 * scaling damage to players outside it, and shrinks on eliminations.
 */
public class BarrierManager {

    private double currentRadius;

    public BarrierManager() {
        this.currentRadius = GameConstants.INITIAL_BORDER_RADIUS;
    }

    /** Called every second during ACTIVE state. */
    public void onSecondTick(Collection<PlayerData> alivePlayers) {
        currentRadius = Math.max(0, currentRadius - GameConstants.BORDER_SHRINK_RATE);
        updateBorderVisual();

        for (PlayerData pd : alivePlayers) {
            if (!pd.isAlive()) continue;
            if (isOutsideBorder(pd.getPlayer())) {
                pd.incrementSecondsOutsideBorder();
                double damage = GameConstants.BORDER_DAMAGE_BASE
                        * Math.pow(2, pd.getSecondsOutsideBorder() - 1);
                applyDamage(pd.getPlayer(), damage);
            } else {
                pd.resetSecondsOutsideBorder();
            }
        }
    }

    /** Shrinks the border by a fixed amount when a player is eliminated. */
    public void onPlayerEliminated() {
        currentRadius = Math.max(0, currentRadius - GameConstants.BORDER_SHRINK_PER_ELIMINATION);
        updateBorderVisual();
    }

    /** Resets the border to its initial size for a new game. */
    public void reset() {
        currentRadius = GameConstants.INITIAL_BORDER_RADIUS;
        updateBorderVisual();
    }

    public double getCurrentRadius() { return currentRadius; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isOutsideBorder(Player player) {
        // TODO: Get player X/Z position and compare to currentRadius — Hytale API unknown
        // Example: return Math.sqrt(player.getX()*player.getX() + player.getZ()*player.getZ()) > currentRadius;
        return false;
    }

    private void applyDamage(Player player, double amount) {
        // TODO: Deal 'amount' HP of damage to player — Hytale API unknown
    }

    private void updateBorderVisual() {
        // TODO: Update the visual border ring to currentRadius — Hytale API unknown
    }
}

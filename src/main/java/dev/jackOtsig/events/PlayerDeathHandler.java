package dev.jackOtsig.events;

import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.jackOtsig.GameManager;

/**
 * Routes player-death events into {@link GameManager#onPlayerDeath(Player, Player)}.
 *
 * TODO: The actual Hytale player death event class and registration are unknown.
 *       Replace the parameter type and registration call once the API is known.
 */
public class PlayerDeathHandler {

    private final GameManager gameManager;

    public PlayerDeathHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Called when a player dies in-game.
     *
     * TODO: Replace Object with the actual Hytale PlayerDeathEvent type — Hytale API unknown.
     *
     * @param event  the death event (type unknown)
     */
    public void onPlayerDeath(Object event) {
        // TODO: Extract victim and killer from the event — Hytale API unknown
        // Example once API is known:
        //   Player victim = event.getVictim();
        //   Player killer = event.getKiller(); // may be null
        //   gameManager.onPlayerDeath(victim, killer);
        Player victim = null;  // TODO
        Player killer = null;  // TODO (nullable)
        if (victim != null) {
            gameManager.onPlayerDeath(victim, killer);
        }
    }
}

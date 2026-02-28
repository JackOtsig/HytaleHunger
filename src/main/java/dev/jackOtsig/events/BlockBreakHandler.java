package dev.jackOtsig.events;

import dev.jackOtsig.GameManager;
import dev.jackOtsig.GameState;

/**
 * Denies block breaking while the game is in WAITING state.
 *
 * TODO: The actual Hytale block-break event class and registration are unknown.
 *       Replace the parameter type and registration call once the API is known.
 */
public class BlockBreakHandler {

    private final GameManager gameManager;

    public BlockBreakHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Called when a player attempts to break a block.
     *
     * TODO: Replace Object with the actual Hytale BlockBreakEvent type — Hytale API unknown.
     *
     * @param event  the block-break event (type unknown)
     */
    public void onBlockBreak(Object event) {
        if (gameManager.getState() == GameState.WAITING) {
            // TODO: Cancel the event — Hytale API unknown
            // Example: event.setCancelled(true);
        }
    }
}

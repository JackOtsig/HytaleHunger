package dev.jackOtsig.events;

import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import dev.jackOtsig.GameManager;
import dev.jackOtsig.GameState;

/**
 * Denies block breaking while the game is in WAITING or PRE_START state.
 *
 * BreakBlockEvent extends CancellableEcsEvent — it is NOT a regular IEvent and
 * cannot be registered via getEventRegistry(). It must be handled via an
 * EntityEventSystem<EntityStore, BreakBlockEvent> subclass registered through
 * getEntityStoreRegistry(). That registration API is not yet confirmed.
 *
 * TODO: Convert to EntityEventSystem<EntityStore, BreakBlockEvent> and register
 *       via HungerGames.getEntityStoreRegistry().
 */
public class BlockBreakHandler {

    private final GameManager gameManager;

    public BlockBreakHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Called when a player attempts to break a block.
     * Cancel API confirmed: event.setCancelled(true).
     */
    public void onBlockBreak(BreakBlockEvent event) {
        GameState state = gameManager.getState();
        if (state == GameState.WAITING || state == GameState.PRE_START) {
            event.setCancelled(true);
        }
    }
}

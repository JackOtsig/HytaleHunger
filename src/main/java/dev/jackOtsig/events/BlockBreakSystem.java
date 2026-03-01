package dev.jackOtsig.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;
import dev.jackOtsig.GameState;

/**
 * Cancels block-breaking while the game is in WAITING or PRE_START state.
 *
 * Registered via HungerGames.getEntityStoreRegistry().registerSystem(new BlockBreakSystem(gameManager)).
 */
public class BlockBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final GameManager gameManager;

    public BlockBreakSystem(GameManager gameManager) {
        super(BreakBlockEvent.class);
        this.gameManager = gameManager;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, BreakBlockEvent event) {
        GameState state = gameManager.getState();
        if (state == GameState.WAITING || state == GameState.PRE_START) {
            event.setCancelled(true);
        }
    }
}

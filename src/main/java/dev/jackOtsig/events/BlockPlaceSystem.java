package dev.jackOtsig.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;
import dev.jackOtsig.GameState;

/**
 * Cancels block-placing while the game has not yet started (WAITING, VOTING, PRE_START).
 *
 * Registered via HungerGames.getEntityStoreRegistry().registerSystem(new BlockPlaceSystem(gameManager)).
 */
public class BlockPlaceSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private final GameManager gameManager;

    public BlockPlaceSystem(GameManager gameManager) {
        super(PlaceBlockEvent.class);
        this.gameManager = gameManager;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, PlaceBlockEvent event) {
        GameState state = gameManager.getState();
        if (state == GameState.WAITING || state == GameState.VOTING || state == GameState.PRE_START) {
            event.setCancelled(true);
        }
    }
}

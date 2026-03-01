package dev.jackOtsig.events;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;

/**
 * Caches the EntityStore reference in GameManager as soon as the first entity
 * is added to the world. This must run before any ECS operations (damage,
 * teleport, setGameMode) are attempted from the scheduler thread.
 *
 * Registered via HungerGames.getEntityStoreRegistry().registerSystem(new WorldInitSystem(gameManager)).
 */
public class WorldInitSystem extends HolderSystem<EntityStore> {

    private final GameManager gameManager;

    public WorldInitSystem(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void onEntityAdd(Holder<EntityStore> holder, AddReason reason, Store<EntityStore> store) {
        // Cache the EntityStore once — safe to call repeatedly (GameManager guards against re-init).
        gameManager.initEntityStore(store.getExternalData());
    }

    @Override
    public void onEntityRemoved(Holder<EntityStore> holder, RemoveReason reason, Store<EntityStore> store) {
        // No action needed on removal.
    }
}

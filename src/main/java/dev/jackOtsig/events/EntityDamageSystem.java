package dev.jackOtsig.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;
import dev.jackOtsig.GameState;

/**
 * During PRE_START, cancels damage dealt by a player to any non-player entity.
 * Player-vs-player damage is unaffected (handled separately by Invulnerable component).
 *
 * Registered via HungerGames.getEntityStoreRegistry().registerSystem(new EntityDamageSystem(gameManager)).
 */
public class EntityDamageSystem extends DamageEventSystem {

    private final GameManager gameManager;

    public EntityDamageSystem(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, Damage event) {
        if (gameManager.getState() != GameState.PRE_START) return;

        // Attacker must be a player entity.
        if (!(event.getSource() instanceof Damage.EntitySource src)) return;
        if (store.getComponent(src.getRef(), Player.getComponentType()) == null) return;

        // Target must not be a player entity.
        Ref<EntityStore> targetRef = chunk.getReferenceTo(entityIndex);
        if (store.getComponent(targetRef, Player.getComponentType()) != null) return;

        event.setCancelled(true);
    }
}

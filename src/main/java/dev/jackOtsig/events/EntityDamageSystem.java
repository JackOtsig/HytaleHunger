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
 * During lobby (WAITING/VOTING) and starting (PRE_START), cancels all damage
 * dealt by a player — to other players, mobs, or any entity.
 * Players are already protected from incoming damage by the Invulnerable component;
 * this system covers the outgoing side and mobs.
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
        GameState state = gameManager.getState();
        if (state != GameState.WAITING && state != GameState.VOTING && state != GameState.PRE_START) return;

        // Cancel all damage where the source is a player.
        if (!(event.getSource() instanceof Damage.EntitySource src)) return;
        if (store.getComponent(src.getRef(), Player.getComponentType()) == null) return;

        event.setCancelled(true);
    }
}

package dev.jackOtsig.events;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;

/**
 * Detects player deaths by listening for DeathComponent being added to entities.
 *
 * Registered via HungerGames.getEntityStoreRegistry().registerSystem(new PlayerDeathSystem(gameManager)).
 */
public class PlayerDeathSystem extends RefChangeSystem<EntityStore, DeathComponent> {

    private final GameManager gameManager;

    public PlayerDeathSystem(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public ComponentType<EntityStore, DeathComponent> componentType() {
        return DeathComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(Ref<EntityStore> ref, DeathComponent death,
                                  Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        // Resolve the Player component from the dying entity.
        Player victim = store.getComponent(ref, Player.getComponentType());
        if (victim == null) return; // not a player entity

        // Extract killer from the damage source, if available.
        Player killer = null;
        Damage deathDamage = death.getDeathInfo();
        if (deathDamage != null && deathDamage.getSource() instanceof Damage.EntitySource src) {
            killer = store.getComponent(src.getRef(), Player.getComponentType());
        }

        // Cache EntityStore early (in case WorldInitSystem hasn't fired yet for some reason).
        gameManager.initEntityStore(store.getExternalData());

        // Delegate to game logic — pass ref + store so setSpectator can run ECS ops in-context.
        gameManager.onPlayerDeath(victim, killer, ref, store);
    }

    @Override
    public void onComponentSet(Ref<EntityStore> ref, DeathComponent oldValue, DeathComponent newValue,
                                Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        // No action needed when DeathComponent is updated.
    }

    @Override
    public void onComponentRemoved(Ref<EntityStore> ref, DeathComponent death,
                                    Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        // No action needed when DeathComponent is removed (e.g. on respawn).
    }
}

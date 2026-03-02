package dev.jackOtsig.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameConstants;
import dev.jackOtsig.GameManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * /setorigin — sets the map origin to the executing player's current position.
 *
 * Stand at the geographical centre of the hand-built map and run this once.
 * Each game will then spawn the cornucopia at a random point within
 * {@link GameConstants#CORNUCOPIA_WANDER_RADIUS} tiles of this origin.
 */
public class SetCenterCommand extends AbstractCommand {

    private final GameManager gameManager;

    public SetCenterCommand(GameManager gameManager) {
        super("setorigin", "Set the map origin for cornucopia placement (admin)");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        EntityStore es = gameManager.getEntityStore();
        if (es == null) {
            context.sendMessage(Message.raw("World not initialised yet — try again in a moment."));
            return CompletableFuture.completedFuture(null);
        }

        Ref<EntityStore> ref = context.senderAsPlayerRef();
        es.getWorld().execute(() -> {
            var pos = es.getStore()
                        .getComponent(ref, TransformComponent.getComponentType())
                        .getPosition();
            GameConstants.MAP_ORIGIN_X = pos.x;
            GameConstants.MAP_ORIGIN_Y = pos.y;
            GameConstants.MAP_ORIGIN_Z = pos.z;
            context.sendMessage(Message.raw(String.format(
                    "Map origin set to (%.1f, %.1f, %.1f). Cornucopia will spawn within %.0f tiles of this point.",
                    pos.x, pos.y, pos.z, GameConstants.CORNUCOPIA_WANDER_RADIUS)));
        });
        return CompletableFuture.completedFuture(null);
    }
}

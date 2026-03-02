package dev.jackOtsig.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.jackOtsig.GameConstants;

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

    public SetCenterCommand() {
        super("setorigin", "Set the map origin for cornucopia placement (admin)");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        Player player = context.senderAs(Player.class);
        Vector3d pos = player.getPlayerRef().getTransform().getPosition();

        GameConstants.MAP_ORIGIN_X = pos.x;
        GameConstants.MAP_ORIGIN_Y = pos.y;
        GameConstants.MAP_ORIGIN_Z = pos.z;

        context.sendMessage(Message.raw(String.format(
                "Map origin set to (%.1f, %.1f, %.1f). Cornucopia will spawn within %.0f tiles of this point.",
                pos.x, pos.y, pos.z, GameConstants.CORNUCOPIA_WANDER_RADIUS)));
        return CompletableFuture.completedFuture(null);
    }
}

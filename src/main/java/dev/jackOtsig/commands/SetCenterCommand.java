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
 * /addcenter — registers the executing player's current position as a valid
 * cornucopia center. One entry is chosen at random at the start of each game.
 *
 * Run this while standing at each cornucopia you want in the rotation.
 * The default (0, 64, 0) fallback is always present; added positions supplement it.
 */
public class SetCenterCommand extends AbstractCommand {

    public SetCenterCommand() {
        super("addcenter", "Add your current position to the cornucopia center rotation");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        Player player = context.senderAs(Player.class);
        Vector3d pos = player.getPlayerRef().getTransform().getPosition();

        GameConstants.MAP_CENTERS.add(new double[]{pos.x, pos.y, pos.z});

        context.sendMessage(Message.raw(String.format(
                "Center (%.1f, %.1f, %.1f) added. %d center(s) in rotation.",
                pos.x, pos.y, pos.z, GameConstants.MAP_CENTERS.size())));
        return CompletableFuture.completedFuture(null);
    }
}

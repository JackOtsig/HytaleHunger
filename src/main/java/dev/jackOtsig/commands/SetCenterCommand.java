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
 * /setcenter — sets the cornucopia (map center) to the executing player's
 * current position.
 *
 * Run this while standing at the cornucopia before starting the server for
 * a new map. Updates {@link GameConstants#CENTER_X/Y/Z} at runtime so that
 * all chest placement, spawn ring, and barrier calculations use the new origin.
 */
public class SetCenterCommand extends AbstractCommand {

    public SetCenterCommand() {
        super("setcenter", "Set the cornucopia center to your current position");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        Player player = context.senderAs(Player.class);
        Vector3d pos = player.getPlayerRef().getTransform().getPosition();

        GameConstants.CENTER_X = pos.x;
        GameConstants.CENTER_Y = pos.y;
        GameConstants.CENTER_Z = pos.z;

        context.sendMessage(Message.raw(String.format(
                "Center set to (%.1f, %.1f, %.1f). Chests and spawn ring will use this origin.",
                pos.x, pos.y, pos.z)));
        return CompletableFuture.completedFuture(null);
    }
}

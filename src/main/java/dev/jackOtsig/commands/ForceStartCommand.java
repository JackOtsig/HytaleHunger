package dev.jackOtsig.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.jackOtsig.GameManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * /forcestart — admin command that skips the player-count and vote requirements
 * and immediately begins PRE_START. Requires at least 2 players in WAITING state.
 */
public class ForceStartCommand extends AbstractCommand {

    private final GameManager gameManager;

    public ForceStartCommand(GameManager gameManager) {
        super("forcestart", "Force the game to start immediately (admin)");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String error = gameManager.forceStart();
        if (error != null) {
            context.sendMessage(Message.raw(error));
        }
        return CompletableFuture.completedFuture(null);
    }
}

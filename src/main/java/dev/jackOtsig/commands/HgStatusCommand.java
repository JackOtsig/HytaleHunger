package dev.jackOtsig.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.jackOtsig.GameManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * /hgstatus — prints the current game state, player/alive counts, phase
 * timer, border radius, and center coordinates to the command sender.
 */
public class HgStatusCommand extends AbstractCommand {

    private final GameManager gameManager;

    public HgStatusCommand(GameManager gameManager) {
        super("hgstatus", "Show current Hunger Games status (admin)");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw(gameManager.getStatus()));
        return CompletableFuture.completedFuture(null);
    }
}

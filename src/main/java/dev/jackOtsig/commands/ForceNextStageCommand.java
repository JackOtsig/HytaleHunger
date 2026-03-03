package dev.jackOtsig.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.jackOtsig.GameManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * /forcenext — admin command that advances the game one stage forward, bypassing all
 * normal requirements (player count, timers, vote threshold).
 *
 * Cycle: WAITING → PRE_START → ACTIVE → WAITING (reset)
 *
 * While admin mode is active (set on first advance), the 1-player win condition is
 * suppressed so the game does not immediately end during testing.
 */
public class ForceNextStageCommand extends AbstractCommand {

    private final GameManager gameManager;

    public ForceNextStageCommand(GameManager gameManager) {
        super("forcenext", "Force the game to advance to the next stage (admin)");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String result = gameManager.forceNextStage();
        context.sendMessage(Message.raw(result));
        return CompletableFuture.completedFuture(null);
    }
}

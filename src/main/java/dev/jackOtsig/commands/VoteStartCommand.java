package dev.jackOtsig.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.jackOtsig.VoteManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * /votestart — registers a player's vote to start the game early.
 *
 * Once >50% of players have voted, the VOTING countdown begins.
 */
public class VoteStartCommand extends AbstractCommand {

    private final VoteManager voteManager;

    public VoteStartCommand(VoteManager voteManager) {
        super("votestart", "Vote to start the Hunger Games early");
        this.voteManager = voteManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }
        Player player = context.senderAs(Player.class);
        voteManager.registerVote(player);
        return CompletableFuture.completedFuture(null);
    }
}

package dev.jackOtsig;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks /votestart votes and notifies GameManager when the threshold is crossed.
 */
public class VoteManager {

    private final GameManager gameManager;
    private final Set<UUID> voters = new HashSet<>();

    public VoteManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Records a vote from the given player.
     * Triggers {@link GameManager#onVoteThresholdReached()} if >50% of
     * current players have voted.
     */
    public void registerVote(Player player) {
        if (gameManager.getState() != GameState.WAITING) {
            player.sendMessage(Message.raw("Voting is not active right now."));
            return;
        }

        // TODO: use player.getUniqueId() — Hytale API unknown
        UUID id = UUID.nameUUIDFromBytes(
                player.getDisplayName().getBytes(StandardCharsets.UTF_8));

        if (voters.contains(id)) {
            player.sendMessage(Message.raw("You have already voted."));
            return;
        }

        voters.add(id);
        int total = gameManager.getPlayerCount();
        int needed = (int) Math.ceil(total * GameConstants.VOTE_START_THRESHOLD);
        player.sendMessage(Message.raw(
                "Vote recorded! (" + voters.size() + "/" + needed + " needed)"));

        gameManager.broadcast(player.getDisplayName()
                + " voted to start! (" + voters.size() + "/" + needed + ")");

        if (voters.size() >= needed && total > 0) {
            gameManager.onVoteThresholdReached();
        }
    }

    /** Clears all votes, typically on game reset. */
    public void reset() {
        voters.clear();
    }

    public int getVoteCount() { return voters.size(); }
}

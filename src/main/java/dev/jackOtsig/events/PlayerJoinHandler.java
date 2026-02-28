package dev.jackOtsig.events;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.jackOtsig.GameManager;

/** Routes {@link PlayerReadyEvent} into {@link GameManager#onPlayerJoin(Player)}. */
public class PlayerJoinHandler {

    private final GameManager gameManager;

    public PlayerJoinHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void onPlayerReady(PlayerReadyEvent event) {
        gameManager.onPlayerJoin(event.getPlayer());
    }
}

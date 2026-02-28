package dev.jackOtsig;

import com.hypixel.hytale.server.core.entity.entities.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Holds per-player state for the duration of a game. */
public class PlayerData {

    private final Player player;
    private final UUID uuid;
    private int kills;
    private boolean alive;
    private int secondsOutsideBorder;

    public PlayerData(Player player) {
        this.player = player;
        // TODO: use player.getUniqueId() once available — Hytale API unknown
        this.uuid = UUID.nameUUIDFromBytes(
                player.getDisplayName().getBytes(StandardCharsets.UTF_8));
        this.kills = 0;
        this.alive = true;
        this.secondsOutsideBorder = 0;
    }

    public Player getPlayer() { return player; }
    public UUID getUuid() { return uuid; }
    public String getDisplayName() { return player.getDisplayName(); }

    public int getKills() { return kills; }
    public void incrementKills() { kills++; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public int getSecondsOutsideBorder() { return secondsOutsideBorder; }
    public void incrementSecondsOutsideBorder() { secondsOutsideBorder++; }
    public void resetSecondsOutsideBorder() { secondsOutsideBorder = 0; }
}

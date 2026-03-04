package dev.jackOtsig;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;

/** Holds per-player state for the duration of a game. */
public class PlayerData {

    private final Player player;
    private int kills;
    private boolean alive;
    private int secondsOutsideBorder;

    /** Ring position assigned during PRE_START. Null outside PRE_START. */
    private Vector3d frozenPos;
    private float frozenYaw;

    public PlayerData(Player player) {
        this.player = player;
        this.kills = 0;
        this.alive = true;
        this.secondsOutsideBorder = 0;
    }

    public Player getPlayer() { return player; }
    public String getDisplayName() { return player.getDisplayName(); }

    public int getKills() { return kills; }
    public void incrementKills() { kills++; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public int getSecondsOutsideBorder() { return secondsOutsideBorder; }
    public void incrementSecondsOutsideBorder() { secondsOutsideBorder++; }
    public void resetSecondsOutsideBorder() { secondsOutsideBorder = 0; }

    public void setFrozenPos(Vector3d pos, float yaw) { this.frozenPos = pos; this.frozenYaw = yaw; }
    public Vector3d getFrozenPos() { return frozenPos; }
    public float getFrozenYaw() { return frozenYaw; }
    public void clearFrozenPos() { frozenPos = null; frozenYaw = 0; }
}

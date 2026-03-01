package dev.jackOtsig.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.jackOtsig.PlayerData;

import java.util.Collections;
import java.util.List;

/**
 * Per-player HUD that displays game timer, alive count, and top-kill leaderboard.
 *
 * Data is pushed via {@link #setData} each second, then {@link #refresh()} calls
 * {@code update(false, new UICommandBuilder())} to push the new state to the client.
 */
public class GameHud extends CustomUIHud {

    private String timer      = "00:00";
    private int    aliveCount = 0;
    private List<PlayerData> topKillers = Collections.emptyList();
    private String winner     = null;   // set when game ends

    public GameHud(PlayerRef playerRef) {
        super(playerRef);
    }

    /** Called every second during ACTIVE state to update displayed values. */
    public void setData(String timer, int aliveCount, List<PlayerData> topKillers) {
        this.timer      = timer;
        this.aliveCount = aliveCount;
        this.topKillers = topKillers;
        this.winner     = null;
    }

    /** Called at game end to switch the HUD to a winner screen. */
    public void setWinner(String winnerName) {
        this.winner = winnerName;
    }

    /** Pushes the current data to the client. Call after setData / setWinner. */
    public void refresh() {
        update(false, new UICommandBuilder());
    }

    // ── CustomUIHud ──────────────────────────────────────────────────────────

    @Override
    protected void build(UICommandBuilder builder) {
        if (winner != null) {
            builder.set("winner", winner);
            return;
        }

        builder.set("timer",       timer);
        builder.set("alive_count", aliveCount);

        for (int i = 0; i < topKillers.size(); i++) {
            PlayerData pd = topKillers.get(i);
            builder.set("killer_name_"  + i, pd.getDisplayName());
            builder.set("killer_kills_" + i, pd.getKills());
        }
    }
}

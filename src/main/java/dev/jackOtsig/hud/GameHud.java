package dev.jackOtsig.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.jackOtsig.PlayerData;

import java.util.Collections;
import java.util.List;

/**
 * Per-player HUD showing timer, alive count, and top-kill leaderboard.
 *
 * {@link #show()} appends the full layout markup then sets initial values (clear=true).
 * {@link #refresh()} sends only value-update Set commands (clear=false) every second.
 *
 * Label bindings (matched by #id in LAYOUT):
 *   #timer       — game clock, e.g. "04:32"
 *   #alive_count — e.g. "15 remaining"
 *   #killer_0..4 — leaderboard rows, e.g. "PlayerName x3"
 */
public class GameHud extends CustomUIHud {

    private static final int LEADERBOARD_SIZE = 5;

    /**
     * Full HUD layout appended once during show().
     * Labels with #id receive values via UICommandBuilder.set() in build().
     */
    private static final String LAYOUT = """
            Group #hg_hud {
              LayoutMode: Left;
              Anchor: (Top: 15);

              Group { FlexWeight: 1; }

              Group #hg_panel {
                Anchor: (Width: 220, Right: 15);
                Background: #000000(0.6);
                Padding: (Horizontal: 12, Vertical: 10);
                LayoutMode: Top;

                Label #timer {
                  Anchor: (Height: 40);
                  Style: (FontSize: 28, HorizontalAlignment: Center);
                }

                Label #alive_count {
                  Anchor: (Height: 28, Top: 6);
                  Style: (FontSize: 18, HorizontalAlignment: Center);
                }

                Label {
                  Text: "-- Top Kills --";
                  Anchor: (Height: 22, Top: 8);
                  Style: (FontSize: 13, HorizontalAlignment: Center);
                }

                Label #killer_0 { Anchor: (Height: 20); Style: (FontSize: 14, HorizontalAlignment: Center); }
                Label #killer_1 { Anchor: (Height: 20); Style: (FontSize: 14, HorizontalAlignment: Center); }
                Label #killer_2 { Anchor: (Height: 20); Style: (FontSize: 14, HorizontalAlignment: Center); }
                Label #killer_3 { Anchor: (Height: 20); Style: (FontSize: 14, HorizontalAlignment: Center); }
                Label #killer_4 { Anchor: (Height: 20); Style: (FontSize: 14, HorizontalAlignment: Center); }
              }
            }
            """;

    private String timer      = "00:00";
    private int    aliveCount = 0;
    private List<PlayerData> topKillers = Collections.emptyList();
    private String winner     = null;

    public GameHud(PlayerRef playerRef) {
        super(playerRef);
    }

    /** Called every second during ACTIVE state to stage new values. */
    public void setData(String timer, int aliveCount, List<PlayerData> topKillers) {
        this.timer      = timer;
        this.aliveCount = aliveCount;
        this.topKillers = topKillers;
        this.winner     = null;
    }

    /** Called at game end to switch the HUD to winner-display mode. */
    public void setWinner(String winnerName) {
        this.winner = winnerName;
    }

    /**
     * Sends value-only Set commands without clearing or re-sending the layout.
     * Call after setData() or setWinner().
     */
    public void refresh() {
        UICommandBuilder builder = new UICommandBuilder();
        build(builder);
        update(false, builder);
    }

    // ── CustomUIHud overrides ─────────────────────────────────────────────────

    /**
     * Appends the full HUD layout then populates initial label values (clear=true).
     * Must be called on the ECS world thread.
     */
    @Override
    public void show() {
        UICommandBuilder builder = new UICommandBuilder();
        builder.append(LAYOUT);
        build(builder);
        update(true, builder);
    }

    /**
     * Populates all value-bound labels. Called by both show() and refresh().
     * Does not emit any layout (Append) commands.
     */
    @Override
    protected void build(UICommandBuilder builder) {
        if (winner != null) {
            // Reuse game-mode labels to show the winner without a layout swap.
            builder.set("timer",       "WINNER");
            builder.set("alive_count", winner);
            for (int i = 0; i < LEADERBOARD_SIZE; i++) {
                builder.set("killer_" + i, "");
            }
            return;
        }

        builder.set("timer",       timer);
        builder.set("alive_count", aliveCount + " remaining");

        for (int i = 0; i < LEADERBOARD_SIZE; i++) {
            if (i < topKillers.size()) {
                PlayerData pd = topKillers.get(i);
                builder.set("killer_" + i, pd.getDisplayName() + " x" + pd.getKills());
            } else {
                builder.set("killer_" + i, "");
            }
        }
    }
}

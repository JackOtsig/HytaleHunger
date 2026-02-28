package dev.jackOtsig;

public final class GameConstants {

    private GameConstants() {}

    public static final int MAX_PLAYERS = 24;

    public static final double VOTE_START_THRESHOLD = 0.50;
    public static final int VOTE_COUNTDOWN_SECONDS = 30;
    public static final int PRE_START_FREEZE_SECONDS = 15;

    public static final double SPAWN_RING_RADIUS = 20.0;    // tiles from center

    public static final double INITIAL_BORDER_RADIUS = 500.0;          // tiles
    public static final double BORDER_SHRINK_RATE = 0.5;               // tiles/sec
    public static final double BORDER_SHRINK_PER_ELIMINATION = 10.0;   // tiles
    public static final double BORDER_DAMAGE_BASE = 1.0;               // HP/sec, doubles each consecutive second outside

    public static final int MAX_ITEMS_PER_CATEGORY = 5;
    public static final int CORNUCOPIA_CHEST_COUNT = 6;
    public static final int FIELD_CHEST_COUNT = 40;

    public static final int END_RESET_DELAY_SECONDS = 10;
}

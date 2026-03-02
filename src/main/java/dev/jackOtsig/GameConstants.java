package dev.jackOtsig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GameConstants {

    private GameConstants() {}

    public static final int MAX_PLAYERS = 24;

    /**
     * Registered cornucopia positions — add entries with /addcenter.
     * One is chosen at random at the start of each game.
     * Starts with a single default fallback so the game is always runnable.
     */
    public static final List<double[]> MAP_CENTERS = new ArrayList<>(
            List.of(new double[]{0.0, 64.0, 0.0}));

    private static final Random RNG = new Random();

    /**
     * Currently active center — set by {@link #pickRandomCenter()} at the
     * start of each game. All chest placement, spawn ring, and barrier
     * calculations read from these fields.
     */
    public static double CENTER_X = 0.0;
    public static double CENTER_Y = 64.0;
    public static double CENTER_Z = 0.0;

    /**
     * Picks a random entry from {@link #MAP_CENTERS} and writes it into
     * {@link #CENTER_X}, {@link #CENTER_Y}, {@link #CENTER_Z}.
     * Called once in GameManager.transitionToPreStart().
     */
    public static void pickRandomCenter() {
        if (MAP_CENTERS.isEmpty()) return;
        double[] center = MAP_CENTERS.get(RNG.nextInt(MAP_CENTERS.size()));
        CENTER_X = center[0];
        CENTER_Y = center[1];
        CENTER_Z = center[2];
    }

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

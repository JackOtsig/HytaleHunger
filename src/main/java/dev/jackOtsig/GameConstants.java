package dev.jackOtsig;

import java.util.Random;

public final class GameConstants {

    private GameConstants() {}

    public static final int MAX_PLAYERS = 24;

    /**
     * Centre of the hand-built map (world origin of the playable area).
     * Set once with /setorigin while standing at the map's geographical centre.
     */
    public static double MAP_ORIGIN_X = 0.0;
    public static double MAP_ORIGIN_Y = 64.0;
    public static double MAP_ORIGIN_Z = 0.0;

    /**
     * Maximum distance from the map origin at which a cornucopia can appear.
     * Should be small enough that the spawn ring and field chests still fit
     * comfortably inside INITIAL_BORDER_RADIUS.
     */
    public static final double CORNUCOPIA_WANDER_RADIUS = 150.0;

    private static final Random RNG = new Random();

    /**
     * Currently active cornucopia position — set by {@link #pickRandomCenter()}
     * at the start of each game. All chest placement, spawn ring, and barrier
     * calculations read from these fields.
     */
    public static double CENTER_X = MAP_ORIGIN_X;
    public static double CENTER_Y = MAP_ORIGIN_Y;
    public static double CENTER_Z = MAP_ORIGIN_Z;

    /**
     * Picks a uniformly random point within {@link #CORNUCOPIA_WANDER_RADIUS}
     * of the map origin and writes it into CENTER_X/Y/Z.
     * Called once per game in GameManager.transitionToPreStart().
     */
    public static void pickRandomCenter() {
        // Uniform distribution inside a circle: sqrt(r) * R gives uniform area sampling.
        double angle  = RNG.nextDouble() * 2 * Math.PI;
        double dist   = Math.sqrt(RNG.nextDouble()) * CORNUCOPIA_WANDER_RADIUS;
        CENTER_X = MAP_ORIGIN_X + dist * Math.cos(angle);
        CENTER_Y = MAP_ORIGIN_Y;
        CENTER_Z = MAP_ORIGIN_Z + dist * Math.sin(angle);
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

package dev.jackOtsig;

public enum GameState {
    /** Players can join and roam freely; no damage, no block breaking. */
    WAITING,
    /** >50% of players voted /votestart; 30s broadcast countdown to PRE_START. */
    VOTING,
    /** Players teleported to spawn ring, frozen for 15s before ACTIVE. */
    PRE_START,
    /** Border shrinking, combat enabled, scoreboard running. */
    ACTIVE,
    /** One player remains; winner announced, 10s delay then reset to WAITING. */
    ENDED
}

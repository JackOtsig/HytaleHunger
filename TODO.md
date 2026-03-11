# Hunger Games Plugin — TODO

> Game logic TODO, kept separate from the main network infrastructure TODO.
> For infrastructure tasks (Docker, Kubernetes, etc.) see the root TODO.md.

---

## Known Issues

- [ ] `ServerVersion` was `"0.1"` — updated to `"*"` to match current server version.
      Verify the plugin loads cleanly on the current server build.
- [ ] Manifest file is `manifest.json` (singular object). The server's own built-in
      plugins use `manifests.json` (array). Both appear to work but confirm this.

---

## Manager Stubs — Need Implementation

All three manager classes are currently empty stubs.

- [ ] **`GameManager.java`** — core game loop
      - Game stage progression: WAITING → STARTING → ACTIVE → DEATHMATCH → ENDED
      - Player elimination tracking
      - Winner detection (last player standing)
      - Game reset / lobby return after match ends

- [ ] **`BarrierManager.java`** — starting barrier logic
      - Keep players in spawn positions until game starts
      - Release barriers when STARTING stage begins
      - Tie into the spawn prefab at `Server/Prefabs/Spawn/Room/Spawn_Room_001.prefab.json`

- [ ] **`ScoreboardManager.java`** — in-game HUD
      - Show alive player count
      - Show current stage / time remaining
      - Update on player death

---

## Events — Need Implementation

- [ ] **`PlayerDeathSystem.java`** — handle player elimination
      - Detect death (from `EntityDamageSystem` or a death event)
      - Update alive player count
      - Broadcast elimination message
      - Check for win condition after each death

- [ ] **`EntityDamageSystem.java`** — damage rules
      - Block friendly fire during WAITING/STARTING stages
      - Allow damage only during ACTIVE/DEATHMATCH

- [ ] **`WorldInitSystem.java`** — world setup on server start
      - Spawn loot chests in the map
      - Place barriers at spawn points
      - Configure world settings (time, weather, etc.)

- [ ] **`BlockBreakSystem.java`** / **`BlockPlaceSystem.java`**
      - Restrict building during WAITING stage
      - Decide building rules during ACTIVE stage

---

## Loot System — Partially Stubbed

- [ ] `LootTable.java` — populate with actual Hytale item IDs
- [ ] `ItemRegistry.java` — map item names to game item types
- [ ] Integrate loot spawning into `WorldInitSystem`

---

## Map Support

- [ ] `MapManager.java` — handle map selection / rotation
- [ ] `WorldStateManager.java` — save/restore world state between games
- [ ] Define map boundaries for deathmatch zone shrink

---

## Voting System

- [ ] `VoteManager.java` — let players vote to start early
      - Threshold: >50% of players vote → start countdown
      - Display vote count in scoreboard

---

## Integration with Network Infrastructure

- [ ] When game ends (winner declared), wait 10 seconds then have all remaining
      players transferred back to a lobby via `PlayerRef.referToServer()`.
      Get lobby IP/port from the network manager: `GET http://network-manager:8080/servers?type=LOBBY`
- [ ] `network-plugin` handles shutdown automatically when players leave.
      No changes needed — just ensure the game properly ends before players disconnect.

---

*Last updated: 2026-03-11*

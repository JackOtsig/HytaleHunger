# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
mvn package        # compile and package the plugin JAR
mvn compile        # compile only
```

The Hytale server dependency (`HytaleServer-parent`) is `provided` scope and must be installed to the local Maven repo from `F:/Hytale/install/release/package/game/latest/Server/HytaleServer.jar` before building:

```bash
mvn install:install-file -Dfile="F:/Hytale/install/release/package/game/latest/Server/HytaleServer.jar" -DgroupId=com.hypixel.hytale -DartifactId=HytaleServer-parent -Dversion=1.0-SNAPSHOT -Dpackaging=jar
```

## Architecture

This is a **Hytale server plugin** implementing a Hunger Games game mode. The main class is `dev.jackOtsig.HungerGames` (declared in `src/main/resources/manifest.json`).

### Plugin Lifecycle

`HungerGames extends JavaPlugin` with three lifecycle hooks:
- `setup()` — register commands, event handlers, tasks
- `start()` — plugin is live
- `shutdown()` — cleanup

### Manager Classes

The three manager stubs (`GameManager`, `BarrierManager`, `ScoreboardManager`) are all currently empty and intended to be implemented as the game logic grows.

### Commands

Extend `AbstractCommand`, override `execute(@Nonnull CommandContext context)` returning `CompletableFuture<Void>`. Send messages via `context.sendMessage(Message.raw("..."))`.

### Events

Event handlers are static methods on plain classes, taking a Hytale event type (e.g. `PlayerReadyEvent`) as the parameter. Register them in `HungerGames.setup()`.

### Logging

Use `HytaleLogger.forEnclosingClass()` as a static field, then call e.g. `LOGGER.atInfo().log("...")`.

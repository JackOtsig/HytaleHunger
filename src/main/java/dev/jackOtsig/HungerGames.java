package dev.jackOtsig;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.jackOtsig.commands.ForceStartCommand;
import dev.jackOtsig.commands.HgStatusCommand;
import dev.jackOtsig.commands.SetCenterCommand;
import dev.jackOtsig.commands.VoteStartCommand;
import dev.jackOtsig.events.BlockBreakSystem;
import dev.jackOtsig.events.PlayerDeathSystem;
import dev.jackOtsig.events.PlayerJoinHandler;
import dev.jackOtsig.events.WorldInitSystem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HungerGames extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private GameManager gameManager;
    private ScheduledExecutorService tickScheduler;

    public HungerGames(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up Hunger Games...");

        gameManager = new GameManager();

        // Register commands.
        getCommandRegistry().registerCommand(new VoteStartCommand(gameManager.getVoteManager()));
        getCommandRegistry().registerCommand(new SetCenterCommand());
        getCommandRegistry().registerCommand(new ForceStartCommand(gameManager));
        getCommandRegistry().registerCommand(new HgStatusCommand(gameManager));

        // Register player-join event.
        // PlayerReadyEvent is keyed (KeyType = String); registerGlobal subscribes to all keys.
        PlayerJoinHandler joinHandler = new PlayerJoinHandler(gameManager);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, joinHandler::onPlayerReady);

        // Register ECS systems via the entity-store registry.
        // WorldInitSystem: caches EntityStore as soon as any entity enters the world.
        // BlockBreakSystem: cancels block-breaking in WAITING / PRE_START states.
        // PlayerDeathSystem: routes DeathComponent additions to GameManager.onPlayerDeath().
        getEntityStoreRegistry().registerSystem(new WorldInitSystem(gameManager));
        getEntityStoreRegistry().registerSystem(new BlockBreakSystem(gameManager));
        getEntityStoreRegistry().registerSystem(new PlayerDeathSystem(gameManager));

        // Schedule 1-second game tick.
        // TaskRegistry auto-cancels the ScheduledFuture on plugin shutdown.
        tickScheduler = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "hungergames-tick"));
        @SuppressWarnings("unchecked")
        ScheduledFuture<Void> ticker = (ScheduledFuture<Void>) tickScheduler.scheduleAtFixedRate(
                gameManager::onSecondTick, 1, 1, TimeUnit.SECONDS);
        getTaskRegistry().registerTask(ticker);
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Hunger Games plugin started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Shutting down Hunger Games...");
        if (tickScheduler != null) {
            tickScheduler.shutdownNow();
        }
    }
}

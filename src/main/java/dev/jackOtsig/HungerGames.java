package dev.jackOtsig;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.jackOtsig.commands.VoteStartCommand;
import dev.jackOtsig.events.BlockBreakHandler;
import dev.jackOtsig.events.PlayerDeathHandler;
import dev.jackOtsig.events.PlayerJoinHandler;

public class HungerGames extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private GameManager gameManager;

    public HungerGames(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up Hunger Games...");

        gameManager = new GameManager();

        PlayerJoinHandler joinHandler  = new PlayerJoinHandler(gameManager);
        PlayerDeathHandler deathHandler = new PlayerDeathHandler(gameManager);
        BlockBreakHandler breakHandler  = new BlockBreakHandler(gameManager);
        VoteStartCommand voteCmd = new VoteStartCommand(gameManager.getVoteManager());

        // TODO: Register event handlers — Hytale event-registration API unknown
        // Example pattern (replace with actual API):
        //   registerEvent(PlayerReadyEvent.class, joinHandler::onPlayerReady);
        //   registerEvent(PlayerDeathEvent.class, deathHandler::onPlayerDeath);
        //   registerEvent(BlockBreakEvent.class,  breakHandler::onBlockBreak);

        // TODO: Register commands — Hytale command-registration API unknown
        // Example pattern (replace with actual API):
        //   registerCommand(voteCmd);

        // TODO: Schedule repeating 1-second tick — Hytale scheduler API unknown
        // Example pattern (replace with actual API):
        //   scheduleRepeating(gameManager::onSecondTick, 1 /* second */);
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Hunger Games plugin started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Shutting down Hunger Games...");
        // TODO: Cancel scheduled tasks — Hytale scheduler API unknown
        // Example: cancelAllTasks();
    }
}

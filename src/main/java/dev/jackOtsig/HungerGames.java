package dev.jackOtsig;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;

public class HungerGames extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public HungerGames(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up...");
        // Register commands, event handlers, tasks...
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Plugin started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Cleaning up...");
    }
}
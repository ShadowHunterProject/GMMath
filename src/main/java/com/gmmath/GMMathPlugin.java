package com.gmmath;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GMMathPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        gameManager = new GameManager(this, configManager);
        gameManager.startAutoGameTask();

        getCommand("math").setExecutor(new MathCommand(this, gameManager, configManager));
        getServer().getPluginManager().registerEvents(new ChatListener(gameManager), this);

        getLogger().info("GMMath đã được bật thành công (API 1.21)!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopGame();
        }
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("GMMath đã được tắt!");
    }

    public void reloadPlugin() {
        configManager.loadConfig();
        gameManager.restartAutoGameTask();
    }
}

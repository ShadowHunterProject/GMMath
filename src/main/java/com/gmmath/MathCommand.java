package com.gmmath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MathCommand implements CommandExecutor {
    private final GMMathPlugin plugin;
    private final GameManager gameManager;
    private final ConfigManager configManager;

    public MathCommand(GMMathPlugin plugin, GameManager gameManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gmmath.admin")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eGMMath Plugin - Các lệnh:");
            sender.sendMessage("§e/math start §f- Bắt đầu trò chơi mới");
            sender.sendMessage("§e/math stop §f- Dừng trò chơi hiện tại");
            sender.sendMessage("§e/math reload §f- Tải lại cấu hình");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (gameManager.isGameRunning()) {
                    sender.sendMessage(configManager.getMessage("game-already-running"));
                } else {
                    gameManager.startGame();
                    sender.sendMessage(configManager.getMessage("game-force-started"));
                }
                break;
            case "stop":
                if (gameManager.isGameRunning()) {
                    gameManager.stopGame();
                    sender.sendMessage(configManager.getMessage("game-force-stopped"));
                } else {
                    sender.sendMessage(configManager.getMessage("game-not-running"));
                }
                break;
            case "reload":
                plugin.reloadPlugin();
                sender.sendMessage(configManager.getMessage("reload-success"));
                break;
            default:
                sender.sendMessage("§cLệnh không hợp lệ.");
                break;
        }
        return true;
    }
}

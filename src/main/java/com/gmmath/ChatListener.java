package com.gmmath;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final GameManager gameManager;

    public ChatListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (gameManager.isGameRunning()) {
            gameManager.handleAnswer(event.getPlayer(), event.getMessage());
        }
    }
}

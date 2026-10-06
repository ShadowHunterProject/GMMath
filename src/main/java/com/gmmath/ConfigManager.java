package com.gmmath;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ConfigManager {
    private final GMMathPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(GMMathPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean isAutoStart() {
        return config.getBoolean("auto-start", true);
    }

    public int getIntervalSeconds() {
        return config.getInt("interval-seconds", 300);
    }

    public int getDurationSeconds() {
        return config.getInt("duration-seconds", 30);
    }

    public int getMinNumber() {
        return config.getInt("math.min-number", 1);
    }

    public int getMaxNumber() {
        return config.getInt("math.max-number", 100);
    }

    public List<String> getOperators() {
        return config.getStringList("math.operators");
    }

    public List<String> getEnabledModes() {
        List<String> modes = config.getStringList("enabled-modes");
        if (modes.isEmpty()) {
            modes.add("MATH");
        }
        return modes;
    }

    public List<Map<String, String>> getTriviaList() {
        List<Map<String, String>> triviaList = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("trivia");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Map<String, String> map = new HashMap<>();
                map.put("question", section.getString(key + ".question", ""));
                map.put("answer", section.getString(key + ".answer", ""));
                triviaList.add(map);
            }
        }
        return triviaList;
    }

    public List<String> getRewards() {
        return config.getStringList("rewards");
    }

    public String getMessage(String path) {
        String prefix = config.getString("messages.prefix", "&8[&eGMMath&8] ");
        String message = config.getString("messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}

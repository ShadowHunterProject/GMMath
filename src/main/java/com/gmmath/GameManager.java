package com.gmmath;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {
    private final GMMathPlugin plugin;
    private final ConfigManager configManager;
    private final Random random;

    private boolean isGameRunning = false;
    private String currentMode = "MATH";
    private String currentQuestionString = "";
    private long currentMathResult = 0;
    private String currentTriviaAnswer = "";
    private BukkitRunnable currentTask;

    public GameManager(GMMathPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.random = new Random();
    }

    public void restartAutoGameTask() {
        stopGame();
        Bukkit.getScheduler().cancelTasks(plugin);
        startAutoGameTask();
    }

    public void startAutoGameTask() {
        if (!configManager.isAutoStart()) return;
        
        long intervalTicks = configManager.getIntervalSeconds() * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isGameRunning) {
                    startGame();
                }
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }

    public boolean startGame() {
        if (isGameRunning) {
            return false;
        }

        List<String> modes = configManager.getEnabledModes();
        if (modes.isEmpty()) {
            modes.add("MATH");
        }
        currentMode = modes.get(random.nextInt(modes.size())).toUpperCase();

        String msg;
        if (currentMode.equals("TRIVIA")) {
            if (!generateTriviaProblem()) {
                // Fallback to MATH if no trivia questions exist
                currentMode = "MATH";
                generateMathProblem();
                msg = configManager.getMessage("game-started-math").replace("%math%", currentQuestionString);
            } else {
                msg = configManager.getMessage("game-started-trivia").replace("%question%", currentQuestionString);
            }
        } else {
            generateMathProblem();
            msg = configManager.getMessage("game-started-math").replace("%math%", currentQuestionString);
        }
        
        Bukkit.broadcastMessage(msg);
        
        isGameRunning = true;

        currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    endGameNoWinner();
                }
            }
        };
        currentTask.runTaskLater(plugin, configManager.getDurationSeconds() * 20L);
        return true;
    }

    private void generateMathProblem() {
        int min = configManager.getMinNumber();
        int max = configManager.getMaxNumber();
        int num1 = random.nextInt(max - min + 1) + min;
        int num2 = random.nextInt(max - min + 1) + min;
        
        List<String> operators = configManager.getOperators();
        if (operators.isEmpty()) operators.add("+");
        String operator = operators.get(random.nextInt(operators.size()));

        switch (operator) {
            case "+":
                currentMathResult = num1 + num2;
                break;
            case "-":
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                currentMathResult = num1 - num2;
                break;
            case "*":
                currentMathResult = (long) num1 * num2;
                break;
            default:
                currentMathResult = num1 + num2;
                operator = "+";
        }
        
        currentQuestionString = num1 + " " + operator + " " + num2;
    }

    private boolean generateTriviaProblem() {
        List<Map<String, String>> triviaList = configManager.getTriviaList();
        if (triviaList.isEmpty()) {
            return false;
        }
        Map<String, String> selected = triviaList.get(random.nextInt(triviaList.size()));
        currentQuestionString = selected.get("question");
        currentTriviaAnswer = selected.get("answer");
        return true;
    }

    public void endGameNoWinner() {
        if (!isGameRunning) return;
        isGameRunning = false;
        
        String resultStr = currentMode.equals("TRIVIA") ? currentTriviaAnswer : String.valueOf(currentMathResult);
        String msg = configManager.getMessage("game-ended-no-winner")
                .replace("%result%", resultStr);
        Bukkit.broadcastMessage(msg);
    }

    public void stopGame() {
        if (!isGameRunning) return;
        if (currentTask != null) currentTask.cancel();
        isGameRunning = false;
    }

    public void handleAnswer(Player player, String answer) {
        if (!isGameRunning) return;
        
        boolean isCorrect = false;
        String resultStr = "";

        if (currentMode.equals("MATH")) {
            try {
                long playerAnswer = Long.parseLong(answer.trim());
                if (playerAnswer == currentMathResult) {
                    isCorrect = true;
                    resultStr = String.valueOf(currentMathResult);
                }
            } catch (NumberFormatException ignored) {
            }
        } else if (currentMode.equals("TRIVIA")) {
            if (answer.trim().equalsIgnoreCase(currentTriviaAnswer.trim())) {
                isCorrect = true;
                resultStr = currentTriviaAnswer;
            }
        }

        if (isCorrect) {
            if (currentTask != null) currentTask.cancel();
            isGameRunning = false;
            
            String msg = configManager.getMessage("game-won")
                    .replace("%player%", player.getName())
                    .replace("%result%", resultStr);
            Bukkit.broadcastMessage(msg);
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (String rewardCmd : configManager.getRewards()) {
                    String cmd = rewardCmd.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            });
        }
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }
}

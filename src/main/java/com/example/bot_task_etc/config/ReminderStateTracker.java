package com.example.bot_task_etc.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReminderStateTracker {

    private final Map<Long, Boolean> awaitingTime = new HashMap<>();
    private final Map<Long, Boolean> awaitingMessage = new HashMap<>();

    public void setAwaitingTime(Long userId, boolean state) {
        awaitingTime.put(userId, state);
    }

    public boolean isAwaitingTime(Long userId) {
        return awaitingTime.getOrDefault(userId, false);
    }

    public void setAwaitingMessage(Long userId, boolean state) {
        awaitingMessage.put(userId, state);
    }

    public boolean isAwaitingMessage(Long userId) {
        return awaitingMessage.getOrDefault(userId, false);
    }
}

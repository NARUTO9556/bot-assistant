package com.example.bot_task_etc.state;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReminderStateTracker {

    public enum State {
        NONE,
        AWAITING_REMINDER_TEXT,
        AWAITING_REMINDER_TIME,
        AWAITING_REMINDER_TO_EDIT,
        AWAITING_REMINDER_TO_DELETE,
    }

    private final Map<Long, State> chatState = new HashMap<>();
    private final Map<Long, String> tempReminderTexts = new HashMap<>();

    public void setState(Long chatId, State state) {
        chatState.put(chatId, state);
    }

    public State getState(Long chatId) {
        return chatState.getOrDefault(chatId, State.NONE);
    }

    public void clear(Long chatId) {
        chatState.remove(chatId);
        tempReminderTexts.remove(chatId);
    }

    public void setTempReminderTexts(Long chatId, String text) {
        tempReminderTexts.put(chatId, text);
    }

    public String getTempReminderTexts(Long chatId) {
        return tempReminderTexts.getOrDefault(chatId, "");
    }
}

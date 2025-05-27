package com.example.bot_task_etc.state;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NoteStateTracker {

    public enum State {
        NONE,
        AWAITING_NOTE_TEXT,
        AWAITING_NOTE_EDIT_ID,
        AWAITING_NEW_NOTE_TEXT,
        AWAITING_NOTE_DELETE_ID,
    }

    private final Map<Long, State> stateMap = new HashMap<>();
    private final Map<Long, String> tempNoteIdMap = new HashMap<>();

    public void setState(Long chatId, State state) {
        stateMap.put(chatId, state);
    }

    public State getState(Long chatId) {
        return stateMap.getOrDefault(chatId, State.NONE);
    }

    public void clearState(Long chatId) {
        stateMap.remove(chatId);
        tempNoteIdMap.remove(chatId);
    }

    public void setTempNoteId(Long chatId, String noteId) {
        tempNoteIdMap.put(chatId, noteId);
    }

    public String getTempNoteId(Long chatId) {
        return tempNoteIdMap.get(chatId);
    }
}

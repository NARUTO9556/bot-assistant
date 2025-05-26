package com.example.bot_task_etc.controller;

import com.example.bot_task_etc.model.Note;
import com.example.bot_task_etc.service.NoteService;
import com.example.bot_task_etc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class BotController {

    private final NoteService noteService;
    private final UserService userService;

    private final Map<Long, Boolean> awaitingNote = new ConcurrentHashMap<>();
    private final Map<Long, Integer> editNoteIndex = new ConcurrentHashMap<>();

    public void registerUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        userService.registerUser(tgUser);
    }

    public boolean isAwaitingNote(Long userId) {
        return awaitingNote.getOrDefault(userId, false);
    }

    public void setAwaitingNote(Long userId, boolean awaiting) {
        awaitingNote.put(userId, awaiting);
    }

    public void saveNote(Long chatId, String text) {
        noteService.saveNote(chatId, text);
    }

    public String listNotes(Long userId) {
        List<Note> notes = noteService.getAllNotes(userId);
        if (notes.isEmpty()) {
            return "У вас пока нет заметок.";
        }

        StringBuilder sb = new StringBuilder("📋 Ваши заметки:\n\n");
        int i = 1;
        for (Note note : notes) {
            sb.append(i++).append(". ").append(note.getText()).append("\n");
        }
        return sb.toString();
    }

    public void deleteNote(Long userId, int index) {
        noteService.deleteNoteByIndex(userId, index);
    }

    public void prepareNoteEdit(Long userId, int index) {
        editNoteIndex.put(userId, index);
    }

    public boolean isEditingNote(Long userId) {
        return editNoteIndex.containsKey(userId);
    }

    public void updateNote(Long userId, String newText) {
        Integer index = editNoteIndex.get(userId);
        if (index != null) {
            noteService.updateNoteByIndex(userId, index, newText);
            editNoteIndex.remove(userId);
        }
    }
}

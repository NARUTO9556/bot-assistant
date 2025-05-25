package com.example.bot_task_etc.controller;

import com.example.bot_task_etc.model.Note;
import com.example.bot_task_etc.service.NoteService;
import com.example.bot_task_etc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BotController {

    private final NoteService noteService;
    private final UserService userService;

    public void registerUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        userService.registerUser(tgUser);
    }

    public void saveNote(Long chatId, String text) {
        noteService.saveNote(chatId, text);
    }

    public List<Note> getUserNotes(Long chatId) {
        return noteService.getNotes(chatId);
    }

    public void deleteNote(Long id) {
        noteService.deleteNote(id);
    }
}

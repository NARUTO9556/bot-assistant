package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.Note;
import com.example.bot_task_etc.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public void saveNote(Long chatId, String text) {
        Note note = new Note();
        note.setChatId(chatId);
        note.setText(text);
        note.setCreateAt(LocalDateTime.now());
        noteRepository.save(note);
    }

    public String getAllNotes(Long chatId) {
        List<Note> notes = noteRepository.findAllByChatId(chatId);
        if (notes.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("📋 Ваши заметки:\n");
        for (Note note : notes) {
            sb.append("ID: ").append(note.getId()).append("\n");
            sb.append(note.getText()).append("\n\n");
        }
        return sb.toString();
    }

    public boolean deleteNote(Long chatId, String noteId) {
        return noteRepository.findByIdAndChatId(Long.valueOf(noteId), chatId).map(note -> {
            noteRepository.delete(note);
            return true;
        }).orElse(false);
    }

    public boolean updateNote(Long chatId, String noteId, String newText) {
        return noteRepository.findByIdAndChatId(Long.valueOf(noteId), chatId).map(note -> {
            note.setText(newText);
            noteRepository.save(note);
            return true;
        }).orElse(false);
    }
}

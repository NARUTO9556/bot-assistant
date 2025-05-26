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

    public void saveNote(Long userId, String text) {
        Note note = new Note();
        note.setUserId(userId);
        note.setText(text);
        note.setCreateAt(LocalDateTime.now());
        noteRepository.save(note);
    }

    public List<Note> getAllNotes(Long userId) {
        return noteRepository.findAllByUserId(userId);
    }

    public void deleteNoteByIndex(Long userId, int index) {
        List<Note> notes = getAllNotes(userId);
        if (index >= 1 && index <= notes.size()) {
            noteRepository.deleteById(notes.get(index - 1).getId());
        }
    }

    public void updateNoteByIndex(Long userId, int index, String newText) {
        List<Note> notes = getAllNotes(userId);
        if (index >= 1 && index <= notes.size()) {
            Note note = notes.get(index - 1);
            note.setText(newText);
            noteRepository.save(note);
        }
    }
}

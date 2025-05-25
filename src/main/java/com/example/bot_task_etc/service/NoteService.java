package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.Note;
import com.example.bot_task_etc.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Note saveNote(Long userId, String text) {
        return noteRepository.save(Note.builder().userId(userId).text(text).build());
    }

    public List<Note> getNotes(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    public void deleteNote(Long userId) {
        noteRepository.deleteById(userId);
    }
}

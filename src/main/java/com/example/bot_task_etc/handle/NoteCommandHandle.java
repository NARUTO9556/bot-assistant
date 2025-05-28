package com.example.bot_task_etc.handle;

import com.example.bot_task_etc.service.NoteService;
import com.example.bot_task_etc.state.NoteStateTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class NoteCommandHandle {

    private final NoteService noteService;
    private final NoteStateTracker noteStateTracker;

    public void handleNewNote(Long chatId, AbsSender sender) {
        noteStateTracker.setState(chatId, NoteStateTracker.State.AWAITING_NOTE_TEXT);
        send(sender, chatId, "Введите текст новой заметки");
    }

    public void handleListNotes(Long chatId, AbsSender sender) {
        String notes = noteService.getAllNotes(chatId);
        send(sender, chatId, notes.isEmpty() ? "У вас нет заметок" : notes);
    }

    public void handleEditNote(Long chatId, AbsSender sender) {
        noteStateTracker.setState(chatId, NoteStateTracker.State.AWAITING_NOTE_EDIT_ID);
        send(sender, chatId, "Введите ID заметки, которую хотите отредактировать: ");
    }

    public void handleDeleteNote(Long chatId, AbsSender sender) {
        noteStateTracker.setState(chatId, NoteStateTracker.State.AWAITING_NOTE_DELETE_ID);
        send(sender, chatId, "Введите ID заметки, которую хотите удалить: ");
    }

    public void handleTextInput(Long chatId, String msgText, AbsSender sender) {
        var state = noteStateTracker.getState(chatId);

        switch (state) {
            case AWAITING_NOTE_TEXT -> {
                noteService.saveNote(chatId, msgText);
                noteStateTracker.clearState(chatId);
                send(sender, chatId, "Заметка сохранена");
            }
            case AWAITING_NOTE_EDIT_ID -> {
                noteStateTracker.setTempNoteId(chatId, msgText);
                noteStateTracker.setState(chatId, NoteStateTracker.State.AWAITING_NEW_NOTE_TEXT);
                send(sender, chatId, "Введите новый текст для заметки: ");
            }
            case AWAITING_NEW_NOTE_TEXT -> {
                String id = noteStateTracker.getTempNoteId(chatId);
                if (noteService.updateNote(chatId, id, msgText)) {
                    send(sender, chatId, "Заметка обновлена");
                    noteStateTracker.clearState(chatId);
                } else {
                    send(sender, chatId, "Заметка не найдена");
                    noteStateTracker.clearState(chatId);
                }
            }
            case AWAITING_NOTE_DELETE_ID -> {
                if (noteService.deleteNote(chatId, msgText)) {
                    send(sender, chatId, "Заметка удалена.");
                    noteStateTracker.clearState(chatId);
                } else {
                    send(sender,chatId,"Заметка не найдена");
                    noteStateTracker.clearState(chatId);
                }
                noteStateTracker.clearState(chatId);
            }
        }
    }
    private void send(AbsSender sender, Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

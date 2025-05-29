package com.example.bot_task_etc.bot;

import com.example.bot_task_etc.config.KeyboardConfig;
import com.example.bot_task_etc.handle.NoteCommandHandle;
import com.example.bot_task_etc.handle.ReminderCommandHandle;
import com.example.bot_task_etc.service.UserService;
import com.example.bot_task_etc.state.NoteStateTracker;
import com.example.bot_task_etc.state.ReminderStateTracker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssistantBot extends TelegramLongPollingBot {

    private final ReminderCommandHandle reminderCommandHandle;
    private final ReminderStateTracker stateTracker;
    private final NoteCommandHandle noteCommandHandle;
    private final NoteStateTracker noteStateTracker;
    private final UserService userService;
    private final KeyboardConfig keyboardConfig;

    @Value("${telegrambots.bots.username}")
    private String botUsername;

    @Value("${telegrambots.bots.token}")
    private String botToken;

    @PostConstruct
    public void start() throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }

        var message = update.getMessage();
        userService.registerUser(message);
        if (message.hasText()) {
            String text = message.getText();
            Long chatId = message.getChatId();
            if ("/start".equals(text)) {
                sendText(chatId, "Привет!");
            }
        }
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (noteStateTracker.getState(chatId) != NoteStateTracker.State.NONE) {
            noteCommandHandle.handleTextInput(chatId, text, this);
            return;
        }

        if (stateTracker.getState(chatId) != ReminderStateTracker.State.NONE) {
            reminderCommandHandle.handleInput(chatId, text, this);
            return;
        }

        switch (text) {
            case "/start", "🔙 Назад" -> sendMenu(chatId, "Выберите раздел:");

            case "📝 Заметки"->
                    sendKeyboard(chatId, "Выберите действие с заметками:", keyboardConfig.getNoteMenuKeyboard());
            case "⏰ Напоминания"->
                    sendKeyboard(chatId, "Выберите действие с напоминаниями:", keyboardConfig.getReminderMenuKeyboard());

            case "➕ Новая заметка"-> noteCommandHandle.handleNewNote(chatId, this);
            case "📋 Список заметок"-> noteCommandHandle.handleListNotes(chatId, this);
            case "✏️ Изменить заметку"-> noteCommandHandle.handleEditNote(chatId, this);
            case "❌ Удалить заметку"-> noteCommandHandle.handleDeleteNote(chatId, this);

            case "➕ Новое напоминание"-> reminderCommandHandle.handleNewReminder(chatId, this);
            case "📋 Список напоминаний"-> reminderCommandHandle.handleListReminders(chatId, this);
            case "✏️ Изменить напоминание"-> reminderCommandHandle.handleEditReminders(chatId, this);
            case "❌ Удалить напоминание"-> reminderCommandHandle.handleDeleteReminders(chatId, this);
            default -> {
                sendText(chatId, "Неизвестная команда. Используйте /start.");
            }
        }

    }

    private void sendText(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenu(Long chatId, String text) {
        sendKeyboard(chatId,text, keyboardConfig.getMainMenuKeyboard());
    }


    private void sendKeyboard(Long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

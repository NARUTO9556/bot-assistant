package com.example.bot_task_etc.bot;

import com.example.bot_task_etc.config.NoteCommandHandle;
import com.example.bot_task_etc.config.ReminderCommandHandle;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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
            case "/start"-> sendMenu(chatId);
            case "📝 Заметки"-> sendNoteMenu(chatId);
            case "⏰ Напоминания"-> sendReminderMenu(chatId);

            case "➕ Новая заметка"-> noteCommandHandle.handleNewNote(chatId, this);
            case "📋 Список заметок"-> noteCommandHandle.handleListNotes(chatId, this);
            case "✏️ Редактировать"-> noteCommandHandle.handleEditNote(chatId, this);
            case "❌ Удалить заметку"-> noteCommandHandle.handleDeleteNote(chatId, this);

            case "➕ Новое напоминание"-> reminderCommandHandle.handleNewReminder(chatId, this);
            case "📋 Список напоминаний"-> reminderCommandHandle.handleListReminders(chatId, this);
            case "✏️ Изменить напоминание"-> reminderCommandHandle.handleEditReminders(chatId, this);
            case "❌ Удалить напоминание"-> reminderCommandHandle.handleDeleteReminders(chatId, this);
            case "🔙 Назад" -> {
                SendMessage msg = new SendMessage();
                msg.setChatId(chatId.toString());
                msg.setText("Вы вернулись в главное меню.");
                msg.setReplyMarkup(sendMenu(chatId));
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            default -> {
                sendText(chatId, "Неизвестная команда. Используйте /start.");
            }
        }

    }

    private void sendText(Long chatId, String text) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboard sendMenu(Long chatId) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("📝 Заметки"));
        row.add(new KeyboardButton("⏰ Напоминания"));
        return sendKeyboard(chatId, "Выберите раздел:", List.of(row));

    }

    private void sendNoteMenu(Long chatId) {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("➕ Новая заметка"));
        row1.add(new KeyboardButton("📋 Список заметок"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("✏️ Редактировать"));
        row2.add(new KeyboardButton("❌ Удалить заметку"));
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("🔙 Назад"));
        sendKeyboard(chatId, "Выберите действие с заметками:", List.of(row1, row2, row3));
    }

    private void sendReminderMenu(Long chatId) {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("➕ Новое напоминание"));
        row1.add(new KeyboardButton("📋 Список напоминаний"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("✏️ Изменить напоминание"));
        row2.add(new KeyboardButton("❌ Удалить напоминание"));
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("🔙 Назад"));
        sendKeyboard(chatId, "Выберите действие с напоминаниями:", List.of(row1, row2, row3));
    }

    private ReplyKeyboard sendKeyboard(Long chatId, String text, List<KeyboardRow> rows) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(rows);
        keyboard.setResizeKeyboard(true);

        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(keyboard)
                .build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return msg.getReplyMarkup();
    }
}

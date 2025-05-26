package com.example.bot_task_etc.bot;

import com.example.bot_task_etc.controller.BotController;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AssistantBot extends TelegramLongPollingBot {

    private final BotController botController;

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

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText().trim();
        Long userId = message.getFrom().getId();

        String command = text.toLowerCase();

        if (botController.isAwaitingNote(userId)) {
            botController.saveNote(userId, text);
            botController.setAwaitingNote(userId, false);
            send(chatId, "✅ Заметка сохранена!");
            return;
        }

        if (botController.isEditingNote(userId)) {
            botController.updateNote(userId, text);
            send(chatId, "✏️ Заметка обновлена.");
            return;
        }

        if (command.startsWith("удалить ")) {
            try {
                int index = Integer.parseInt(command.substring(8).trim());
                botController.deleteNoteByIndex(userId, index);
                send(chatId, "✅ Заметка удалена.");
            } catch (Exception e) {
                send(chatId, "⚠ Неверный формат. Используй `удалить 2`.");
            }
            return;
        }

        if (command.startsWith("редактировать ")) {
            try {
                int index = Integer.parseInt(command.substring(14).trim());
                botController.prepareNoteEdit(userId, index);
                send(chatId, "✍️ Напиши новый текст для заметки #" + index + ":");
            } catch (Exception e) {
                send(chatId, "⚠ Неверный формат. Используй `редактировать 2`.");
            }
            return;
        }

        switch (command) {
            case "/start" -> {
                botController.registerUser(message.getFrom());
                send(chatId, "👋 Привет, " + message.getFrom().getFirstName() + "!\nВыберите действие:");
            }
            case "/note", "📝 новая заметка" -> {
                botController.setAwaitingNote(userId, true);
                send(chatId, "✍️ Напиши текст заметки:");
            }
            case "/listnote", "📋 список" -> {
                send(chatId, botController.listNotes(userId));
            }
            case "/deletenote", "❌ удалить" -> {
                send(chatId, "🗑 Напиши `удалить N`, где N — номер заметки.");
            }
            default -> {
                send(chatId, "⚠ Неизвестная команда. Используйте кнопки или напишите `удалить N`, `редактировать N`.");
            }
        }
    }

    private void send(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(buildMainKeyboard())
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup buildMainKeyboard() {
        KeyboardButton newNoteButton = new KeyboardButton("📝 Новая заметка");
        KeyboardButton listButton = new KeyboardButton("📋 Список");
        KeyboardButton deleteButton = new KeyboardButton("❌ Удалить");

        KeyboardRow row1 = new KeyboardRow();
        row1.add(newNoteButton);
        row1.add(listButton);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(deleteButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true); // адаптирует под экран
        markup.setOneTimeKeyboard(false);

        return markup;
    }
}

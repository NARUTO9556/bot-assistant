package com.example.bot_task_etc.bot;

import com.example.bot_task_etc.controller.BotController;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        var message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();

        String response = "";
        switch (text) {
            case "/start" -> {
                botController.registerUser(message.getFrom());
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Привет, " + message.getFrom().getFirstName() + "! Я твой ассистент.\nВыберите действие:")
                        .replyMarkup(getMainMenuKeyboard())
                        .build();
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            case "📝 Новая заметка" -> response = "Отправь мне текст заметки, и я её сохраню.";
            case "📋 Список" -> {
                var notes = botController.getUserNotes(chatId);
                if (notes.isEmpty()) {
                    response = "У тебя пока нет заметок.";
                } else {
                    StringBuilder sb = new StringBuilder("Твои заметки:\n");
                    notes.forEach(note ->
                            sb.append(note.getId()).append(": ").append(note.getText()).append("\n"));
                    response = sb.toString();
                }
            }

            case "❌ Удалить" -> response = "Напиши ID заметки, которую нужно удалить.";
            default -> {
                if (text.startsWith("/note ")) {
                    botController.saveNote(chatId, text.substring(6));
                    response = "Заметка сохранена.";
                } else if (text.startsWith("/deletenote ")) {
                    try {
                        Long id = Long.parseLong(text.substring(12));
                        botController.deleteNote(id);
                        response = "Заметка удалена.";
                    } catch (Exception e) {
                        response = "Ошибка: ID должен быть числом.";
                    }
                } else {
                    response = """
                            Неизвестная команда.  
                            Используй /start или кнопки меню.
                            """;
                }
            }
        }

        sendMessage(chatId, response, getMainMenuKeyboard());
    }

    private void sendMessage(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardButton newNote = new KeyboardButton("📝 Новая заметка");
        KeyboardButton list = new KeyboardButton("📋 Список");
        KeyboardButton delete = new KeyboardButton("❌ Удалить");

        List<KeyboardButton> row1 = List.of(newNote, list);
        List<KeyboardButton> row2 = List.of(delete);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(List.of());
        keyboard.setResizeKeyboard(true);

        return keyboard;
    }
}

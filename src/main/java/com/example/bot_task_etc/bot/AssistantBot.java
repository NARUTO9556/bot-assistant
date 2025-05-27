package com.example.bot_task_etc.bot;

import com.example.bot_task_etc.config.NoteCommandHandle;
import com.example.bot_task_etc.config.ReminderCommandHandle;
import com.example.bot_task_etc.controller.BotController;
import com.example.bot_task_etc.model.User;
import com.example.bot_task_etc.repository.UserRepository;
import com.example.bot_task_etc.state.NoteStateTracker;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AssistantBot extends TelegramLongPollingBot {

    private final BotController botController;
    private final ReminderCommandHandle reminderCommandHandle;
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
        }


    }

    public void send(Long chatId, String text) {
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
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📝 Новая заметка"));
        row1.add(new KeyboardButton("📋 Список"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("❌ Удалить"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("⏰ Напоминание"));
        row3.add(new KeyboardButton("🔕 Отключить напоминание"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("📝 Текст напоминания"));
        row4.add(new KeyboardButton("ℹ Настройки напоминания"));


        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true); // адаптирует под экран
        markup.setOneTimeKeyboard(false);

        return markup;
    }
}

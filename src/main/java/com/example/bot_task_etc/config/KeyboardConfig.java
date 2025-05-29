package com.example.bot_task_etc.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardConfig {
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("📝 Заметки"));
        row.add(new KeyboardButton("⏰ Напоминания"));
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        return buildKeyboard(keyboard);
    }

    public ReplyKeyboardMarkup getNoteMenuKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createRow("➕ Новая заметка", "📋 Список заметок"));
        keyboard.add(createRow("✏️ Изменить заметку", "❌ Удалить заметку"));
        keyboard.add(createRow("🔙 Назад"));
        return buildKeyboard(keyboard);
    }

    public ReplyKeyboardMarkup getReminderMenuKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createRow("➕ Новое напоминание", "📋 Список напоминаний"));
        keyboard.add(createRow("✏️ Изменить напоминание", "❌ Удалить напоминание"));
        keyboard.add(createRow("🔙 Назад"));
        return buildKeyboard(keyboard);
    }

    private KeyboardRow createRow(String... labels) {
        KeyboardRow row = new KeyboardRow();
        for (String label : labels) {
            row.add(new KeyboardButton(label));
        }
        return row;
    }

    private ReplyKeyboardMarkup buildKeyboard(List<KeyboardRow> rows) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        return keyboardMarkup;
    }
}

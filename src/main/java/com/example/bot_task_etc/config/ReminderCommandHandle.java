package com.example.bot_task_etc.config;

import com.example.bot_task_etc.bot.AssistantBot;
import com.example.bot_task_etc.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderCommandHandle {

    private final ReminderService reminderService;
    private final ReminderStateTracker tracker;

    public boolean handleInput(Long chatId, Long userId, String text, AssistantBot bot) {
        if (tracker.isAwaitingTime(userId)) {
            try {
                reminderService.setReminderTime(userId, text);
                tracker.setAwaitingTime(userId, false);
                bot.send(chatId, "✅ Время напоминания установлено!");
            } catch (Exception e) {
                bot.send(chatId, "❌ Неверный формат. Введите время в формате HH:mm");
            }
            return true;
        }

        if (tracker.isAwaitingMessage(userId)) {
            reminderService.updateReminderMessage(userId, text);
            tracker.setAwaitingMessage(userId, false);
            bot.send(chatId, "✅ Текст напоминания обновлён.");
            return true;
        }
        return false;
    }

    public boolean handleCommand(Long chatId, Long userId, String command, AssistantBot bot) {
        switch (command) {
            case "/reminder", "⏰ Напоминание" -> {
                tracker.setAwaitingTime(userId, true);
                bot.send(chatId, "🕒 Введите время напоминания (например, 09:00):");
                return true;
            }
            case "🔕 Отключить напоминание" -> {
                reminderService.disableReminder(userId);
                bot.send(chatId, "🔕 Напоминание отключено.");
                return true;
            }
            case "📝 Текст напоминания" -> {
                tracker.setAwaitingMessage(userId, true);
                bot.send(chatId, "✍ Введите новый текст напоминания:");
                return true;
            }
            case "ℹ Настройки напоминания" -> {
                String info = reminderService.getReminderSettings(userId);
                bot.send(chatId, info);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}

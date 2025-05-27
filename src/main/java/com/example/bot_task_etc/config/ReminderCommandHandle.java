package com.example.bot_task_etc.config;

import com.example.bot_task_etc.bot.AssistantBot;
import com.example.bot_task_etc.model.Reminder;
import com.example.bot_task_etc.service.ReminderService;
import com.example.bot_task_etc.state.ReminderStateTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderCommandHandle {

    private final ReminderService reminderService;
    private final ReminderStateTracker tracker;

    public void handleInput(Long chatId, String text, AbsSender sender) {
        switch (tracker.getState(chatId)) {
            case AWAITING_NEW_REMINDER_TEXT -> {
                tracker.setTempReminderTexts(chatId, text);
                tracker.setState(chatId, ReminderStateTracker.State.AWAITING_REMINDER_TIME);
                sendText(sender, chatId, "Введите дату и время напоминания в формате 'yyyy-MM-dd HH:mm' (например, 2025-06-01 14:30));");
            }
            case AWAITING_REMINDER_TIME -> {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    String reminderText = tracker.getTempReminderTexts(chatId);
                    reminderService.saveReminder(chatId, reminderText, dateTime);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedDateTime = dateTime.format(formatter);
                    sendText(sender, chatId, "Напоминание сохранено на: " + formattedDateTime);
                } catch (DateTimeParseException e) {
                    sendText(sender, chatId, "Неверный формат даты и времени. Повторите ввод");
                }
                tracker.clear(chatId);
            }
            case AWAITING_REMINDER_TO_EDIT -> {
                tracker.setTempReminderTexts(chatId, text);
                tracker.setState(chatId, ReminderStateTracker.State.AWAITING_REMINDER_TIME);
                sendText(sender, chatId, "Введите новую дату и время напоминания в формате 'yyyy-MM-dd HH:mm");
            }
            case AWAITING_REMINDER_TO_DELETE -> {
                boolean deleted = reminderService.deleteReminder(chatId, text);
                sendText(sender, chatId, deleted ? "Напоминание удалено" : "Напоминание не найдено");
                tracker.setState(chatId, ReminderStateTracker.State.NONE);
            }
        }
    }

    public void handleNewReminder(Long chatId, AbsSender sender) {
        tracker.setState(chatId, ReminderStateTracker.State.AWAITING_NEW_REMINDER_TEXT);
        sendText(sender, chatId, "Введите текст нового напоминания");
    }

    public void handleListReminders(Long chatId, AbsSender sender) {
        List<Reminder> reminders = reminderService.getAllReminders(chatId);
        if (reminders.isEmpty()) {
            sendText(sender, chatId, "У вас нет напоминаний");
        } else {
            StringBuilder sb = new StringBuilder("Ваши напоминания: \n");
            reminders.forEach(reminder -> sb.append("- ")
                    .append(reminder.getText())
                    .append("(на ")
                    .append(reminder.getTime())
                    .append(")\n"));
            sendText(sender, chatId, sb.toString());
        }
    }

    public void handleEditReminders(Long chatId, AbsSender sender) {
        tracker.setState(chatId, ReminderStateTracker.State.AWAITING_REMINDER_TO_EDIT);
        sendText(sender, chatId, "Введите новый текст напоминания");
    }

    public void handleDeleteReminders(Long chatId, AbsSender sender) {
        tracker.setState(chatId, ReminderStateTracker.State.AWAITING_REMINDER_TO_DELETE);
        sendText(sender, chatId, "Введите текст напоминания, которое хотите удалить: ");
    }

    private void sendText(AbsSender sender, Long chatId, String text) {
        try {
            sender.execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

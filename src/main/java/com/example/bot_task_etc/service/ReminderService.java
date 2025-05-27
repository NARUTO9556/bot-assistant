package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.Reminder;
import com.example.bot_task_etc.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public void saveReminder(Long chatId, String text, LocalDateTime time) {
        Reminder reminder = new Reminder();
        reminder.setChatId(chatId);
        reminder.setText(text);
        reminder.setTime(time);
        reminderRepository.save(reminder);
    }

    public List<Reminder> getAllReminders(Long chatId) {
        return reminderRepository.findAllByChatId(chatId);
    }

    public void editReminder(Long chatId, String newText, LocalDateTime newTime) {
        List<Reminder> reminders = reminderRepository.findAllByChatId(chatId);
        if (!reminders.isEmpty()) {
            Reminder reminder = reminders.get(reminders.size() - 1);
            reminder.setText(newText);
            reminder.setTime(newTime);
            reminderRepository.save(reminder);
        }
    }

    public boolean deleteReminder(Long chatId, String text) {
        List<Reminder> reminders = reminderRepository.findAllByChatId(chatId);
        for (Reminder reminder : reminders) {
            if (reminder.getText().equals(text)) {
                reminderRepository.delete(reminder);
                return true;
            }
        }
        return false;
    }
}

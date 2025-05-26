package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.User;
import com.example.bot_task_etc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final UserRepository userRepository;

    public void setReminderTime(Long userId, String timeString) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setReminderTime(LocalTime.parse(timeString));
        user.setReminderEnabled(true);
        userRepository.save(user);
    }

    public void disableReminder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setReminderEnabled(false);
        userRepository.save(user);
    }

    public void updateReminderMessage(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setReminderMessage(message);
        userRepository.save(user);
    }

    public String getReminderSettings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.isReminderEnabled()
                ? "🔔 Напоминание включено\n🕒 Время: " + user.getReminderTime()
                + "\n💬 Сообщение: " + user.getReminderMessage()
                : "🔕 Напоминания отключены.";
    }
}

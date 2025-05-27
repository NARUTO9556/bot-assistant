package com.example.bot_task_etc.scheduler;

import com.example.bot_task_etc.bot.AssistantBot;
import com.example.bot_task_etc.model.User;
import com.example.bot_task_etc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final UserRepository userRepository;
    private final AssistantBot assistantBot;

    @Scheduled(cron = "0 * * * * *")
    public void sendReminders() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        List<User> users = userRepository.findAllByReminderEnabledTrue();

        for (User user : users) {
            if (now.equals(user.getReminderTime())) {
                SendMessage message = SendMessage.builder()
                        .chatId(user.getChatId().toString())
                        .text(user.getReminderMessage() != null ? user.getReminderMessage() : "🔔 Напоминание!")
                        .build();
                try {
                    assistantBot.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

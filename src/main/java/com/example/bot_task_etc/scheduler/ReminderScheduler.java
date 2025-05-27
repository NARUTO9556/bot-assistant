package com.example.bot_task_etc.scheduler;

import com.example.bot_task_etc.bot.AssistantBot;
import com.example.bot_task_etc.model.Reminder;
import com.example.bot_task_etc.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final AssistantBot assistantBot;

    @Scheduled(fixedRate = 30000)
    public void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueReminders = reminderRepository.findByTimeBeforeAndSentFalse(now);

        for (Reminder reminder : dueReminders) {
            if (reminder.getChatId() == null) {
                continue;
            }

            SendMessage msg = new SendMessage();
            msg.setChatId(reminder.getChatId().toString());
            msg.setText("🔔 Напоминание: " + reminder.getText());

            try {
                assistantBot.execute(msg);
                reminder.setSent(true);
                reminderRepository.save(reminder);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
}

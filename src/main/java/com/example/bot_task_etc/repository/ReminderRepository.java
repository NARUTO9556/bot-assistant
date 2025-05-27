package com.example.bot_task_etc.repository;

import com.example.bot_task_etc.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findAllByChatId(Long chatId);
    List<Reminder> findByTimeBeforeAndSentFalse(LocalDateTime time);
}
package com.example.bot_task_etc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime registeredAt;
    @Column(name = "reminder_time")
    private LocalTime reminderTime;
    @Column(name = "reminder_enabled")
    private boolean reminderEnabled;
    @Column(name = "reminder_message")
    private String reminderMessage = "🔔 Напоминание! Не забудьте проверить свои заметки.";
}

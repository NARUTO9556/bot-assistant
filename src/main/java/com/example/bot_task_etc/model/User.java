package com.example.bot_task_etc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime registeredAt;
}

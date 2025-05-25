package com.example.bot_task_etc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notes")
public class Note {
    @Id
    private Long id;
    private Long userId;
    private String text;
}

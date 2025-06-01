package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import com.example.bot_task_etc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(Message message) {
        org.telegram.telegrambots.meta.api.objects.User tgUser = message.getFrom();
        Long chatId = message.getChatId();

        if (userRepository.existsById(chatId)) {
            return;
        }

        User user = new User();
        user.setChatId(chatId);
        user.setUsername(tgUser.getUserName());
        user.setFirstName(tgUser.getFirstName());
        user.setLastName(tgUser.getLastName());
        user.setRegisteredAt(LocalDateTime.now().withNano(0));

        userRepository.save(user);
    }
}

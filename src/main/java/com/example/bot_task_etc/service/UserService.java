package com.example.bot_task_etc.service;

import com.example.bot_task_etc.model.User;
import com.example.bot_task_etc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        Long id = tgUser.getId();

        Optional<User> existing = userRepository.findById(id);
        if (existing.isPresent()) {
            return;
        }

        User newUser = new User();
        newUser.setId(id);
        newUser.setFirstName(tgUser.getFirstName());
        newUser.setLastName(tgUser.getLastName());
        newUser.setUsername(tgUser.getUserName());
        newUser.setRegisteredAt(LocalDateTime.now());

        userRepository.save(newUser);
    }
}

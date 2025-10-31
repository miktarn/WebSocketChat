package com.websocket.chat.user.service;

import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DomainUser create(String username, String password) {
        Optional<DomainUser> founded = userRepository.findByName(username);
        if (founded.isPresent()) {
            throw new IllegalStateException("User " + username + " already exists");
        }
        return userRepository.save(DomainUser.builder()
                .name(username)
                .password(passwordEncoder.encode(password))
                .activeChats(Collections.emptySet())
                .build());
    }

    public boolean exists(String name) {
        return userRepository.findByName(name).isPresent();
    }
}

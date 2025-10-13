package com.websocket.chat.user.service;

import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import com.websocket.chat.user.dto.UserResponseDto;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public DomainUser create(String name) {
        Optional<DomainUser> founded = userRepository.findByName(name);
        if (founded.isPresent()) {
            return founded.get();
        }
        return userRepository.save(DomainUser.builder()
                .name(name)
                .activeChats(Collections.emptySet())
                .build());
    }

    public boolean exists(String name) {
        return userRepository.findByName(name).isPresent();
    }
}

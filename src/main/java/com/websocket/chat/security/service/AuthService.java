package com.websocket.chat.security.service;

import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DomainUser login(String username, String inputPassword) {
        Optional<DomainUser> user = userRepository.findByName(username);
        if (user.isPresent() && passwordEncoder.matches(inputPassword, user.get().getPassword())) {
            return user.get();
        }
        throw new IllegalArgumentException("Bad credentials");
    }
}

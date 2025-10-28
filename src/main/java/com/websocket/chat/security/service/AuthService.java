package com.websocket.chat.security.service;

import com.websocket.chat.security.util.HashUtil;
import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public DomainUser login(String username, String inputPassword) {
        Optional<DomainUser> user = userRepository.findByName(username);
        if (user.isPresent() && passwordsMatch(inputPassword, user.get())) {
            return user.get();
        }
        throw new IllegalArgumentException("Bad credentials");
    }

    private static boolean passwordsMatch(String inputPassword, DomainUser user) {
        String existingPassword = user.getPassword();
        return existingPassword.equals(HashUtil.hashPassword(inputPassword));
    }
}

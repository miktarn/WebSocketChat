package com.websocket.chat.chat.service;


import com.websocket.chat.chat.dao.ChatRepository;
import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public List<DomainChat> getChatsByUserName(String name) {
        Optional<DomainUser> user = userRepository.findByName(name);
        if (user.isPresent()) {
            return chatRepository.findAllByParticipantsContaining(user.get());
        }
        return Collections.emptyList();
    }

    @Transactional
    public DomainChat create(String chatName, Set<String> userNames) {
        if (chatRepository.existsByName(chatName)) {
            throw new RuntimeException(String.format("Chat %s already exist", chatName));
        }
        Set<DomainUser> users = userRepository.findByNameIn(userNames);
        DomainChat newChat = DomainChat.builder()
                .name(chatName)
                .participants(users)
                .build();
        users.forEach(u -> u.getActiveChats().add(newChat));
        return chatRepository.save(newChat);
    }

    @Transactional
    public DomainChat addUserToChat(String chatName, String userName) {
        Optional<DomainChat> chat = chatRepository.findByName(chatName);
        if (chat.isPresent()) {
            DomainUser user = userRepository.findByName(userName).orElseThrow();
            chat.get().getParticipants().add(user);
            user.getActiveChats().add(chat.get());
            return chatRepository.save(chat.get());
        } else {
            throw new RuntimeException(String.format("Chat %s is not exist", chatName));
        }
    }
}

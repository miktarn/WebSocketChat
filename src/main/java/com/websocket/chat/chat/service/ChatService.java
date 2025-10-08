package com.websocket.chat.chat.service;


import com.websocket.chat.chat.dao.ChatRepository;
import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public DomainChat createIfNotExist(String chatName, String creatorName) {
        Optional<DomainChat> chat = chatRepository.findByName(chatName);
        DomainUser creator = userRepository.findByName(creatorName).orElseThrow();
        if (chat.isPresent()) {
            chat.get().getParticipants().add(creator);
            return chatRepository.save(chat.get());
        }

        DomainChat newChat = DomainChat.builder()
                .name(chatName)
                .participants(List.of(creator))
                .build();
        creator.getActiveChats().add(newChat);
        return chatRepository.save(newChat);
    }
}

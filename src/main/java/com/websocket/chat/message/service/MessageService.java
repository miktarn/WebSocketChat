package com.websocket.chat.message.service;

import static java.lang.String.*;

import com.websocket.chat.chat.dao.ChatRepository;
import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.dao.MessageRepository;
import com.websocket.chat.message.domain.DomainMessage;
import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public ChatMessage persist(ChatMessage chatMessage) {
        Optional<DomainUser> user = userRepository.findByName(chatMessage.getSender());
        Optional<DomainChat> chat = chatRepository.findByName(chatMessage.getRoom());
        if (user.isEmpty() || chat.isEmpty()) {
            throw new IllegalArgumentException(format("Can't persist message for user %s and chat %s", user, chat));
        }
        DomainMessage newMessage = map(chatMessage, user.get(), chat.get());
        return map(messageRepository.save(newMessage));
    }

    public List<ChatMessage> getAllByRoomChronologically(String room) {
        return messageRepository.findAllByRoomNameOrderByCreationTime(room).stream()
                .map(MessageService::map)
                .collect(Collectors.toList());
    }

    private static DomainMessage map(ChatMessage chatMessage, DomainUser user, DomainChat chat) {
        return DomainMessage.builder()
                .content(chatMessage.getContent())
                .sender(user)
                .room(chat)
                .creationTime(chatMessage.getCreationTime())
                .type(chatMessage.getType())
                .build();
    }

    private static ChatMessage map(DomainMessage domainMessage) {
        return ChatMessage.builder()
                .content(domainMessage.getContent())
                .sender(domainMessage.getSender().getName())
                .room(domainMessage.getRoom().getName())
                .creationTime(domainMessage.getCreationTime())
                .type(domainMessage.getType())
                .build();
    }

}

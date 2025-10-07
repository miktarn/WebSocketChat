package com.websocket.chat.message.service;

import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.dao.MessageRepository;
import com.websocket.chat.message.domain.DomainMessage;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public ChatMessage persist(ChatMessage chatMessage) {
        DomainMessage newMessage = map(chatMessage);
        return map(messageRepository.save(newMessage));
    }

    public List<ChatMessage> getAllByRoomChronologically(String room) {
        return messageRepository.findAllByRoomOrderByCreationTime(room).stream()
                .map(MessageService::map)
                .collect(Collectors.toList());
    }

    private static DomainMessage map(ChatMessage chatMessage) {
        return DomainMessage.builder()
                .content(chatMessage.getContent())
                .sender(chatMessage.getSender())
                .room(chatMessage.getRoom())
                .creationTime(chatMessage.getCreationTime())
                .type(chatMessage.getType())
                .build();
    }

    private static ChatMessage map(DomainMessage domainMessage) {
        return ChatMessage.builder()
                .content(domainMessage.getContent())
                .sender(domainMessage.getSender())
                .room(domainMessage.getRoom())
                .creationTime(domainMessage.getCreationTime())
                .type(domainMessage.getType())
                .build();
    }

}

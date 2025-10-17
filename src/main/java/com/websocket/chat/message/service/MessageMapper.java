package com.websocket.chat.message.service;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.domain.DomainMessage;
import com.websocket.chat.user.domain.DomainUser;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public DomainMessage toDomainMessage(ChatMessage chatMessage,
                                         DomainUser user,
                                         DomainChat chat) {
        return DomainMessage.builder()
                .content(chatMessage.getContent())
                .sender(user)
                .room(chat)
                .creationTime(chatMessage.getCreationTime())
                .type(chatMessage.getType())
                .build();
    }

    public ChatMessage toChatMessage(DomainMessage domainMessage) {
        return ChatMessage.builder()
                .content(domainMessage.getContent())
                .sender(domainMessage.getSender().getName())
                .room(domainMessage.getRoom().getName())
                .creationTime(domainMessage.getCreationTime())
                .type(domainMessage.getType())
                .build();
    }
}

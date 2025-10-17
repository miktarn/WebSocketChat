package com.websocket.chat.message.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.MessageType;
import com.websocket.chat.message.domain.DomainMessage;
import com.websocket.chat.user.domain.DomainUser;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageMapperTest {

    private MessageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MessageMapper();
    }

    @Test
    void shouldMapChatMessageToDomainMessage() {
        // GIVEN
        var creationTime = Instant.now();
        var chatMessage = ChatMessage.builder()
                .content("Hello world")
                .type(MessageType.CHAT)
                .build();

        var user = DomainUser.builder()
                .id(1L)
                .name("Alice")
                .build();

        var chat = DomainChat.builder()
                .id(10L)
                .name("General")
                .build();

        // WHEN
        DomainMessage result = mapper.toDomainMessage(chatMessage, user, chat);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello world");
        assertThat(result.getSender()).isEqualTo(user);
        assertThat(result.getRoom()).isEqualTo(chat);
        assertThat(result.getType()).isEqualTo(MessageType.CHAT);
    }

    @Test
    void shouldMapDomainMessageToChatMessage() {
        // GIVEN
        var user = DomainUser.builder()
                .id(1L)
                .name("Bob")
                .build();

        var chat = DomainChat.builder()
                .id(20L)
                .name("Random")
                .build();

        var domainMessage = DomainMessage.builder()
                .content("Hey there")
                .sender(user)
                .room(chat)
                .type(MessageType.CHAT)
                .build();

        // WHEN
        ChatMessage result = mapper.toChatMessage(domainMessage);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hey there");
        assertThat(result.getSender()).isEqualTo("Bob");
        assertThat(result.getRoom()).isEqualTo("Random");
        assertThat(result.getType()).isEqualTo(MessageType.CHAT);
    }
}

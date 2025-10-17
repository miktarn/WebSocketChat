package com.websocket.chat.message.service;

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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    @Transactional
    public ChatMessage persist(ChatMessage chatMessage) {

        Optional<DomainUser> user = userRepository.findByName(chatMessage.getSender());
        Optional<DomainChat> chat = chatRepository.findByName(chatMessage.getRoom());
        if (user.isEmpty() || chat.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Can't persist message for user %s and chat %s", user, chat));
        }
        DomainMessage newMessage = mapper.toDomainMessage(chatMessage, user.get(), chat.get());
        return mapper.toChatMessage(messageRepository.save(newMessage));
    }

    public List<ChatMessage> getAllByRoomChronologically(String room) {
        return messageRepository.findAllByRoomNameOrderByCreationTime(room).stream()
                .map(mapper::toChatMessage)
                .collect(Collectors.toList());
    }



}

package com.websocket.chat.chat.service;

import com.websocket.chat.chat.dao.ChatRepository;
import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.user.dao.UserRepository;
import com.websocket.chat.user.domain.DomainUser;
import com.websocket.chat.websocket.client.InviteClient;
import java.util.Collections;
import java.util.HashSet;
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
    private final InviteClient inviteClient;

    public List<DomainChat> getChatsByUserName(String name) {
        Optional<DomainUser> user = userRepository.findByName(name);
        if (user.isPresent()) {
            return chatRepository.findAllByParticipantsContaining(user.get());
        }
        return Collections.emptyList();
    }

    @Transactional
    public DomainChat create(String chatName, Set<String> invitedUsersNames, String creatorName) {
        if (chatRepository.existsByName(chatName)) {
            throw new RuntimeException(String.format("Chat %s already exist", chatName));
        }
        Set<DomainUser> participants = fetchUsers(invitedUsersNames, creatorName);
        DomainChat newChat = DomainChat.builder()
                .name(chatName)
                .participants(participants)
                .build();
        participants.forEach(user -> user.getActiveChats().add(newChat));

        DomainChat savedChat = chatRepository.save(newChat);

        inviteClient.sendInvites(chatName, invitedUsersNames);
        return savedChat;
    }

    private Set<DomainUser> fetchUsers(Set<String> invitedUsers, String chatCreator) {
        Set<String> invitedUsersCopy = new HashSet<>(invitedUsers);
        invitedUsersCopy.add(chatCreator);
        return userRepository.findByNameIn(invitedUsersCopy);
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

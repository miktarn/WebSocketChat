package com.websocket.chat.user.dto;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.user.domain.DomainUser;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserResponseDto toDto(DomainUser user) {
        List<String> chatNames = user.getActiveChats().stream().map(DomainChat::getName).toList();
        return new UserResponseDto(user.getName(), chatNames);
    }
}

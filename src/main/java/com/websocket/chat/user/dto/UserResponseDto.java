package com.websocket.chat.user.dto;

import java.util.List;

public record UserResponseDto(String name, List<String> chatNames) {
}

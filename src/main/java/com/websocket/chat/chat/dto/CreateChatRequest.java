package com.websocket.chat.chat.dto;

public record CreateChatRequest(
        String name, String creatorName
) {
}

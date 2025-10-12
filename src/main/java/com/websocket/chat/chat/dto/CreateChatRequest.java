package com.websocket.chat.chat.dto;

import java.util.Set;

public record CreateChatRequest(String name, Set<String> userNames) { }

package com.websocket.chat.chat.dto;

import java.util.Set;

public record CreateChatRequest(String roomName, Set<String> invitedUsersNames, String creatorName) { }

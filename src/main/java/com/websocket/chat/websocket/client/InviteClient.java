package com.websocket.chat.websocket.client;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InviteClient {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendInvites(String roomName, Collection<String> recipientNames) {
        for (String recipientName : recipientNames) {
            messagingTemplate.convertAndSend("/invites/" + recipientName, roomName);
        }
    }
}

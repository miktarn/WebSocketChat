package com.websocket.chat.message;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatMessage {

    private String content;
    private String sender;
    private String room;
    private LocalDateTime creationTime;
    private MessageType type;
}

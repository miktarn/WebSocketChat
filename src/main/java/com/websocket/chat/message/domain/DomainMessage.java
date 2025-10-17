package com.websocket.chat.message.domain;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.message.MessageType;
import com.websocket.chat.user.domain.DomainUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "message")
public class DomainMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    private DomainUser sender;
    @ManyToOne
    private DomainChat room;
    private LocalDateTime creationTime;
    private MessageType type;
}

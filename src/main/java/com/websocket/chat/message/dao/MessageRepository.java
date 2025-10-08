package com.websocket.chat.message.dao;

import com.websocket.chat.message.domain.DomainMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<DomainMessage, Long> {

    List<DomainMessage> findAllByRoomNameOrderByCreationTime(String room);
}

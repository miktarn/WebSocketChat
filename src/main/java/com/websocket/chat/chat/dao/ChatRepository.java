package com.websocket.chat.chat.dao;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.user.domain.DomainUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<DomainChat, Long> {

    List<DomainChat> findAllByParticipantsContaining(DomainUser user);

    Optional<DomainChat> findByName(String name);
}

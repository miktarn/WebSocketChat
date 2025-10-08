package com.websocket.chat.user.dao;

import com.websocket.chat.user.domain.DomainUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<DomainUser, Long> {

    @EntityGraph(attributePaths = "activeChats")
    Optional<DomainUser> findByName(String name);

}

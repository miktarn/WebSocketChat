package com.websocket.chat.user.dao;

import com.websocket.chat.user.domain.DomainUser;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<DomainUser, Long> {

    Optional<DomainUser> findByName(String name);

    Set<DomainUser> findByNameIn(Set<String> names);

}

package org.kiennguyenfpt.datingapp.repositories;

import org.kiennguyenfpt.datingapp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
     // Lấy tất cả tin nhắn của một match
    List<Message> findByMatch_MatchId(Long matchId);
}

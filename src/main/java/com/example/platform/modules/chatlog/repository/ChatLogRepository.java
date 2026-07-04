package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.model.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Integer> {

    List<ChatLog> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    Page<ChatLog> findBySessionId(String sessionId, Pageable pageable);

    @Query("""
            SELECT c
            FROM ChatLog c
            LEFT JOIN FETCH c.matchedQa
            WHERE c.id = :id
            """)
    Optional<ChatLog> findWithMatchedQaById(@Param("id") Integer id);
}

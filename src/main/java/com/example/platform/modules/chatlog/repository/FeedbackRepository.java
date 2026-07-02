package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    /**
     * A chat log can have more than one feedback row - there's no unique
     * constraint on log_id in the feedback table - so this returns a List,
     * not an Optional/single result.
     */
    List<Feedback> findByLogIdOrderByCreatedAtDesc(Integer logId);
}

package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

}
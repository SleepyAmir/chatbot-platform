package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.entity.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository
extends JpaRepository<Feedback,Long>{

Optional<Feedback>

findByLogId(

Long logId

);

}
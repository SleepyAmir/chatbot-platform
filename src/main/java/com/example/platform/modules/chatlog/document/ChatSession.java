package com.example.platform.modules.chatlog.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatSessions")
public class ChatSession {
    @Id
    private String id;
    private String sessionId;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private List<MessageDoc> messages = new ArrayList<>();

    // --- این فیلدها بات شما را هوشمند می‌کند ---
    private String lastTopic;     // مثلاً "python_course"
    private String lastEntityId;  // مثلاً "course_id_123"
}

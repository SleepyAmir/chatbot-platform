package com.example.platform.config.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "v001-init-chat-sessions", order = "001", author = "lead")
public class V001InitChatSessions {

    private static final String COLLECTION_NAME = "chatSessions";

    @Execution
    public void execution(MongoTemplate mongoTemplate) {

        if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
            mongoTemplate.createCollection(COLLECTION_NAME);
        }

        mongoTemplate.indexOps(COLLECTION_NAME)
                .ensureIndex(new Index()
                        .on("sessionId", Sort.Direction.ASC)
                        .named("idx_chat_sessions_session_id"));

        mongoTemplate.indexOps(COLLECTION_NAME)
                .ensureIndex(new Index()
                        .on("createdAt", Sort.Direction.DESC)
                        .named("idx_chat_sessions_created_at"));
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            mongoTemplate.dropCollection(COLLECTION_NAME);
        }
    }
}

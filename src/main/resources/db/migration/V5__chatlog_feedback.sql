CREATE TABLE chat_logs
(
    id BIGSERIAL PRIMARY KEY,

    session_id VARCHAR(255) NOT NULL,

    user_question TEXT NOT NULL,

    matched_qa BIGINT,

    answer_returned TEXT,

    confidence REAL,

    model_used VARCHAR(100),

    response_time_ms BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feedback
(
    id BIGSERIAL PRIMARY KEY,

    log_id BIGINT NOT NULL,

    rating SMALLINT NOT NULL,

    comment TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_feedback_log
        FOREIGN KEY (log_id)
            REFERENCES chat_logs(id),

    CONSTRAINT chk_rating
        CHECK (rating IN (-1,1))
);


CREATE INDEX idx_chat_logs_created
    ON chat_logs(created_at DESC);

CREATE INDEX idx_feedback_log
    ON feedback(log_id);
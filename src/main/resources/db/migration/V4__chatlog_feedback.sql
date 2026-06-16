-- V4__chatlog_feedback.sql
-- Chat logs, feedback and reporting views.

CREATE TABLE IF NOT EXISTS chat_logs (
                                         id                SERIAL PRIMARY KEY,
                                         session_id        TEXT NOT NULL,
                                         user_question     TEXT NOT NULL,
                                         matched_qa_id     INT,
                                         answer_returned   TEXT NOT NULL,
                                         confidence        REAL,
                                         model_used        TEXT,
                                         response_time_ms  INT,
                                         created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_chat_logs_matched_qa
    FOREIGN KEY (matched_qa_id) REFERENCES qa_pairs(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,

    CONSTRAINT chk_chat_logs_confidence_range
    CHECK (confidence IS NULL OR (confidence >= 0 AND confidence <= 1)),

    CONSTRAINT chk_chat_logs_response_time_non_negative
    CHECK (response_time_ms IS NULL OR response_time_ms >= 0)
    );

CREATE TABLE IF NOT EXISTS feedback (
                                        id          SERIAL PRIMARY KEY,
                                        log_id      INT NOT NULL,
                                        rating      SMALLINT NOT NULL,
                                        comment     TEXT,
                                        created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_feedback_log
    FOREIGN KEY (log_id) REFERENCES chat_logs(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

    CONSTRAINT chk_feedback_rating CHECK (rating IN (-1, 1))
    );

CREATE INDEX IF NOT EXISTS idx_chat_logs_created
    ON chat_logs (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_chat_logs_session_id
    ON chat_logs (session_id);

CREATE INDEX IF NOT EXISTS idx_chat_logs_matched_qa
    ON chat_logs (matched_qa_id);

CREATE INDEX IF NOT EXISTS idx_feedback_log
    ON feedback (log_id);

CREATE INDEX IF NOT EXISTS idx_feedback_created
    ON feedback (created_at DESC);

CREATE OR REPLACE VIEW v_chat_report AS
SELECT
    cl.id AS log_id,
    cl.session_id,
    cl.user_question,
    cl.answer_returned,
    cl.confidence,
    cl.model_used,
    cl.response_time_ms,
    cl.created_at,
    q.id AS matched_qa_id,
    q.question AS matched_question,
    q.course_id,
    c.name AS course_name,
    COALESCE(SUM(CASE WHEN f.rating = 1 THEN 1 ELSE 0 END), 0)::INT AS positive_feedback_count,
    COALESCE(SUM(CASE WHEN f.rating = -1 THEN 1 ELSE 0 END), 0)::INT AS negative_feedback_count,
    COUNT(f.id)::INT AS total_feedback_count
FROM chat_logs cl
         LEFT JOIN qa_pairs q ON q.id = cl.matched_qa_id
         LEFT JOIN courses c ON c.id = q.course_id
         LEFT JOIN feedback f ON f.log_id = cl.id
GROUP BY
    cl.id,
    cl.session_id,
    cl.user_question,
    cl.answer_returned,
    cl.confidence,
    cl.model_used,
    cl.response_time_ms,
    cl.created_at,
    q.id,
    q.question,
    q.course_id,
    c.name;

CREATE OR REPLACE VIEW v_feedback_summary AS
SELECT
    cl.id AS log_id,
    cl.session_id,
    cl.user_question,
    COUNT(f.id)::INT AS total_feedback_count,
    COALESCE(SUM(CASE WHEN f.rating = 1 THEN 1 ELSE 0 END), 0)::INT AS positive_count,
    COALESCE(SUM(CASE WHEN f.rating = -1 THEN 1 ELSE 0 END), 0)::INT AS negative_count,
    CASE
        WHEN COUNT(f.id) = 0 THEN NULL
        ELSE AVG(f.rating)::DOUBLE PRECISION
END AS average_rating,
    MAX(f.created_at) AS last_feedback_at
FROM chat_logs cl
LEFT JOIN feedback f ON f.log_id = cl.id
GROUP BY cl.id, cl.session_id, cl.user_question;

CREATE OR REPLACE VIEW v_stats AS
SELECT
    (SELECT COUNT(*)::BIGINT FROM courses) AS total_courses,
    (SELECT COUNT(*)::BIGINT FROM course_details) AS total_course_details,
    (SELECT COUNT(*)::BIGINT FROM qa_pairs) AS total_qa_pairs,
    (SELECT COUNT(*)::BIGINT FROM intents) AS total_intents,
    (SELECT COUNT(*)::BIGINT FROM careers) AS total_careers,
    (SELECT COUNT(*)::BIGINT FROM chat_logs) AS total_chat_logs,
    (SELECT COUNT(*)::BIGINT FROM feedback) AS total_feedback,
    (SELECT COUNT(*)::BIGINT FROM feedback WHERE rating = 1) AS positive_feedback,
    (SELECT COUNT(*)::BIGINT FROM feedback WHERE rating = -1) AS negative_feedback,
    (SELECT AVG(confidence)::DOUBLE PRECISION FROM chat_logs WHERE confidence IS NOT NULL) AS average_confidence,
                                                                 (SELECT AVG(response_time_ms)::DOUBLE PRECISION FROM chat_logs WHERE response_time_ms IS NOT NULL) AS average_response_time_ms,
    NOW() AS generated_at;
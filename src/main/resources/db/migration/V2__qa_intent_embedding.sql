-- V2__qa_intent_embedding.sql
-- QA pairs + intents + pgvector embeddings and semantic QA search.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS qa_pairs (
                                        id          SERIAL PRIMARY KEY,
                                        question    TEXT NOT NULL,
                                        answer      TEXT NOT NULL,
                                        course_id   INT,
                                        created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_qa_pairs_question UNIQUE (question),
    CONSTRAINT fk_qa_pairs_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS intents (
                                       id          SERIAL PRIMARY KEY,
                                       name        TEXT NOT NULL,
                                       created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_intents_name UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS qa_intents (
                                          qa_id      INT NOT NULL,
                                          intent_id  INT NOT NULL,

                                          CONSTRAINT pk_qa_intents PRIMARY KEY (qa_id, intent_id),
    CONSTRAINT fk_qa_intents_qa
    FOREIGN KEY (qa_id) REFERENCES qa_pairs(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT fk_qa_intents_intent
    FOREIGN KEY (intent_id) REFERENCES intents(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS qa_embeddings (
                                             id          SERIAL PRIMARY KEY,
                                             qa_id       INT NOT NULL,
                                             embedding   vector(384) NOT NULL,
    model_name  TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_qa_embeddings_qa UNIQUE (qa_id),
    CONSTRAINT fk_qa_embeddings_qa
    FOREIGN KEY (qa_id) REFERENCES qa_pairs(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_qa_pairs_course_id
    ON qa_pairs (course_id);

CREATE INDEX IF NOT EXISTS idx_qa_pairs_created_at
    ON qa_pairs (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_intents_name
    ON intents (name);

CREATE INDEX IF NOT EXISTS idx_qa_intents_intent_id
    ON qa_intents (intent_id);

CREATE INDEX IF NOT EXISTS idx_qa_embeddings_qa_id
    ON qa_embeddings (qa_id);

-- Cosine ANN index for pgvector. For small datasets PostgreSQL may still use sequential scan.
CREATE INDEX IF NOT EXISTS idx_qa_embeddings_embedding_ivfflat
    ON qa_embeddings
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- Semantic QA search.
-- similarity = 1 - cosine_distance
CREATE OR REPLACE FUNCTION search_qa(
    query_embedding vector(384),
    top_k INT DEFAULT 5,
    min_similarity DOUBLE PRECISION DEFAULT 0.0
)
RETURNS TABLE (
    qa_id INT,
    question TEXT,
    answer TEXT,
    course_id INT,
    similarity DOUBLE PRECISION,
    model_name TEXT,
    created_at TIMESTAMPTZ
)
LANGUAGE sql
STABLE
AS $$
SELECT
    q.id AS qa_id,
    q.question,
    q.answer,
    q.course_id,
    (1.0 - (e.embedding <=> query_embedding))::DOUBLE PRECISION AS similarity,
        e.model_name,
        q.created_at
FROM qa_embeddings e
    JOIN qa_pairs q ON q.id = e.qa_id
WHERE (1.0 - (e.embedding <=> query_embedding)) >= min_similarity
ORDER BY e.embedding <=> query_embedding
    LIMIT GREATEST(top_k, 0);
$$;
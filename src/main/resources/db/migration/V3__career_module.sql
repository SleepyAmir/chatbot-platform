-- V3__career_module.sql
-- Career and job-market module.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS careers (
                                       id           SERIAL PRIMARY KEY,
                                       title        TEXT NOT NULL,
                                       description  TEXT,
                                       source_url   TEXT,
                                       created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_careers_title UNIQUE (title)
    );

CREATE TABLE IF NOT EXISTS career_skills (
                                             id          SERIAL PRIMARY KEY,
                                             career_id   INT NOT NULL,
                                             skill_name  TEXT NOT NULL,

                                             CONSTRAINT fk_career_skills_career
                                             FOREIGN KEY (career_id) REFERENCES careers(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

    CONSTRAINT uk_career_skills_career_skill UNIQUE (career_id, skill_name)
    );

CREATE TABLE IF NOT EXISTS career_requirements (
                                                   id                SERIAL PRIMARY KEY,
                                                   career_id         INT NOT NULL,
                                                   chunk_index       INT NOT NULL,
                                                   requirement_text  TEXT NOT NULL,
                                                   embedding         vector(384),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_career_requirements_career
    FOREIGN KEY (career_id) REFERENCES careers(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

    CONSTRAINT uk_career_requirements_career_chunk UNIQUE (career_id, chunk_index),
    CONSTRAINT chk_career_requirements_chunk_non_negative CHECK (chunk_index >= 0)
    );

CREATE TABLE IF NOT EXISTS course_careers (
                                              course_id  INT NOT NULL,
                                              career_id  INT NOT NULL,
                                              relevance  REAL NOT NULL DEFAULT 0,

                                              CONSTRAINT pk_course_careers PRIMARY KEY (course_id, career_id),
    CONSTRAINT fk_course_careers_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT fk_course_careers_career
    FOREIGN KEY (career_id) REFERENCES careers(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

    CONSTRAINT chk_course_careers_relevance_range CHECK (relevance >= 0 AND relevance <= 1)
    );

CREATE INDEX IF NOT EXISTS idx_careers_created_at
    ON careers (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_career_skills_name
    ON career_skills (skill_name);

CREATE INDEX IF NOT EXISTS idx_career_skills_career_id
    ON career_skills (career_id);

CREATE INDEX IF NOT EXISTS idx_career_requirements_career_chunk
    ON career_requirements (career_id, chunk_index);

CREATE INDEX IF NOT EXISTS idx_career_requirements_embedding_ivfflat
    ON career_requirements
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100)
    WHERE embedding IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_course_careers_course
    ON course_careers (course_id);

CREATE INDEX IF NOT EXISTS idx_course_careers_career
    ON course_careers (career_id);

CREATE INDEX IF NOT EXISTS idx_course_careers_relevance
    ON course_careers (relevance DESC);

-- Semantic search inside career requirement chunks.
CREATE OR REPLACE FUNCTION search_career_requirements(
    query_embedding vector(384),
    top_k INT DEFAULT 5,
    min_similarity DOUBLE PRECISION DEFAULT 0.0
)
RETURNS TABLE (
    requirement_id INT,
    career_id INT,
    career_title TEXT,
    chunk_index INT,
    requirement_text TEXT,
    similarity DOUBLE PRECISION,
    created_at TIMESTAMPTZ
)
LANGUAGE sql
STABLE
AS $$
SELECT
    cr.id AS requirement_id,
    cr.career_id,
    c.title AS career_title,
    cr.chunk_index,
    cr.requirement_text,
    (1.0 - (cr.embedding <=> query_embedding))::DOUBLE PRECISION AS similarity,
        cr.created_at
FROM career_requirements cr
    JOIN careers c ON c.id = cr.career_id
WHERE cr.embedding IS NOT NULL
  AND (1.0 - (cr.embedding <=> query_embedding)) >= min_similarity
ORDER BY cr.embedding <=> query_embedding
    LIMIT GREATEST(top_k, 0);
$$;
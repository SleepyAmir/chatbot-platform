-- V1__init_courses.sql
-- Base schema: courses + course_details
-- Designed for scraped/course data where many values are displayed as text/placeholders.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS courses (
                                       id          SERIAL PRIMARY KEY,
                                       name        TEXT NOT NULL,
                                       lesson_url  TEXT,
                                       created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_courses_name UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS course_details (
                                              id            SERIAL PRIMARY KEY,
                                              course_id     INT NOT NULL,
                                              price         TEXT,
                                              teacher       TEXT,
                                              duration      TEXT,
                                              branch        TEXT,
                                              link          TEXT,
                                              department    TEXT,
                                              prerequisite  TEXT,
                                              syllabus      TEXT,
                                              start_time    TEXT,
                                              course_code   TEXT,
                                              updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_course_details_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

    CONSTRAINT uk_course_details_course UNIQUE (course_id)
    );

CREATE INDEX IF NOT EXISTS idx_courses_created_at
    ON courses (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_course_details_course_id
    ON course_details (course_id);

-- Reusable helper for automatic updated_at updates.
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_course_details_updated_at ON course_details;

CREATE TRIGGER trg_course_details_updated_at
    BEFORE UPDATE ON course_details
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
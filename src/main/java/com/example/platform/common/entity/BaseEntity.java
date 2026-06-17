package com.example.platform.common.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for entities that have a PostgreSQL SERIAL/IDENTITY primary key named "id".
 *
 * Important:
 * - This class only contains "id".
 * - It does NOT contain created_at or updated_at because not all tables have those columns.
 * - Entities with composite primary keys, such as qa_intents and course_careers,
 *   must NOT extend this class.
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Equality is based on:
     * 1. Same Hibernate entity class
     * 2. Non-null database id
     *
     * This prevents different entity types with the same id from being considered equal.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * Constant per entity class.
     * This is a common safe pattern for JPA entities with generated ids.
     */
    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
package com.example.platform.modules.course.dto;

import java.time.OffsetDateTime;

public record CourseResponse(
        Integer id,
        String name,
        String lessonUrl,
        OffsetDateTime createdAt
) {
}
package com.example.platform.modules.course.dto;

import java.time.OffsetDateTime;

public record CourseDetailResponse(
        Integer id,
        Integer courseId,
        String courseName,
        String price,
        String teacher,
        String duration,
        String branch,
        String link,
        String department,
        String prerequisite,
        String syllabus,
        String startTime,
        String courseCode,
        OffsetDateTime updatedAt
) {
}
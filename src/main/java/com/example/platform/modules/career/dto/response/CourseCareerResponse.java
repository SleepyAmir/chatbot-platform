package com.example.platform.modules.career.dto.response;

public record CourseCareerResponse(
        Integer courseId,
        String courseName,
        Integer careerId,
        String careerTitle,
        Float relevance
) {
}

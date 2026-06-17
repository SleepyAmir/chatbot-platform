package com.example.platform.modules.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(

        @NotBlank(message = "Course name is required")
        String name,

        String lessonUrl
) {
}
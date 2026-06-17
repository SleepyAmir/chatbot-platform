package com.example.platform.modules.course.dto;

public record CourseDetailRequest(
        String price,
        String teacher,
        String duration,
        String branch,
        String link,
        String department,
        String prerequisite,
        String syllabus,
        String startTime,
        String courseCode
) {
}
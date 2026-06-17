package com.example.platform.modules.course.mapper;

import com.example.platform.modules.course.dto.request.CourseRequest;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.model.Course;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class CourseMapper {

    public Course toEntity(CourseRequest request) {
        if (request == null) {
            return null;
        }

        return new Course(
                request.name(),
                request.lessonUrl()
        );
    }

    public abstract CourseResponse toResponse(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromRequest(
            CourseRequest request,
            @MappingTarget Course course
    );
}
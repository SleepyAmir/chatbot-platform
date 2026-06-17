package com.example.platform.modules.course.mapper;

import com.example.platform.modules.course.dto.CourseRequest;
import com.example.platform.modules.course.dto.CourseResponse;
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

    /**
     * NOTE on semantics: this is a PARTIAL update, not a full PUT replacement.
     * Because of NullValuePropertyMappingStrategy.IGNORE on this @Mapper, a field
     * left null in the request (e.g. lessonUrl) is left untouched on the entity
     * instead of being cleared. This is intentional for this project (course data
     * mostly arrives incrementally from a scraper), but it means PUT /api/courses/{id}
     * behaves like PATCH. Document this in the API docs so consumers aren't surprised
     * that sending null does not clear a field.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromRequest(
            CourseRequest request,
            @MappingTarget Course course
    );
}
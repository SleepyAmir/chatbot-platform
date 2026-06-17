package com.example.platform.modules.course.mapper;

import com.example.platform.modules.course.dto.request.CourseDetailRequest;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import com.example.platform.modules.course.model.Course;
import com.example.platform.modules.course.model.CourseDetail;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class CourseDetailMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    public abstract CourseDetailResponse toResponse(CourseDetail detail);

    public CourseDetail toEntity(CourseDetailRequest request, Course course) {
        if (course == null) {
            return null;
        }

        CourseDetail detail = new CourseDetail(course);
        updateEntityFromRequest(request, detail);

        return detail;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    public abstract void updateEntityFromRequest(
            CourseDetailRequest request,
            @MappingTarget CourseDetail detail
    );
}
package com.example.platform.modules.course.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.common.web.PageableUtils;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.service.CourseDetailService;
import com.example.platform.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    /**
     * Only these Course properties may be used in ?sort=. Anything else
     * (e.g. Swagger's default placeholder "string", or a typo) is silently
     * dropped instead of causing a 500 — see PageableUtils for why.
     */
    private static final Set<String> COURSE_SORTABLE_PROPERTIES = Set.of("id", "name", "lessonUrl");

    /**
     * CourseDetail is fetched via the "course" relation for some sorts
     * (e.g. course name), plus its own direct text columns.
     */
    private static final Set<String> COURSE_DETAIL_SORTABLE_PROPERTIES = Set.of(
            "id", "price", "teacher", "duration", "branch",
            "department", "prerequisite", "syllabus", "startTime", "courseCode"
    );

    private final CourseService courseService;
    private final CourseDetailService courseDetailService;

    @GetMapping("/all")
    public ApiResponse<List<CourseResponse>> getAllCoursesWithoutPagination() {
        return ApiResponse.ok(courseService.getAllCourses());
    }

    @GetMapping
    public ApiResponse<Page<CourseResponse>> getCourses(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Pageable safePageable = PageableUtils.sanitizeSort(pageable, COURSE_SORTABLE_PROPERTIES);

        Page<CourseResponse> result = (keyword == null || keyword.isBlank())
                ? courseService.getAllCourses(safePageable)
                : courseService.searchCourses(keyword, safePageable);

        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable Integer id) {
        return ApiResponse.ok(courseService.getCourseById(id));
    }

    @GetMapping("/by-name")
    public ApiResponse<CourseResponse> getCourseByName(@RequestParam String name) {
        return ApiResponse.ok(courseService.getCourseByName(name));
    }

    @GetMapping("/{id}/details")
    public ApiResponse<CourseDetailResponse> getCourseDetails(@PathVariable Integer id) {
        return ApiResponse.ok(courseDetailService.getDetailByCourseId(id));
    }

    @GetMapping("/details/search")
    public ApiResponse<Page<CourseDetailResponse>> searchCourseDetails(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Pageable safePageable = PageableUtils.sanitizeSort(pageable, COURSE_DETAIL_SORTABLE_PROPERTIES);
        return ApiResponse.ok(courseDetailService.searchDetails(keyword, safePageable));
    }

    // ----------------------------------------------------------------
    // Phase 1: create/update/delete are not
    // exposed this phase for Course/CourseDetail
    // ----------------------------------------------------------------

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
    //     return ApiResponse.ok("Course created successfully", courseService.createCourse(request));
    // }

    // @PutMapping("/{id}")
    // public ApiResponse<CourseResponse> updateCourse(
    //         @PathVariable Integer id,
    //         @Valid @RequestBody CourseRequest request
    // ) {
    //     return ApiResponse.ok("Course updated successfully", courseService.updateCourse(id, request));
    // }

    // @DeleteMapping("/{id}")
    // public ApiResponse<Void> deleteCourse(@PathVariable Integer id) {
    //     courseService.deleteCourse(id);
    //     return ApiResponse.ok("Course deleted successfully");
    // }

    // @PostMapping("/{id}/details")
    // @ResponseStatus(HttpStatus.CREATED)
    // public ApiResponse<CourseDetailResponse> createCourseDetails(
    //         @PathVariable Integer id,
    //         @RequestBody CourseDetailRequest request
    // ) {
    //     return ApiResponse.ok(
    //             "Course detail created successfully",
    //             courseDetailService.createDetail(id, request)
    //     );
    // }

    // @PutMapping("/{id}/details")
    // public ApiResponse<CourseDetailResponse> upsertCourseDetails(
    //         @PathVariable Integer id,
    //         @RequestBody CourseDetailRequest request
    // ) {
    //     return ApiResponse.ok(
    //             "Course detail saved successfully",
    //             courseDetailService.upsertDetail(id, request)
    //     );
    // }

    // @DeleteMapping("/{id}/details")
    // public ApiResponse<Void> deleteCourseDetails(@PathVariable Integer id) {
    //     courseDetailService.deleteDetailByCourseId(id);
    //     return ApiResponse.ok("Course detail deleted successfully");
    // }
}
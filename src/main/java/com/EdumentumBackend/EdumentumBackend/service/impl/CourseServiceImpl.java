package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseDetailResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseUpdateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.EnrollmentResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ExerciseCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ExerciseResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.LessonCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.LessonResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.RatingCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.RatingResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ResourceCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ResourceResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.TagCourseResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.TeacherSummaryResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.EnrollmentEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.ExerciseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.LessonEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.RatingEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.ResourceEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.TagCourseEntity;
import com.EdumentumBackend.EdumentumBackend.enums.*;
import com.EdumentumBackend.EdumentumBackend.exception.*;
import com.EdumentumBackend.EdumentumBackend.repository.*;
import com.EdumentumBackend.EdumentumBackend.service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TagCourseRepository TagCourseRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final ResourceRepository resourceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RatingRepository ratingRepository;

    public CourseServiceImpl(CourseRepository courseRepository,
                           UserRepository userRepository,
                           TagCourseRepository TagCourseRepository,
                           LessonRepository lessonRepository,
                           ExerciseRepository exerciseRepository,
                           ResourceRepository resourceRepository,
                           EnrollmentRepository enrollmentRepository,
                           RatingRepository ratingRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.TagCourseRepository = TagCourseRepository;
        this.lessonRepository = lessonRepository;
        this.exerciseRepository = exerciseRepository;
        this.resourceRepository = resourceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.ratingRepository = ratingRepository;
    }

    // Course Management
    @Override
    @Transactional
    public CourseResponseDto createCourse(CourseCreateRequestDto request, Long teacherId) {
        UserEntity teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + teacherId));

        // Handle tags
        Set<TagCourseEntity> tags = processTags(request.getTagNames());

        CourseEntity course = CourseEntity.builder()
                .title(request.getTitle())
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .courseLevel(request.getCourseLevel())
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .teacher(teacher)
                .tags(tags)
                .build();

        CourseEntity savedCourse = courseRepository.save(course);
        return convertToCourseResponse(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourse(Long courseId, CourseUpdateRequestDto request, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            course.setTitle(request.getTitle());
        }
        if (request.getShortDescription() != null) {
            course.setShortDescription(request.getShortDescription());
        }
        if (request.getFullDescription() != null) {
            course.setFullDescription(request.getFullDescription());
        }
        if (request.getCourseLevel() != null) {
            course.setCourseLevel(request.getCourseLevel());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Price cannot be negative");
            }
            course.setPrice(request.getPrice());
        }
        if (request.getTagNames() != null) {
            course.setTags(processTags(request.getTagNames()));
        }

        CourseEntity updatedCourse = courseRepository.save(course);
        return convertToCourseResponse(updatedCourse);
    }

    @Override
    public CourseDetailResponseDto getCourseById(Long courseId, Long userId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Check access permissions
        boolean isTeacher = course.getTeacher().getUserId().equals(userId);
        boolean isPublished = course.getStatus() == CourseStatus.PUBLISHED;
        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(user, course);

        if (!isTeacher && !isPublished && !isEnrolled) {
            throw new BadRequestException("Access denied: You don't have permission to view this course");
        }

        CourseDetailResponseDto response = CourseDetailResponseDto.builder()
                .course(convertToCourseResponse(course))
                .lessons(convertToLessonResponses(lessonRepository.findByCourseOrderByOrderIndex(course)))
                .exercises(convertToExerciseResponses(exerciseRepository.findByCourseOrderByOrderIndex(course)))
                .resources(convertToResourceResponses(resourceRepository.findByCourseOrderByOrderIndex(course)))
                .isEnrolled(isEnrolled)
                .build();

        if (isEnrolled) {
            EnrollmentEntity enrollment = enrollmentRepository.findByStudentAndCourse(user, course).orElse(null);
            if (enrollment != null) {
                response.setEnrollmentStatus(enrollment.getStatus());
                response.setProgressPercentage(enrollment.getProgressPercentage());
            }
        }

        RatingEntity userRating = ratingRepository.findByStudentAndCourse(user, course).orElse(null);
        if (userRating != null) {
            response.setUserRating(convertToRatingResponse(userRating));
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        courseRepository.delete(course);
    }

    @Override
    @Transactional
    public CourseResponseDto publishCourse(Long courseId, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new BadRequestException("Course is already published");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        CourseEntity updatedCourse = courseRepository.save(course);
        return convertToCourseResponse(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDto archiveCourse(Long courseId, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        course.setStatus(CourseStatus.ARCHIVED);
        CourseEntity updatedCourse = courseRepository.save(course);
        return convertToCourseResponse(updatedCourse);
    }

    // Course Queries
    @Override
    public Page<CourseResponseDto> getTeacherCourses(Long teacherId, CourseStatus status, Pageable pageable) {
        UserEntity teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + teacherId));
        
        Page<CourseEntity> courses = courseRepository.findByTeacherAndStatus(teacher, status, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> getPublishedCourses(Pageable pageable) {
        Page<CourseEntity> courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> searchCourses(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Search keyword is required");
        }
        Page<CourseEntity> courses = courseRepository.searchByKeyword(keyword, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> filterCourses(CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<CourseEntity> courses = courseRepository.findByFilters(level, minPrice, maxPrice, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> getCoursesByTags(List<String> tagNames, Pageable pageable) {
        if (tagNames == null || tagNames.isEmpty()) {
            throw new BadRequestException("Tag names are required");
        }
        Page<CourseEntity> courses = courseRepository.findByTagNames(tagNames, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> getPopularCourses(Pageable pageable) {
        Page<CourseEntity> courses = courseRepository.findPopularCourses(pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> getHighlyRatedCourses(Pageable pageable) {
        Page<CourseEntity> courses = courseRepository.findHighlyRatedCourses(pageable);
        return courses.map(this::convertToCourseResponse);
    }

    @Override
    public Page<CourseResponseDto> getFreeCourses(Pageable pageable) {
        Page<CourseEntity> courses = courseRepository.findByStatusAndPrice(CourseStatus.PUBLISHED, BigDecimal.ZERO, pageable);
        return courses.map(this::convertToCourseResponse);
    }

    // Enrollment Management
    @Override
    @Transactional
    public EnrollmentResponseDto enrollInCourse(Long courseId, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new CourseEnrollmentException("Cannot enroll in unpublished course");
        }

        if (course.getTeacher().getUserId().equals(studentId)) {
            throw new CourseEnrollmentException("Cannot enroll in your own course");
        }

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new AlreadyExistsException("Student is already enrolled in this course");
        }

        EnrollmentEntity enrollment = EnrollmentEntity.builder()
                .student(student)
                .course(course)
                .paidAmount(course.getPrice())
                .build();

        EnrollmentEntity savedEnrollment = enrollmentRepository.save(enrollment);
        
        // Update course enrollment count
        course.setTotalEnrollments(course.getTotalEnrollments() + 1);
        courseRepository.save(course);

        return convertToEnrollmentResponse(savedEnrollment);
    }

    @Override
    @Transactional
    public void unenrollFromCourse(Long courseId, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        EnrollmentEntity enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        
        // Update course enrollment count
        course.setTotalEnrollments(Math.max(0, course.getTotalEnrollments() - 1));
        courseRepository.save(course);
    }

    @Override
    public Page<EnrollmentResponseDto> getStudentEnrollments(Long studentId, EnrollmentStatus status, Pageable pageable) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        Page<EnrollmentEntity> enrollments = enrollmentRepository.findByStudent(student, pageable);
        return enrollments.map(this::convertToEnrollmentResponse);
    }

    @Override
    public Page<EnrollmentResponseDto> getCourseEnrollments(Long courseId, Long teacherId, Pageable pageable) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        Page<EnrollmentEntity> enrollments = enrollmentRepository.findByCourse(course, pageable);
        return enrollments.map(this::convertToEnrollmentResponse);
    }

    @Override
    @Transactional
    public EnrollmentResponseDto updateEnrollmentProgress(Long enrollmentId, Integer completedLessons, Integer completedExercises) {
        EnrollmentEntity enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found with id: " + enrollmentId));

        if (completedLessons != null) {
            enrollment.setCompletedLessons(completedLessons);
        }
        if (completedExercises != null) {
            enrollment.setCompletedExercises(completedExercises);
        }

        // Calculate progress percentage
        Long totalLessons = lessonRepository.countByCourseId(enrollment.getCourse().getCourseId());
        Long totalExercises = exerciseRepository.countByCourseId(enrollment.getCourse().getCourseId());
        
        if (totalLessons + totalExercises > 0) {
            BigDecimal progress = BigDecimal.valueOf(
                    (enrollment.getCompletedLessons() + enrollment.getCompletedExercises()) * 100.0 
                    / (totalLessons + totalExercises)
            ).setScale(2, RoundingMode.HALF_UP);
            enrollment.setProgressPercentage(progress);
        }

        EnrollmentEntity updatedEnrollment = enrollmentRepository.save(enrollment);
        return convertToEnrollmentResponse(updatedEnrollment);
    }

    // Rating Management
    @Override
    @Transactional
    public RatingResponseDto rateCourse(Long courseId, RatingCreateRequestDto request, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        if (!enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new BadRequestException("You must be enrolled in the course to rate it");
        }

        if (ratingRepository.findByStudentAndCourse(student, course).isPresent()) {
            throw new AlreadyExistsException("You have already rated this course");
        }

        RatingEntity rating = RatingEntity.builder()
                .student(student)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        RatingEntity savedRating = ratingRepository.save(rating);
        
        // Update course average rating
        updateCourseAverageRating(course);
        
        return convertToRatingResponse(savedRating);
    }

    @Override
    @Transactional
    public RatingResponseDto updateCourseRating(Long courseId, RatingCreateRequestDto request, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        RatingEntity rating = ratingRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new NotFoundException("Rating not found for this course"));

        rating.setRating(request.getRating());
        rating.setComment(request.getComment());

        RatingEntity updatedRating = ratingRepository.save(rating);
        
        // Update course average rating
        updateCourseAverageRating(course);
        
        return convertToRatingResponse(updatedRating);
    }

    @Override
    @Transactional
    public void deleteCourseRating(Long courseId, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        RatingEntity rating = ratingRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new NotFoundException("Rating not found for this course"));

        ratingRepository.delete(rating);
        
        // Update course average rating
        updateCourseAverageRating(course);
    }

    @Override
    public Page<RatingResponseDto> getCourseRatings(Long courseId, Pageable pageable) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        Page<RatingEntity> ratings = ratingRepository.findByCourse(course, pageable);
        return ratings.map(this::convertToRatingResponse);
    }

    @Override
    public RatingResponseDto getUserCourseRating(Long courseId, Long studentId) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        RatingEntity rating = ratingRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new NotFoundException("Rating not found for this course"));

        return convertToRatingResponse(rating);
    }

    // Lesson Management (basic implementation - can be expanded)
    @Override
    @Transactional
    public LessonResponseDto createLesson(Long courseId, LessonCreateRequestDto request, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        
        LessonEntity lesson = LessonEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .videoUrl(request.getVideoUrl())
                .durationMinutes(request.getDurationMinutes())
                .course(course)
                .build();

        LessonEntity savedLesson = lessonRepository.save(lesson);
        return convertToLessonResponse(savedLesson);
    }

    @Override
    @Transactional
    public LessonResponseDto updateLesson(Long lessonId, LessonCreateRequestDto request, Long teacherId) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + lessonId));

        if (!lesson.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only update lessons from your own courses");
        }

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getContent() != null) lesson.setContent(request.getContent());
        if (request.getOrderIndex() != null) lesson.setOrderIndex(request.getOrderIndex());
        if (request.getVideoUrl() != null) lesson.setVideoUrl(request.getVideoUrl());
        if (request.getDurationMinutes() != null) lesson.setDurationMinutes(request.getDurationMinutes());

        LessonEntity updatedLesson = lessonRepository.save(lesson);
        return convertToLessonResponse(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId, Long teacherId) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + lessonId));

        if (!lesson.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only delete lessons from your own courses");
        }

        lessonRepository.delete(lesson);
    }

    @Override
    public List<LessonResponseDto> getCourseLessons(Long courseId, Long userId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Check access
        boolean canAccess = course.getTeacher().getUserId().equals(userId) ||
                           course.getStatus() == CourseStatus.PUBLISHED ||
                           enrollmentRepository.existsByStudentAndCourse(user, course);

        if (!canAccess) {
            throw new BadRequestException("Access denied");
        }

        List<LessonEntity> lessons = lessonRepository.findByCourseOrderByOrderIndex(course);
        return convertToLessonResponses(lessons);
    }

    // Exercise Management (similar pattern)
    @Override
    @Transactional
    public ExerciseResponseDto createExercise(Long courseId, ExerciseCreateRequestDto request, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        
        ExerciseEntity exercise = ExerciseEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .course(course)
                .build();

        ExerciseEntity savedExercise = exerciseRepository.save(exercise);
        return convertToExerciseResponse(savedExercise);
    }

    @Override
    @Transactional
    public ExerciseResponseDto updateExercise(Long exerciseId, ExerciseCreateRequestDto request, Long teacherId) {
        ExerciseEntity exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found with id: " + exerciseId));

        if (!exercise.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only update exercises from your own courses");
        }

        if (request.getTitle() != null) exercise.setTitle(request.getTitle());
        if (request.getDescription() != null) exercise.setDescription(request.getDescription());
        if (request.getInstructions() != null) exercise.setInstructions(request.getInstructions());
        if (request.getOrderIndex() != null) exercise.setOrderIndex(request.getOrderIndex());

        ExerciseEntity updatedExercise = exerciseRepository.save(exercise);
        return convertToExerciseResponse(updatedExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId, Long teacherId) {
        ExerciseEntity exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found with id: " + exerciseId));

        if (!exercise.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only delete exercises from your own courses");
        }

        exerciseRepository.delete(exercise);
    }

    @Override
    public List<ExerciseResponseDto> getCourseExercises(Long courseId, Long userId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Check access
        boolean canAccess = course.getTeacher().getUserId().equals(userId) ||
                           course.getStatus() == CourseStatus.PUBLISHED ||
                           enrollmentRepository.existsByStudentAndCourse(user, course);

        if (!canAccess) {
            throw new BadRequestException("Access denied");
        }

        List<ExerciseEntity> exercises = exerciseRepository.findByCourseOrderByOrderIndex(course);
        return convertToExerciseResponses(exercises);
    }

    // Resource Management (similar pattern)
    @Override
    @Transactional
    public ResourceResponseDto createResource(Long courseId, ResourceCreateRequestDto request, Long teacherId) {
        CourseEntity course = getCourseByIdAndTeacher(courseId, teacherId);
        
        ResourceEntity resource = ResourceEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .resourceType(request.getResourceType())
                .url(request.getUrl())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .course(course)
                .build();

        ResourceEntity savedResource = resourceRepository.save(resource);
        return convertToResourceResponse(savedResource);
    }

    @Override
    @Transactional
    public ResourceResponseDto updateResource(Long resourceId, ResourceCreateRequestDto request, Long teacherId) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found with id: " + resourceId));

        if (!resource.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only update resources from your own courses");
        }

        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getDescription() != null) resource.setDescription(request.getDescription());
        if (request.getResourceType() != null) resource.setResourceType(request.getResourceType());
        if (request.getUrl() != null) resource.setUrl(request.getUrl());
        if (request.getOrderIndex() != null) resource.setOrderIndex(request.getOrderIndex());

        ResourceEntity updatedResource = resourceRepository.save(resource);
        return convertToResourceResponse(updatedResource);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId, Long teacherId) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found with id: " + resourceId));

        if (!resource.getCourse().getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only delete resources from your own courses");
        }

        resourceRepository.delete(resource);
    }

    @Override
    public List<ResourceResponseDto> getCourseResources(Long courseId, Long userId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Check access
        boolean canAccess = course.getTeacher().getUserId().equals(userId) ||
                           course.getStatus() == CourseStatus.PUBLISHED ||
                           enrollmentRepository.existsByStudentAndCourse(user, course);

        if (!canAccess) {
            throw new BadRequestException("Access denied");
        }

        List<ResourceEntity> resources = resourceRepository.findByCourseOrderByOrderIndex(course);
        return convertToResourceResponses(resources);
    }

    // Helper Methods
    private CourseEntity getCourseByIdAndTeacher(Long courseId, Long teacherId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
        
        if (!course.getTeacher().getUserId().equals(teacherId)) {
            throw new BadRequestException("You can only access your own courses");
        }
        
        return course;
    }

    private Set<TagCourseEntity> processTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Set.of();
        }

        return tagNames.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(name -> {
                    String trimmedName = name.trim();
                    return TagCourseRepository.findByName(trimmedName)
                            .orElseGet(() -> {
                                TagCourseEntity newTag = TagCourseEntity.builder()
                                        .name(trimmedName)
                                        .build();
                                return TagCourseRepository.save(newTag);
                            });
                })
                .collect(Collectors.toSet());
    }

    private void updateCourseAverageRating(CourseEntity course) {
        ratingRepository.findAverageRatingByCourseId(course.getCourseId())
                .ifPresentOrElse(
                        avgRating -> course.setAverageRating(avgRating),
                        () -> course.setAverageRating(null)
                );
        courseRepository.save(course);
    }

    // Conversion Methods
    private CourseResponseDto convertToCourseResponse(CourseEntity entity) {
        return CourseResponseDto.builder()
                .courseId(entity.getCourseId())
                .title(entity.getTitle())
                .shortDescription(entity.getShortDescription())
                .fullDescription(entity.getFullDescription())
                .courseLevel(entity.getCourseLevel())
                .status(entity.getStatus())
                .thumbnailUrl(entity.getThumbnailUrl())
                .price(entity.getPrice())
                .teacher(convertToTeacherSummaryResponse(entity.getTeacher()))
                .tags(entity.getTags().stream()
                        .map(this::convertToTagResponse)
                        .collect(Collectors.toSet()))
                .totalEnrollments(entity.getTotalEnrollments())
                .averageRating(entity.getAverageRating())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TeacherSummaryResponseDto convertToTeacherSummaryResponse(UserEntity user) {
        return TeacherSummaryResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }

    private TagCourseResponseDto convertToTagResponse(TagCourseEntity entity) {
        return TagCourseResponseDto.builder()
                .tagCourseId(entity.getTagCourseId())
                .name(entity.getName())
                .color(entity.getColor())
                .build();
    }

    private List<LessonResponseDto> convertToLessonResponses(List<LessonEntity> entities) {
        return entities.stream()
                .map(this::convertToLessonResponse)
                .collect(Collectors.toList());
    }

    private LessonResponseDto convertToLessonResponse(LessonEntity entity) {
        return LessonResponseDto.builder()
                .lessonId(entity.getLessonId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .orderIndex(entity.getOrderIndex())
                .videoUrl(entity.getVideoUrl())
                .durationMinutes(entity.getDurationMinutes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private List<ExerciseResponseDto> convertToExerciseResponses(List<ExerciseEntity> entities) {
        return entities.stream()
                .map(this::convertToExerciseResponse)
                .collect(Collectors.toList());
    }

    private ExerciseResponseDto convertToExerciseResponse(ExerciseEntity entity) {
        return ExerciseResponseDto.builder()
                .exerciseId(entity.getExerciseId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .instructions(entity.getInstructions())
                .orderIndex(entity.getOrderIndex())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private List<ResourceResponseDto> convertToResourceResponses(List<ResourceEntity> entities) {
        return entities.stream()
                .map(this::convertToResourceResponse)
                .collect(Collectors.toList());
    }

    private ResourceResponseDto convertToResourceResponse(ResourceEntity entity) {
        return ResourceResponseDto.builder()
                .resourceId(entity.getResourceId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .resourceType(entity.getResourceType())
                .url(entity.getUrl())
                .orderIndex(entity.getOrderIndex())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private EnrollmentResponseDto convertToEnrollmentResponse(EnrollmentEntity entity) {
        CourseResponseDto.Summary courseSummary = CourseResponseDto.Summary.builder()
                .courseId(entity.getCourse().getCourseId())
                .title(entity.getCourse().getTitle())
                .shortDescription(entity.getCourse().getShortDescription())
                .courseLevel(entity.getCourse().getCourseLevel())
                .thumbnailUrl(entity.getCourse().getThumbnailUrl())
                .price(entity.getCourse().getPrice())
                .teacherName(entity.getCourse().getTeacher().getUsername())
                .totalEnrollments(entity.getCourse().getTotalEnrollments())
                .averageRating(entity.getCourse().getAverageRating())
                .createdAt(entity.getCourse().getCreatedAt())
                .build();

        return EnrollmentResponseDto.builder()
                .enrollmentId(entity.getEnrollmentId())
                .course(courseSummary)
                .status(entity.getStatus())
                .paidAmount(entity.getPaidAmount())
                .completedLessons(entity.getCompletedLessons())
                .completedExercises(entity.getCompletedExercises())
                .progressPercentage(entity.getProgressPercentage())
                .enrolledAt(entity.getCreatedAt())
                .build();
    }

    private RatingResponseDto convertToRatingResponse(RatingEntity entity) {
        return RatingResponseDto.builder()
                .ratingId(entity.getRatingId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .studentName(entity.getStudent().getUsername())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
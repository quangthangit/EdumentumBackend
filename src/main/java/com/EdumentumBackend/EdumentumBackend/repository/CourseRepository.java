package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

       // Find courses by teacher
       // Page<CourseEntity> findByTeacherAndStatus(UserEntity teacher, CourseStatus
       // status, Pageable pageable);

       List<CourseEntity> findByTeacherUserId(Long teacherId);

       // Find published courses
       Page<CourseEntity> findByCourseStatus(CourseStatus courseStatus, Pageable pageable);

       // Search courses
       @Query("SELECT c FROM CourseEntity c WHERE c.courseStatus = 'PUBLISHED' AND " +
                     "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<CourseEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

       // Filter courses
       @Query("SELECT c FROM CourseEntity c WHERE c.courseStatus = 'PUBLISHED' " +
                     "AND (:level IS NULL OR c.courseLevel = :level) " +
                     "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
                     "AND (:maxPrice IS NULL OR c.price <= :maxPrice)")
       Page<CourseEntity> findByFilters(@Param("level") CourseLevel level,
                     @Param("minPrice") BigDecimal minPrice,
                     @Param("maxPrice") BigDecimal maxPrice,
                     Pageable pageable);

       // Filter courses by teacherId
       @Query("SELECT c FROM CourseEntity c WHERE c.teacher = :teacher AND c.courseStatus = :courseStatus")
       Page<CourseEntity> findByTeacherAndCourseStatus(@Param("teacher") UserEntity teacher,
                     @Param("courseStatus") CourseStatus courseStatus, Pageable pageable);

       // Find courses by tags
       @Query("SELECT DISTINCT c FROM CourseEntity c JOIN c.courseTags t WHERE c.courseStatus = 'PUBLISHED' AND t.name IN :tagNames")
       Page<CourseEntity> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);

       // Popular courses (most enrolled)
       @Query("SELECT c FROM CourseEntity c WHERE c.courseStatus = 'PUBLISHED' ORDER BY c.totalEnrollments DESC")
       Page<CourseEntity> findPopularCourses(Pageable pageable);

       // Highly rated courses
       @Query("SELECT c FROM CourseEntity c WHERE c.courseStatus = 'PUBLISHED' AND c.averageRating IS NOT NULL ORDER BY c.averageRating DESC")
       Page<CourseEntity> findHighlyRatedCourses(Pageable pageable);

       // Free courses
       Page<CourseEntity> findByCourseStatusAndPrice(CourseStatus courseStatus, BigDecimal price, Pageable pageable);
}
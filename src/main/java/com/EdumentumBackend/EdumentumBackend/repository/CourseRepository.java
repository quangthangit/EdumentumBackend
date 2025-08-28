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
    Page<CourseEntity> findByTeacherAndStatus(UserEntity teacher, CourseStatus status, Pageable pageable);
    
    List<CourseEntity> findByTeacherUserId(Long teacherId);
    
    // Find published courses
    Page<CourseEntity> findByStatus(CourseStatus status, Pageable pageable);
    
    // Search courses
    @Query("SELECT c FROM CourseEntity c WHERE c.status = 'PUBLISHED' AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CourseEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter courses
    @Query("SELECT c FROM CourseEntity c WHERE c.status = 'PUBLISHED' " +
           "AND (:level IS NULL OR c.courseLevel = :level) " +
           "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR c.price <= :maxPrice)")
    Page<CourseEntity> findByFilters(@Param("level") CourseLevel level,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);
    
    // Find courses by tags
    @Query("SELECT DISTINCT c FROM CourseEntity c JOIN c.tags t WHERE c.status = 'PUBLISHED' AND t.name IN :tagCourseNames")
    Page<CourseEntity> findByTagNames(@Param("tagCourseNames") List<String> tagCourseNames, Pageable pageable);
    
    // Popular courses (most enrolled)
    @Query("SELECT c FROM CourseEntity c WHERE c.status = 'PUBLISHED' ORDER BY c.totalEnrollments DESC")
    Page<CourseEntity> findPopularCourses(Pageable pageable);
    
    // Highly rated courses
    @Query("SELECT c FROM CourseEntity c WHERE c.status = 'PUBLISHED' AND c.averageRating IS NOT NULL ORDER BY c.averageRating DESC")
    Page<CourseEntity> findHighlyRatedCourses(Pageable pageable);
    
    // Free courses
    Page<CourseEntity> findByStatusAndPrice(CourseStatus status, BigDecimal price, Pageable pageable);
}
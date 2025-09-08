package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.ResourceEntity;
import com.EdumentumBackend.EdumentumBackend.enums.ResourceType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
    
    List<ResourceEntity> findByCourseOrderByOrderIndex(CourseEntity course);
    
    List<ResourceEntity> findByCourse_CourseIdOrderByOrderIndex(Long courseId);
    
    List<ResourceEntity> findByCourseAndResourceType(CourseEntity course, ResourceType resourceType);
}

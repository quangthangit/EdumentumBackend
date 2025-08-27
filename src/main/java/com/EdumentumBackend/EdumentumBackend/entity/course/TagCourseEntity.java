package com.EdumentumBackend.EdumentumBackend.entity.course;

import com.EdumentumBackend.EdumentumBackend.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "course_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCourseEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagCourseId;
    
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String name;
    
    @Size(max = 7)
    @Column(nullable = true)
    private String color; // hex color code
    
    @ManyToMany(mappedBy = "tags")
    private Set<CourseEntity> courses;
}

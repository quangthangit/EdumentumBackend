package com.EdumentumBackend.EdumentumBackend.entity.course;

import com.EdumentumBackend.EdumentumBackend.entity.BaseEntity;
import com.EdumentumBackend.EdumentumBackend.enums.ResourceType;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;
    
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String title;
    
    @Size(max = 500)
    @Column(nullable = true)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;
    
    @NotBlank
    @Column(nullable = false)
    private String url;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;
}
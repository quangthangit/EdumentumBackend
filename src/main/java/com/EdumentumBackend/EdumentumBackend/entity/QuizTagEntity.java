package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="quiz_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizTagEntity {

    @EmbeddedId
    private QuizTagId id;

    @ManyToOne
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id")
    private QuizzesEntity quiz;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private TagsEntity tag;

    @Column(name = "weight")
    private Integer weight = 1;

//    @ManyToOne
//    @JoinColumn(name = "assigned_by")
//    private UserEntity assignedBy;

//    @Column(name = "is_ai_generated")
//    private Boolean isAiGenerated = false;
//
//    @Column(name = "confidence_score", precision = 3, scale = 2)
//    private BigDecimal confidenceScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
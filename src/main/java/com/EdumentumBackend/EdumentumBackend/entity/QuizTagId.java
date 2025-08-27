package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QuizTagId implements Serializable {

    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "tag_id")
    private Long tagId;
}
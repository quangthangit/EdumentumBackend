package com.EdumentumBackend.EdumentumBackend.commom.model;

import com.EdumentumBackend.EdumentumBackend.entity.QuestionType;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionData {
    private String id;
    private String question;
    private QuestionType type;
    private String difficulty;
    private String bloom_level;
    private Integer points;
    private Integer order_index;
    private String explanation;
    private List<Answer> answers;
}

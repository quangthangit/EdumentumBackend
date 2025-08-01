package com.EdumentumBackend.EdumentumBackend.commom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizMetadata {
    private Integer total_questions;
    private Integer total_points;
    private Integer estimated_time;
    private List<String> tags;
    private boolean ai_generated;
    private String source_summary;
} 
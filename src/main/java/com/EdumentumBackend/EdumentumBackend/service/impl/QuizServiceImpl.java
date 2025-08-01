package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.commom.model.*;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.repository.QuizRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    @Override
    public QuizResponseDto create(QuizRequestDto quizRequestDto) throws JsonProcessingException {
        try {
            System.out.println("Creating quiz: " + quizRequestDto.getTitle());
            System.out.println("Questions count: " + quizRequestDto.getQuestions().size());
            
            // Create QuizData structure
            QuizData quizData = createQuizData(quizRequestDto);
            
            // Create QuizEntity from DTO
            QuizEntity quizEntity = QuizEntity.builder()
                    .title(quizRequestDto.getTitle())
                    .description(quizRequestDto.getDescription())
                    .visibility(quizRequestDto.isVisibility() ? QuizVisibility.PUBLIC : QuizVisibility.PRIVATE)
                    .numberOfQuestions(quizRequestDto.getTotal())
                    .sourceContent(quizRequestDto.getTopic())
                    .sourceType(SourceType.MANUAL)
                    .isAiGenerated(false)
                    .quizData(quizData)
                    .estimatedTime(quizData.getMetadata().getEstimated_time())
                    .aiModel(null)
                    .passingScore(70)
                    .difficulty(QuizDifficultyType.EASY)
                    .mode(QuizModeType.QUIZ)
                    .questionType(QuestionType.MIXED)
                    .language(QuizLanguageType.AUTO)
                    .parsingMode(ParsingModeType.FAST)
                    .task(QuizTask.GENERATE_QUIZ)
                    .user(null)
                    .category(null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            System.out.println("Saving quiz entity...");
            // Save to database
            QuizEntity savedQuiz = quizRepository.save(quizEntity);
            System.out.println("Quiz saved with ID: " + savedQuiz.getId());

            // Convert to response DTO
            return QuizResponseDto.builder()
                    .quizId(savedQuiz.getId())
                    .title(savedQuiz.getTitle())
                    .description(savedQuiz.getDescription())
                    .visibility(savedQuiz.getVisibility() == QuizVisibility.PUBLIC)
                    .total(savedQuiz.getNumberOfQuestions())
                    .topic(savedQuiz.getSourceContent())
                    .quizCreationType(QuizCreationType.MANUAL)
                    .quizData(savedQuiz.getQuizData())
                    .build();
        } catch (Exception e) {
            System.err.println("Error creating quiz: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private QuizData createQuizData(QuizRequestDto quizRequestDto) {
        try {
            // Calculate total points from questions
            int totalPoints = quizRequestDto.getQuestions().stream()
                    .mapToInt(QuestionData::getPoints)
                    .sum();
            
            // Create metadata
            QuizMetadata metadata = QuizMetadata.builder()
                    .total_questions(quizRequestDto.getTotal())
                    .total_points(totalPoints)
                    .estimated_time(quizRequestDto.getTotal() * 2) // 2 minutes per question
                    .tags(Arrays.asList(quizRequestDto.getTopic().toLowerCase()))
                    .ai_generated(false)
                    .source_summary("Manual quiz creation")
                    .build();

            // Create settings
            QuizSettings settings = QuizSettings.builder()
                    .shuffle_questions(true)
                    .shuffle_answers(true)
                    .show_explanations(true)
                    .allow_retry(false)
                    .time_limit_per_question(null)
                    .build();

            // Create QuizData
            QuizData quizData = QuizData.builder()
                    .questions(quizRequestDto.getQuestions())
                    .metadata(metadata)
                    .settings(settings)
                    .build();
            
            System.out.println("QuizData created successfully");
            System.out.println("Questions: " + quizData.getQuestions().size());
            System.out.println("Metadata: " + quizData.getMetadata().getTotal_questions() + " questions, " + 
                             quizData.getMetadata().getTotal_points() + " points");
            
            return quizData;
        } catch (Exception e) {
            System.err.println("Error creating QuizData: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 
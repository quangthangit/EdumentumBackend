package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCategoriesEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizzesEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.repository.QuizCategoriesRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private QuizCategoriesRepository quizCategoriesRepository;

    @Autowired
    private UserRepository userRepository;

    private QuizResponseDto toResponseDto(QuizzesEntity entity) {
        QuizResponseDto.QuizResponseDtoBuilder builder = QuizResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .language(entity.getLanguage())
                .visibility(entity.getVisibility())
                .questionType(entity.getQuestionType())
                .numberOfQuestions(entity.getNumberOfQuestions())
                .mode(entity.getMode())
                .difficulty(entity.getDifficulty())
                .parsingMode(entity.getParsingMode())
                .sourceType(entity.getSourceType())
                .sourceContent(entity.getSourceContent())
                .isAiGenerated(entity.getIsAiGenerated())
                .aiModel(entity.getAiModel())
                .generationMode(entity.getGenerationMode())
                .fileProcessingMode(entity.getFileProcessingMode())
                .quizData(entity.getQuizData())
                .tags(entity.getTags())
                .estimatedTime(entity.getEstimatedTime())
                .passingScore(entity.getPassingScore())
                .totalQuestions(entity.getTotalQuestions())
                .totalPoints(entity.getTotalPoints())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Add category if exists
        if (entity.getCategory() != null) {
            QuizCategoriesResponseDto categoryDto = QuizCategoriesResponseDto.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .description(entity.getCategory().getDescription())
                    .isActive(entity.getCategory().getIsActive())
                    .createdAt(entity.getCategory().getCreatedAt())
                    .updatedAt(entity.getCategory().getUpdatedAt())
                    .build();
            builder.category(categoryDto);
        }

        // Add user if exists
        if (entity.getUser() != null) {
            UserResponseDto userDto = UserResponseDto.builder()
                    .userId(entity.getUser().getUserId())
                    .username(entity.getUser().getUsername())
                    .email(entity.getUser().getEmail())
                    .isActive(entity.getUser().getIsActive())
                    .build();
            builder.user(userDto);
        }

        return builder.build();
    }

    private QuizzesEntity toEntity(QuizRequestDto dto, Long userId) {
        return QuizzesEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .userId(userId)
                .language(dto.getLanguage())
                .visibility(dto.getVisibility())
                .questionType(dto.getQuestionType())
                .numberOfQuestions(dto.getNumberOfQuestions())
                .mode(dto.getMode())
                .difficulty(dto.getDifficulty())
                .parsingMode(dto.getParsingMode())
                .sourceType(dto.getSourceType())
                .sourceContent(dto.getSourceContent())
                .isAiGenerated(dto.getIsAiGenerated())
                .aiModel(dto.getAiModel())
                .generationMode(dto.getGenerationMode())
                .fileProcessingMode(dto.getFileProcessingMode())
                .quizData(dto.getQuizData())
                .tags(dto.getTags())
                .estimatedTime(dto.getEstimatedTime())
                .passingScore(dto.getPassingScore())
                .build();
    }

    @Override
    public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId) {
        // Validate category exists
        Optional<QuizCategoriesEntity> categoryOpt = quizCategoriesRepository.findById(quizRequestDto.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with id: " + quizRequestDto.getCategoryId());
        }

        // Validate user exists
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        QuizzesEntity entity = toEntity(quizRequestDto, userId);
        entity.setCategory(categoryOpt.get());

        QuizzesEntity savedEntity = quizzesRepository.save(entity);
        return toResponseDto(savedEntity);
    }

    @Override
    public List<QuizResponseDto> getAllQuizzes(Long userId) {
        List<QuizzesEntity> quizzes = quizzesRepository.findByUserId(userId);
        return quizzes.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizResponseDto getQuizById(Long quizId, Long userId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found with id: " + quizId);
        }

        QuizzesEntity quiz = quizOpt.get();
        // Check if user owns the quiz or if it's public
        if (!quiz.getUserId().equals(userId) && quiz.getVisibility() != com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC) {
            throw new RuntimeException("Access denied to quiz with id: " + quizId);
        }

        return toResponseDto(quiz);
    }

    @Override
    public QuizResponseDto updateQuiz(Long quizId, QuizRequestDto quizRequestDto, Long userId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found with id: " + quizId);
        }

        QuizzesEntity quiz = quizOpt.get();
        if (!quiz.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to update quiz with id: " + quizId);
        }

        // Validate category if changed
        if (quizRequestDto.getCategoryId() != null) {
            Optional<QuizCategoriesEntity> categoryOpt = quizCategoriesRepository.findById(quizRequestDto.getCategoryId());
            if (categoryOpt.isEmpty()) {
                throw new RuntimeException("Category not found with id: " + quizRequestDto.getCategoryId());
            }
            quiz.setCategory(categoryOpt.get());
        }

        // Update fields
        quiz.setTitle(quizRequestDto.getTitle());
        quiz.setDescription(quizRequestDto.getDescription());
        quiz.setLanguage(quizRequestDto.getLanguage());
        quiz.setVisibility(quizRequestDto.getVisibility());
        quiz.setQuestionType(quizRequestDto.getQuestionType());
        quiz.setNumberOfQuestions(quizRequestDto.getNumberOfQuestions());
        quiz.setMode(quizRequestDto.getMode());
        quiz.setDifficulty(quizRequestDto.getDifficulty());
        quiz.setParsingMode(quizRequestDto.getParsingMode());
        quiz.setSourceType(quizRequestDto.getSourceType());
        quiz.setSourceContent(quizRequestDto.getSourceContent());
        quiz.setIsAiGenerated(quizRequestDto.getIsAiGenerated());
        quiz.setAiModel(quizRequestDto.getAiModel());
        quiz.setGenerationMode(quizRequestDto.getGenerationMode());
        quiz.setFileProcessingMode(quizRequestDto.getFileProcessingMode());
        quiz.setQuizData(quizRequestDto.getQuizData());
        quiz.setTags(quizRequestDto.getTags());
        quiz.setEstimatedTime(quizRequestDto.getEstimatedTime());
        quiz.setPassingScore(quizRequestDto.getPassingScore());

        QuizzesEntity savedEntity = quizzesRepository.save(quiz);
        return toResponseDto(savedEntity);
    }

    @Override
    public boolean deleteQuiz(Long quizId, Long userId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            return false;
        }

        QuizzesEntity quiz = quizOpt.get();
        if (!quiz.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to delete quiz with id: " + quizId);
        }

        quizzesRepository.deleteById(quizId);
        return true;
    }

    @Override
    public List<QuizResponseDto> getQuizzesByCategory(Long categoryId, Long userId) {
        List<QuizzesEntity> quizzes = quizzesRepository.findByCategoryId(categoryId);
        return quizzes.stream()
                .filter(quiz -> quiz.getUserId().equals(userId) ||
                               quiz.getVisibility() == com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResponseDto> searchQuizzes(String title, Long userId) {
        List<QuizzesEntity> quizzes = quizzesRepository.findByTitleContaining(title);
        return quizzes.stream()
                .filter(quiz -> quiz.getUserId().equals(userId) ||
                               quiz.getVisibility() == com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}

package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.*;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.enums.QuizStatus;
import com.EdumentumBackend.EdumentumBackend.repository.QuizTagRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import com.EdumentumBackend.EdumentumBackend.service.TagsService;
import com.EdumentumBackend.EdumentumBackend.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private QuizTagRepository quizTagRepository;

    private QuizResponseDto toResponseDto(QuizzesEntity entity) {
        QuizResponseDto.QuizResponseDtoBuilder builder = QuizResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .thumbnailUrl(entity.getThumbnailUrl())
                .visibility(entity.getVisibility())
                .difficulty(entity.getDifficulty())
                .sourceType(entity.getSourceType())
                .isAiGenerated(entity.getIsAiGenerated())
                .aiModel(entity.getAiModel())
                .quizData(entity.getQuizData())
                .estimatedTime(entity.getEstimatedTime())
                .passingScore(entity.getPassingScore())
                .maxAttempts(entity.getMaxAttempts())
                .totalQuestions(entity.getTotalQuestions())
                .totalPoints(entity.getTotalPoints())
                .viewCount(entity.getViewCount())
                .attemptCount(entity.getAttemptCount())
                .completionCount(entity.getCompletionCount())
                .avgScore(entity.getAvgScore())
                .avgCompletionTime(entity.getAvgCompletionTime())
                .bookmarkCount(entity.getBookmarkCount())
                .shareCount(entity.getShareCount())
                .isFeatured(entity.getIsFeatured())
                .isTrending(entity.getIsTrending())
                .isPremium(entity.getIsPremium())
                .status(entity.getStatus().name())
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .canonicalUrl(entity.getCanonicalUrl())
                .publishedAt(entity.getPublishedAt())
                .archivedAt(entity.getArchivedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Add keywords if exists
        if (entity.getKeywords() != null) {
            builder.keywords(Arrays.asList(entity.getKeywords()));
        }

        // Add original quiz if exists
        if (entity.getOriginalQuiz() != null) {
            builder.originalQuizId(entity.getOriginalQuiz().getId());
        }

        // Add user if exists
        if (entity.getUser() != null) {
            UserResponseDto userDto = UserResponseDto.builder()
                    .userId(entity.getUser().getUserId()) 
                    .username(entity.getUser().getUsername())
                    .email(entity.getUser().getEmail())
                    .isActive(entity.getUser().getIsActive())
                    .imageUrl(entity.getUser().getImageUrl())
                    .roles(entity.getUser().getRoles())
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
//                .language(dto.getLanguage())
                .visibility(dto.getVisibility())

                .difficulty(dto.getDifficulty())
                .sourceType(dto.getSourceType())
                .isAiGenerated(dto.getIsAiGenerated())
                .aiModel(dto.getAiModel())
                .quizData(dto.getQuizData())
                .estimatedTime(dto.getEstimatedTime())
                .passingScore(dto.getPassingScore())
                .build();
    }

    @Override
    @Transactional
    public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId) {
        // Validate user exists
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Create the quiz entity
        QuizzesEntity quizEntity = QuizzesEntity.builder()
                .title(quizRequestDto.getTitle())
                .slug(SlugUtil.toUniqueSlug(quizRequestDto.getTitle()))
                .description(quizRequestDto.getDescription())
                .thumbnailUrl(quizRequestDto.getThumbnailUrl())
                .userId(userId)
                .quizData(quizRequestDto.getQuizData())
                .difficulty(quizRequestDto.getDifficulty())
                .estimatedTime(quizRequestDto.getEstimatedTime())
                .passingScore(quizRequestDto.getPassingScore())
                .maxAttempts(quizRequestDto.getMaxAttempts())
                .isAiGenerated(quizRequestDto.getIsAiGenerated())
                .aiModel(quizRequestDto.getAiModel())
                .sourceType(quizRequestDto.getSourceType())
                .metaTitle(quizRequestDto.getMetaTitle())
                .metaDescription(quizRequestDto.getMetaDescription())
                .canonicalUrl(quizRequestDto.getCanonicalUrl())
                .keywords(quizRequestDto.getKeywords() != null ?
                         quizRequestDto.getKeywords().toArray(new String[0]) : null)
                .visibility(quizRequestDto.getVisibility())
                .status(QuizStatus.DRAFT)
                .isPremium(quizRequestDto.getIsPremium())
                .totalQuestions(calculateTotalQuestions(quizRequestDto.getQuizData()))
                .totalPoints(calculateTotalPoints(quizRequestDto.getQuizData()))
                .build();

        // Save the quiz to get an ID
        QuizzesEntity savedQuiz = quizzesRepository.save(quizEntity);

        // Handle tags
        if (quizRequestDto.getTags() != null && !quizRequestDto.getTags().isEmpty()) {
            processQuizTags(savedQuiz, quizRequestDto.getTags());
        }

        // Refresh the quiz to get the associated tags
        savedQuiz = quizzesRepository.findById(savedQuiz.getId()).orElseThrow();

        return mapToResponseDto(savedQuiz);
    }

    /**
     * Process tags for a quiz - checks for existing tags and creates new ones if needed
     */
    private void processQuizTags(QuizzesEntity quiz, List<TagRequestDto> tagRequests) {
        if (tagRequests == null || tagRequests.isEmpty()) {
            return;
        }

        // Process each tag
        for (TagRequestDto tagRequest : tagRequests) {
            // Get or create the tag
            TagResponseDto tagResponse = tagsService.getOrCreateTag(tagRequest);

            // Create quiz-tag association
            QuizTagId quizTagId = new QuizTagId(quiz.getId(), tagResponse.getId());
            QuizTagEntity quizTag = QuizTagEntity.builder()
                    .id(quizTagId)
                    .quiz(quiz)
                    .tag(TagsEntity.builder().id(tagResponse.getId()).build())
                    .weight(1)
                    .createdAt(LocalDateTime.now())
                    .build();

            quizTagRepository.save(quizTag);
        }
    }

    /**
     * Maps a QuizzesEntity to a QuizResponseDto including tags
     */
    private QuizResponseDto mapToResponseDto(QuizzesEntity entity) {
        // Get tags for this quiz
        List<QuizTagEntity> quizTags = quizTagRepository.findByQuizId(entity.getId());
        List<TagResponseDto> tagDtos = quizTags.stream()
                .map(quizTag -> tagsService.getTagById(quizTag.getTag().getId()))
                .collect(Collectors.toList());

        return QuizResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .thumbnailUrl(entity.getThumbnailUrl())
                .difficulty(entity.getDifficulty())
                .estimatedTime(entity.getEstimatedTime())
                .totalQuestions(entity.getTotalQuestions())
                .totalPoints(entity.getTotalPoints())
                .passingScore(entity.getPassingScore())
                .maxAttempts(entity.getMaxAttempts())
                .isAiGenerated(entity.getIsAiGenerated())
                .aiModel(entity.getAiModel())
                .sourceType(entity.getSourceType())
                .quizData(entity.getQuizData())
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .canonicalUrl(entity.getCanonicalUrl())
                .keywords(entity.getKeywords() != null ?
                        Arrays.asList(entity.getKeywords()) : null)
                .viewCount(entity.getViewCount())
                .attemptCount(entity.getAttemptCount())
                .completionCount(entity.getCompletionCount())
                .avgScore(entity.getAvgScore())
                .avgCompletionTime(entity.getAvgCompletionTime())
                .bookmarkCount(entity.getBookmarkCount())
                .shareCount(entity.getShareCount())
                .visibility(entity.getVisibility())
                .status(entity.getStatus().name())
                .isFeatured(entity.getIsFeatured())
                .isTrending(entity.getIsTrending())
                .isPremium(entity.getIsPremium())
                .tags(tagDtos)
                .publishedAt(entity.getPublishedAt())
                .archivedAt(entity.getArchivedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Calculate total questions from quiz data
     */
    private Integer calculateTotalQuestions(Map<String, Object> quizData) {
        // Default implementation, adjust according to your actual quiz data structure
        if (quizData == null || !quizData.containsKey("questions")) {
            return 0;
        }

        try {
            List<?> questions = (List<?>) quizData.get("questions");
            return questions.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate total points from quiz data
     */
    private Integer calculateTotalPoints(Map<String, Object> quizData) {
        // Default implementation, adjust according to your actual quiz data structure
        if (quizData == null || !quizData.containsKey("questions")) {
            return 0;
        }

        try {
            List<?> questions = (List<?>) quizData.get("questions");
            // Assuming each question is worth 1 point
            return questions.size();
        } catch (Exception e) {
            return 0;
        }
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

        // Update basic fields
        quiz.setTitle(quizRequestDto.getTitle());
        quiz.setDescription(quizRequestDto.getDescription());
        quiz.setVisibility(quizRequestDto.getVisibility());
        quiz.setDifficulty(quizRequestDto.getDifficulty());
        quiz.setEstimatedTime(quizRequestDto.getEstimatedTime());
        quiz.setPassingScore(quizRequestDto.getPassingScore());
        quiz.setMaxAttempts(quizRequestDto.getMaxAttempts());
        quiz.setIsAiGenerated(quizRequestDto.getIsAiGenerated());
        quiz.setAiModel(quizRequestDto.getAiModel());
        quiz.setSourceType(quizRequestDto.getSourceType());
        quiz.setQuizData(quizRequestDto.getQuizData());
        quiz.setMetaTitle(quizRequestDto.getMetaTitle());
        quiz.setMetaDescription(quizRequestDto.getMetaDescription());
        quiz.setCanonicalUrl(quizRequestDto.getCanonicalUrl());
        quiz.setIsPremium(quizRequestDto.getIsPremium());

        // Update keywords if provided
        if (quizRequestDto.getKeywords() != null) {
            quiz.setKeywords(quizRequestDto.getKeywords().toArray(new String[0]));
        }

        // Save the updated quiz
        QuizzesEntity savedQuiz = quizzesRepository.save(quiz);

        // Handle tags update if provided
        if (quizRequestDto.getTags() != null && !quizRequestDto.getTags().isEmpty()) {
            // Remove existing tags first
            quizTagRepository.deleteByQuizId(quiz.getId());
            // Add the new tags
            processQuizTags(savedQuiz, quizRequestDto.getTags());
        }

        savedQuiz = quizzesRepository.findById(savedQuiz.getId()).orElseThrow();

        return mapToResponseDto(savedQuiz);
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
        // Không còn sử dụng category nên trả về danh sách rỗng hoặc có thể viết lại logic
        // để lọc theo tag thay vì category
        return new ArrayList<>();

        // Hoặc có thể thay thế bằng logic tìm kiếm theo tag
        // List<QuizzesEntity> quizzes = quizTagRepository.findByTagId(categoryId).stream()
        //         .map(QuizTagEntity::getQuiz)
        //         .collect(Collectors.toList());
        // return quizzes.stream()
        //         .filter(quiz -> quiz.getUserId().equals(userId) ||
        //                quiz.getVisibility() == VisibilityType.PUBLIC)
        //         .map(this::toResponseDto)
        //         .collect(Collectors.toList());
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

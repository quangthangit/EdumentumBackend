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
import java.util.function.Consumer;
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

        List<QuizTagEntity> quizTags = quizTagRepository.findByQuizId(entity.getId());
        List<TagResponseDto> tagDtos = null;
        if (quizTags != null && !quizTags.isEmpty()) {
            tagDtos = quizTags.stream()
                    .map(quizTag -> tagsService.getTagById(quizTag.getTag().getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

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
                .updatedAt(entity.getUpdatedAt())
                .tags(tagDtos);

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

    // Remove this unused method since it's never called
    /*
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
    */

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
    public List<QuizResponseDto> searchQuizzes(String title, Long userId) {
        List<QuizzesEntity> quizzes = quizzesRepository.findByTitleContaining(title);
        return quizzes.stream()
                .filter(quiz -> quiz.getUserId().equals(userId) ||
                               quiz.getVisibility() == com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponseDto patchQuiz(Long quizId, Long userId, Map<String, Object> updates) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found with id: " + quizId);
        }

        QuizzesEntity quiz = quizOpt.get();
        if (!quiz.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to update quiz with id: " + quizId);
        }

        applyBasicFieldUpdates(quiz, updates);

        applyEnumFieldUpdates(quiz, updates);

        applyArrayFieldUpdates(quiz, updates);

        applyNestedObjectUpdates(quiz, updates);

        // Save the updated quiz
        QuizzesEntity savedQuiz = quizzesRepository.save(quiz);

        // Process tags if provided
        if (updates.containsKey("tags")) {
            Object tagsObj = updates.get("tags");
            if (tagsObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsObj;
                updateQuizTags(savedQuiz, tagsList);
            }
        }

        // Recalculate totals if quiz data changed
        if (updates.containsKey("quizData")) {
            quiz.setTotalQuestions(calculateTotalQuestions(quiz.getQuizData()));
            quiz.setTotalPoints(calculateTotalPoints(quiz.getQuizData()));
            savedQuiz = quizzesRepository.save(quiz);
        }

        // Refresh the quiz to get the latest state with all associations
        savedQuiz = quizzesRepository.findById(savedQuiz.getId()).orElseThrow();

        return mapToResponseDto(savedQuiz);
    }

    /**
     * Helper method to apply basic field updates to a quiz
     */
    private void applyBasicFieldUpdates(QuizzesEntity quiz, Map<String, Object> updates) {
        // Handle title update (with slug generation)
        applyString(updates, "title", title -> {
            quiz.setTitle(title);
            quiz.setSlug(SlugUtil.toUniqueSlug(title));
        });

        // Handle basic string fields
        applyString(updates, "description", quiz::setDescription);
        applyString(updates, "thumbnailUrl", quiz::setThumbnailUrl);
        applyString(updates, "aiModel", quiz::setAiModel);
        applyString(updates, "metaTitle", quiz::setMetaTitle);
        applyString(updates, "metaDescription", quiz::setMetaDescription);
        applyString(updates, "canonicalUrl", quiz::setCanonicalUrl);

        // Handle numeric fields
        applyInteger(updates, "estimatedTime", quiz::setEstimatedTime);
        applyInteger(updates, "passingScore", quiz::setPassingScore);
        applyInteger(updates, "maxAttempts", quiz::setMaxAttempts);

        // Handle boolean fields
        applyBoolean(updates, "isAiGenerated", quiz::setIsAiGenerated);
        applyBoolean(updates, "isPremium", quiz::setIsPremium);
    }

    /**
     * Helper method to apply enum field updates to a quiz
     */
    private void applyEnumFieldUpdates(QuizzesEntity quiz, Map<String, Object> updates) {
        applyEnum(updates, "difficulty", com.EdumentumBackend.EdumentumBackend.enums.DifficultyLevel.class, quiz::setDifficulty);
        applyEnum(updates, "visibility", com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.class, quiz::setVisibility);
        applyEnum(updates, "status", com.EdumentumBackend.EdumentumBackend.enums.QuizStatus.class, quiz::setStatus);
        applyEnum(updates, "sourceType", com.EdumentumBackend.EdumentumBackend.enums.SourceType.class, quiz::setSourceType);
    }

    /**
     * Helper method to apply array field updates to a quiz
     */
    private void applyArrayFieldUpdates(QuizzesEntity quiz, Map<String, Object> updates) {
        if (updates.containsKey("keywords")) {
            Object keywordsObj = updates.get("keywords");
            if (keywordsObj instanceof List) {
                List<?> keywordsList = (List<?>) keywordsObj;
                String[] keywordsArray = keywordsList.stream()
                        .map(Object::toString)
                        .toArray(String[]::new);
                quiz.setKeywords(keywordsArray);
            }
        }
    }

    /**
     * Helper method to apply nested object updates to a quiz
     */
    private void applyNestedObjectUpdates(QuizzesEntity quiz, Map<String, Object> updates) {
        if (updates.containsKey("quizData")) {
            Object quizDataObj = updates.get("quizData");
            if (quizDataObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> quizData = (Map<String, Object>) quizDataObj;
                quiz.setQuizData(quizData);
            }
        }
    }

    /**
     * Helper method to update quiz tags
     */
    private void updateQuizTags(QuizzesEntity quiz, List<Map<String, Object>> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            return;
        }

        List<TagRequestDto> tagRequests = tagsList.stream()
                .map(this::convertMapToTagRequestDto)
                .collect(Collectors.toList());

        // Remove existing tags first
        quizTagRepository.deleteByQuizId(quiz.getId());

        // Add the new tags
        processQuizTags(quiz, tagRequests);
    }

    /**
     * Convert a map to TagRequestDto
     */
    private TagRequestDto convertMapToTagRequestDto(Map<String, Object> tagMap) {
        TagRequestDto dto = new TagRequestDto();

        if (tagMap.containsKey("id")) {
            Object idObj = tagMap.get("id");
            if (idObj instanceof Number) {
                dto.setId(((Number) idObj).longValue());
            } else if (idObj instanceof String) {
                try {
                    dto.setId(Long.parseLong((String) idObj));
                } catch (NumberFormatException ignored) {
                    // If the ID can't be parsed, it will remain null
                }
            }
        }

        if (tagMap.containsKey("name")) {
            dto.setName((String) tagMap.get("name"));
        }

        if (tagMap.containsKey("icon")) {
            dto.setIcon((String) tagMap.get("icon"));
        }

        if (tagMap.containsKey("color")) {
            dto.setColor((String) tagMap.get("color"));
        }

        if (tagMap.containsKey("description")) {
            dto.setDescription((String) tagMap.get("description"));
        }

        return dto;
    }

    /**
     * Helper method to apply string field updates
     */
    private void applyString(Map<String, Object> updates, String key, Consumer<String> setter) {
        if (updates.containsKey(key) && updates.get(key) != null) {
            setter.accept(updates.get(key).toString());
        }
    }

    /**
     * Helper method to apply integer field updates
     */
    private void applyInteger(Map<String, Object> updates, String key, Consumer<Integer> setter) {
        if (updates.containsKey(key) && updates.get(key) != null) {
            Object val = updates.get(key);
            if (val instanceof Integer) {
                setter.accept((Integer) val);
            } else if (val instanceof Number) {
                setter.accept(((Number) val).intValue());
            } else if (val instanceof String) {
                try {
                    setter.accept(Integer.parseInt((String) val));
                } catch (NumberFormatException ignored) {
                    // Skip if the value can't be parsed as an integer
                }
            }
        }
    }

    /**
     * Helper method to apply boolean field updates
     */
    private void applyBoolean(Map<String, Object> updates, String key, Consumer<Boolean> setter) {
        if (updates.containsKey(key) && updates.get(key) != null) {
            Object val = updates.get(key);
            if (val instanceof Boolean) {
                setter.accept((Boolean) val);
            } else if (val instanceof String) {
                setter.accept(Boolean.parseBoolean((String) val));
            }
        }
    }

    /**
     * Helper method to apply enum field updates
     */
    private <E extends Enum<E>> void applyEnum(
            Map<String, Object> updates, String key, Class<E> enumType, Consumer<E> setter) {
        if (updates.containsKey(key) && updates.get(key) != null) {
            Object val = updates.get(key);
            try {
                if (val instanceof String) {
                    setter.accept(Enum.valueOf(enumType, (String) val));
                } else if (enumType.isInstance(val)) {
                    setter.accept(enumType.cast(val));
                }
            } catch (IllegalArgumentException ignored) {
                // Skip if the value is not a valid enum constant
            }
        }
    }
}

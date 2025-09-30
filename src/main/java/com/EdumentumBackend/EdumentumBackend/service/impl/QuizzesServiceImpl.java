package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.*;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.enums.QuizStatus;
import com.EdumentumBackend.EdumentumBackend.enums.VisibilityType;
import com.EdumentumBackend.EdumentumBackend.event.QuizCreatedEvent;
import com.EdumentumBackend.EdumentumBackend.repository.QuizAttemptRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizTagRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import com.EdumentumBackend.EdumentumBackend.service.TagsService;
import com.EdumentumBackend.EdumentumBackend.service.PermissionService;
import com.EdumentumBackend.EdumentumBackend.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizzesServiceImpl implements QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final TagsService tagsService;
    private final QuizTagRepository quizTagRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PermissionService permissionService;

    private QuizResponseDto mapToResponseDto(QuizzesEntity entity) {
        List<TagResponseDto> tags = entity.getQuizTags() == null ?
                Collections.emptyList() :
                entity.getQuizTags().stream()
                        .map(quizTag -> TagResponseDto.builder()
                                .id(quizTag.getTag().getId())
                                .name(quizTag.getTag().getName())
                                .description(quizTag.getTag().getDescription())
                                .build())
                        .collect(Collectors.toList());

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
                .tags(tags);
        builder.keywords(entity.getKeywords() == null ?
                Collections.emptyList() : Arrays.asList(entity.getKeywords()));

        // Add original quiz if exists
        if (entity.getOriginalQuizId() != null) {
            builder.originalQuizId(entity.getOriginalQuizId());
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
    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryDto> getAllQuizzes(Long userId) {
        return quizzesRepository.findSummariesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDto getQuizById(Long quizId, Long userId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findDetailByIdWithAccess(quizId, userId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found with id: " + quizId);
        }

        QuizzesEntity quiz = quizOpt.get();
        if (!quiz.getUserId().equals(userId) && quiz.getVisibility() != com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC) {
            throw new RuntimeException("Access denied to quiz with id: " + quizId);
        }
        return mapToResponseDto(quiz);
    }

    @Override
    @Transactional
    public QuizResponseDto updateQuiz(Long quizId, QuizRequestDto quizRequestDto, Long userId) {
        QuizzesEntity quiz = findQuizAndVerifyUserAccess(quizId, userId);

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

        // Refresh the quiz to get the associated tags with a single query
        savedQuiz = quizzesRepository.findDetailByIdAndUserId(savedQuiz.getId(), userId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved quiz"));

        return mapToResponseDto(savedQuiz);
    }

    @Override
    @Transactional
    public boolean deleteQuiz(Long quizId, Long userId) {
        try {
            QuizzesEntity quiz = findQuizAndVerifyUserAccess(quizId, userId);

            // Delete all quiz attempts associated with this quiz first to avoid foreign key constraint
            quizAttemptRepository.deleteByQuizId(quizId);

            // Then delete the quiz
            quizzesRepository.delete(quiz);
            return true;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Quiz not found")) {
                return false;
            }
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryDto> searchQuizzes(String title, Long userId) {
        Pageable defaultPage = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        return quizzesRepository.findSummariesByTitleAndUserOrPublic(title, userId, defaultPage).getContent();
    }
    @Override
    @Transactional
    public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId) {
        // Validate user exists
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        // Check if user can create quizzes (using the new permission system)
        if (!permissionService.canUseFeature(userId, "CREATE_QUIZ")) {
            throw new RuntimeException("Quiz creation limit reached or feature not available in your plan.");
        }

        String uniqueSlug = generateUniqueSlug(quizRequestDto.getTitle());
        QuizzesEntity quizEntity = QuizzesEntity.builder()
                .title(quizRequestDto.getTitle())
                .slug(uniqueSlug)
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
                .status(QuizStatus.PUBLISHED)
                .isPremium(quizRequestDto.getIsPremium())
                .totalQuestions(calculateTotalQuestions(quizRequestDto.getQuizData()))
                .totalPoints(calculateTotalPoints(quizRequestDto.getQuizData()))
                .build();


        QuizzesEntity savedQuiz = quizzesRepository.save(quizEntity);
        eventPublisher.publishEvent(new QuizCreatedEvent(this, userId));

        // After successfully saving the quiz, increment usage
        permissionService.incrementUsage(userId, "CREATE_QUIZ");

        if (quizRequestDto.getTags() != null && !quizRequestDto.getTags().isEmpty()) {
            processQuizTags(savedQuiz, quizRequestDto.getTags());
        }

        savedQuiz = quizzesRepository.findDetailByIdAndUserId(savedQuiz.getId(), userId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved quiz"));

        return mapToResponseDto(savedQuiz);
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = SlugUtil.toSlugNoRandom(title);
        if (baseSlug.trim().isEmpty()) {
            baseSlug = "quiz-" + System.currentTimeMillis();
        }

        String candidateSlug = baseSlug;
        int counter = 1;

        // Keep checking until we find a unique slug
        while (quizzesRepository.existsBySlug(candidateSlug)) {
            candidateSlug = baseSlug + "-" + counter;
            counter++;
        }

        return candidateSlug;
    }

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
     * Extract questions list from quiz data safely
     * @param quizData The quiz data map
     * @return List of questions or empty list if not found/error
     */
    private List<?> extractQuestionsFromQuizData(Map<String, Object> quizData) {
        if (quizData == null || !quizData.containsKey("questions")) {
            return Collections.emptyList();
        }

        try {
            return (List<?>) quizData.get("questions");
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Calculate total questions from quiz data
     */
    private Integer calculateTotalQuestions(Map<String, Object> quizData) {
        return extractQuestionsFromQuizData(quizData).size();
    }

    /**
     * Calculate total points from quiz data
     */
    private Integer calculateTotalPoints(Map<String, Object> quizData) {
        // Assuming each question is worth 1 point
        return extractQuestionsFromQuizData(quizData).size();
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

        if (updates.containsKey("quizData")) {
            quiz.setTotalQuestions(calculateTotalQuestions(quiz.getQuizData()));
            quiz.setTotalPoints(calculateTotalPoints(quiz.getQuizData()));
        }

        QuizzesEntity savedQuiz = quizzesRepository.save(quiz);

        if (updates.containsKey("tags")) {
            Object tagsObj = updates.get("tags");
            if (tagsObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsObj;
                updateQuizTagsDiff(savedQuiz, tagsList);
            }
        }

        savedQuiz = quizzesRepository.findDetailByIdAndUserId(savedQuiz.getId(), userId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved quiz"));

        return mapToResponseDto(savedQuiz);
    }

    /**
     * Helper method to apply basic field updates to a quiz
     */
    private void applyBasicFieldUpdates(QuizzesEntity quiz, Map<String, Object> updates) {
        // Handle title update (with slug generation)

        applyString(updates, "title", title -> {
            quiz.setTitle(title);
            quiz.setSlug(SlugUtil.toSlug(title));
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
            if (keywordsObj instanceof List<?> keywordsList) {
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

    private void updateQuizTagsDiff(QuizzesEntity quiz, List<Map<String, Object>> tagsList) {
        if (tagsList == null) return;

        List<TagRequestDto> requests = tagsList.stream()
                .map(this::convertMapToTagRequestDto)
                .toList();

        List<TagResponseDto> resolved = requests.stream()
                .map(tagsService::getOrCreateTag)
                .toList();


        Set<Long> newIds = resolved.stream().map(TagResponseDto::getId).collect(Collectors.toSet());
        Set<Long> curIds = quiz.getQuizTags() == null ? Collections.emptySet() :
                quiz.getQuizTags().stream().map(qt -> qt.getTag().getId()).collect(Collectors.toSet());

        Set<Long> toAdd = newIds.stream().filter(id -> !curIds.contains(id)).collect(Collectors.toSet());
        Set<Long> toRemove = curIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());

        if (!toRemove.isEmpty()) {
            quizTagRepository.deleteByQuizIdAndTagIdIn(quiz.getId(), toRemove);
        }

        if (!toAdd.isEmpty()) {
            List<QuizTagEntity> addEntities = toAdd.stream().map(id -> QuizTagEntity.builder()
                    .id(new QuizTagId(quiz.getId(), id))
                    .quiz(quiz)
                    .tag(TagsEntity.builder().id(id).build())
                    .weight(1)
                    .createdAt(LocalDateTime.now())
                    .build()
            ).collect(Collectors.toList());
            quizTagRepository.saveAll(addEntities);
        }
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

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSummaryDto> getAllQuizzesPaginated(Long userId, Pageable pageable) {
        return quizzesRepository.findSummariesByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSummaryDto> searchQuizzesPaginated(String title, Long userId, Pageable pageable) {
        return quizzesRepository.findSummariesByTitleAndUserOrPublic(title, userId, pageable);
    }

    // New optimized methods for quiz listing with attempt statistics
    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> getAllQuizzes(Long userId, Pageable pageable) {
        Page<QuizListDto> page = quizzesRepository.findQuizListByUserId(userId, pageable);
        enrichQuizzesWithAttemptStats(page.getContent(), userId);
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> searchQuizzes(String title, Long userId, Pageable pageable) {
        Page<QuizListDto> page = quizzesRepository.findQuizListByTitleAndUserOrPublic(title, userId, pageable);
        enrichQuizzesWithAttemptStats(page.getContent(), userId);
        return page;
    }

    private void enrichQuizzesWithAttemptStats(List<QuizListDto> quizzes, Long userId) {
        if (quizzes.isEmpty()) return;

        List<Long> quizIds = quizzes.stream().map(QuizListDto::getId).toList();
        List<Map<String, Object>> attemptStats = quizAttemptRepository.findAttemptStatsByUserAndQuizIds(userId, quizIds);

        Map<Long, Map<String, Object>> statsMap = attemptStats.stream()
                .collect(Collectors.toMap(
                    stats -> (Long) stats.get("quizId"),
                    stats -> stats
                ));

        // Enrich each quiz with attempt statistics
        quizzes.forEach(quiz -> {
            Map<String, Object> stats = statsMap.get(quiz.getId());
            if (stats != null) {
                quiz.setLastAttemptAt((LocalDateTime) stats.get("lastAttemptAt"));
                quiz.setTotalAttempts(((Number) stats.get("totalAttempts")).intValue());
                quiz.setBestCorrectAnswers(((Number) stats.get("bestCorrectAnswers")).intValue());
            }

        });
    }

    // New methods for public quizzes
    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> getPublicQuizzes(Pageable pageable) {
        return quizzesRepository.findPublicQuizList(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> searchPublicQuizzes(String title, Pageable pageable) {
        return quizzesRepository.findPublicQuizListByTitle(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> getPublicQuizzesByTags(List<Long> tagIds, Pageable pageable) {
        return quizzesRepository.findPublicQuizListByTags(tagIds, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizListDto> getPopularPublicQuizzes(String popularityCriteria, Pageable pageable) {
        return quizzesRepository.findPopularPublicQuizList(popularityCriteria, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDto getPublicQuizById(Long quizId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findPublicQuizById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Public quiz not found with id: " + quizId);
        }

        QuizzesEntity quiz = quizOpt.get();
        return mapToResponseDto(quiz);
    }

    private QuizzesEntity findQuizAndVerifyUserAccess(Long quizId, Long userId) {
        Optional<QuizzesEntity> quizOpt = quizzesRepository.findDetailByIdAndUserId(quizId, userId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found with id: " + quizId + " or access denied");
        }
        return quizOpt.get();
    }
}

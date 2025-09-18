package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.attempt.AttemptReviewDto;
import com.EdumentumBackend.EdumentumBackend.dtos.attempt.QuizAttemptDto;
import com.EdumentumBackend.EdumentumBackend.dtos.attempt.SubmitAttemptRequest;
import com.EdumentumBackend.EdumentumBackend.entity.QuizAttemptEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizzesEntity;
import com.EdumentumBackend.EdumentumBackend.enums.AttemptStatus;
import com.EdumentumBackend.EdumentumBackend.repository.QuizAttemptRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import com.EdumentumBackend.EdumentumBackend.service.AttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttemptServiceImpl implements AttemptService {

    private final QuizzesRepository quizzesRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    @Transactional
    public AttemptReviewDto submit(Long quizId, Long userId, SubmitAttemptRequest req) {
        // Validate quiz exists
        QuizzesEntity quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Extract questions from quiz data
        List<Map<String, Object>> questions = extractQuestions(quiz.getQuizData());
        if (questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }

        // Validate and normalize client answers
        List<SubmitAttemptRequest.AnswerItem> validatedAnswers = validateAndNormalizeAnswers(req.getAnswers(), questions);

        // Process answers and calculate scores
        AttemptResult result = processAnswers(questions, validatedAnswers);

        // Calculate timing
        TimingInfo timing = calculateTiming(req);

        // Calculate grade and pass status
        GradeInfo gradeInfo = calculateGrade(result.score, result.maxScore, quiz.getPassingScore());

        // Get attempt number with retry mechanism for race condition
        int attemptNumber = getNextAttemptNumberSafe(quizId, userId);

        // Create quiz snapshot for answers data
        Map<String, Object> answersData = createAnswersData(quiz, result.answerSnapshots);

        // Build and save attempt entity
        QuizAttemptEntity attempt = buildAttemptEntity(
                quizId, userId, attemptNumber, result, timing, gradeInfo, answersData);

        attempt = quizAttemptRepository.save(attempt);

        return toReviewDto(attempt, result.answerSnapshots);
    }

    @Override
    public AttemptReviewDto getReview(Long attemptId, Long userId) {
        QuizAttemptEntity attempt = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<Map<String, Object>> answers = extractAnswersFromData(attempt.getAnswersData());
        return toReviewDto(attempt, answers);
    }

    @Override
    public AttemptReviewDto getLatest(Long quizId, Long userId) {
        QuizAttemptEntity attempt = quizAttemptRepository.findTopByUserIdAndQuizIdOrderByCompletedAtDesc(userId, quizId)
                .orElseThrow(() -> new RuntimeException("No attempts found"));

        List<Map<String, Object>> answers = extractAnswersFromData(attempt.getAnswersData());
        return toReviewDto(attempt, answers);
    }

    @Override
    public List<QuizAttemptDto> getQuizAttempts(Long quizId, Long userId) {
        List<QuizAttemptEntity> attempts = quizAttemptRepository
                .findByUserIdAndQuizIdOrderByCompletedAtDesc(userId, quizId);

        return attempts.stream()
                .map(this::toQuizAttemptDto)
                .collect(Collectors.toList());
    }


    private record AttemptResult(
            int score,
            int maxScore,
            int correct,
            int wrong,
            int skipped,
            List<Map<String, Object>> answerSnapshots
    ) {}

    private record TimingInfo(
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            int timeSpentSec
    ) {}

    private record GradeInfo(
            double percentage,
            BigDecimal percentageScore,
            String grade,
            boolean isPassed
    ) {}

    // ---------- Validation Methods ----------

    private List<SubmitAttemptRequest.AnswerItem> validateAndNormalizeAnswers(
            List<SubmitAttemptRequest.AnswerItem> answers,
            List<Map<String, Object>> questions) {

        if (answers == null) {
            return Collections.emptyList();
        }

        // Create a map of valid option IDs for each question
        Map<String, Set<String>> validOptionsByQuestion = questions.stream()
                .collect(Collectors.toMap(
                        q -> (String) q.get("id"),
                        this::getValidOptionIds
                ));

        return answers.stream()
                .filter(Objects::nonNull)
                .filter(answer -> answer.getQuestionId() != null)
                .map(answer -> normalizeAnswer(answer, validOptionsByQuestion.get(answer.getQuestionId())))
                .collect(Collectors.toList());
    }

    private Set<String> getValidOptionIds(Map<String, Object> question) {
        List<Map<String, String>> options = getQuestionOptions(question);
        return options.stream()
                .map(option -> option.get("id"))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private SubmitAttemptRequest.AnswerItem normalizeAnswer(
            SubmitAttemptRequest.AnswerItem answer,
            Set<String> validOptionIds) {

        if (answer.getSelectedOptionIds() == null || validOptionIds == null) {
            SubmitAttemptRequest.AnswerItem normalized = new SubmitAttemptRequest.AnswerItem();
            normalized.setQuestionId(answer.getQuestionId());
            normalized.setSelectedOptionIds(Collections.emptyList());
            return normalized;
        }

        // Filter out null, duplicates, and invalid option IDs
        List<String> normalizedIds = answer.getSelectedOptionIds().stream()
                .filter(Objects::nonNull)
                .filter(validOptionIds::contains)
                .distinct()
                .collect(Collectors.toList());

        SubmitAttemptRequest.AnswerItem normalized = new SubmitAttemptRequest.AnswerItem();
        normalized.setQuestionId(answer.getQuestionId());
        normalized.setSelectedOptionIds(normalizedIds);
        return normalized;
    }

    private int getNextAttemptNumberSafe(Long quizId, Long userId) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                return quizAttemptRepository.findMaxAttemptNumber(quizId, userId) + 1;
            } catch (DataIntegrityViolationException e) {
                if (i == maxRetries - 1) {
                    log.warn("Failed to get next attempt number after {} retries for quizId={}, userId={}",
                            maxRetries, quizId, userId);
                    throw new RuntimeException("Failed to create attempt due to concurrent access");
                }
                // Wait a small amount before retry
                try {
                    Thread.sleep(50 + (i * 50)); // Progressive backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying attempt creation");
                }
            }
        }
        return 1; // Fallback, should never reach here
    }

    // ---------- Processing Methods ----------

    private AttemptResult processAnswers(List<Map<String, Object>> questions, List<SubmitAttemptRequest.AnswerItem> answers) {
        // Build lookup of client answers by questionId
        Map<String, SubmitAttemptRequest.AnswerItem> answerById = answers.stream()
                .collect(Collectors.toMap(SubmitAttemptRequest.AnswerItem::getQuestionId, a -> a));

        int score = 0, correct = 0, wrong = 0, skipped = 0, maxScore = 0;
        List<Map<String, Object>> answerSnapshots = new ArrayList<>();

        int order = 1;
        for (Map<String, Object> question : questions) {
            String questionId = (String) question.get("id");
            int points = getQuestionPoints(question);
            maxScore += points;

            List<String> correctIds = getCorrectOptionIds(question);
            SubmitAttemptRequest.AnswerItem clientAnswer = answerById.get(questionId);
            List<String> selectedIds = getSelectedOptionIds(clientAnswer);

            boolean isSkipped = selectedIds.isEmpty();
            boolean isCorrect = !isSkipped && new HashSet<>(selectedIds).equals(new HashSet<>(correctIds));

            int earnedPoints = isCorrect ? points : 0;

            // Get time spent for this question
            String timeSpent = getTimeSpentForQuestion(clientAnswer);

            // Update counters
            if (isSkipped) {
                skipped++;
            } else if (isCorrect) {
                correct++;
                score += earnedPoints;
            } else {
                wrong++;
            }

            // Create answer snapshot
            Map<String, Object> snapshot = createAnswerSnapshot(
                    question, questionId, order++, selectedIds, correctIds, isCorrect, points, earnedPoints, timeSpent);
            answerSnapshots.add(snapshot);
        }

        return new AttemptResult(score, maxScore, correct, wrong, skipped, answerSnapshots);
    }

    private TimingInfo calculateTiming(SubmitAttemptRequest req) {
        LocalDateTime startedAt = req.getStartedAt() != null ? req.getStartedAt() : LocalDateTime.now();
        LocalDateTime completedAt = req.getCompletedAt() != null ? req.getCompletedAt() : LocalDateTime.now();

        // Use totalTimeSpent from frontend if available, otherwise fallback to timeSpentSec, then calculate from timestamps
        int timeSpent = 0;
        if (req.getTotalTimeSpent() != null) {
            timeSpent = req.getTotalTimeSpent();
        } else if (req.getTimeSpentSec() != null) {
            timeSpent = req.getTimeSpentSec();
        } else {
            timeSpent = (int) Math.max(0, Duration.between(startedAt, completedAt).getSeconds());
        }

        return new TimingInfo(startedAt, completedAt, timeSpent);
    }

    private GradeInfo calculateGrade(int score, int maxScore, Integer passingScore) {
        double percentage = maxScore > 0 ? (score * 100.0 / maxScore) : 0.0;
        BigDecimal percentageScore = BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
        String grade = calculateGradeLabel(percentage);
        boolean isPassed = percentage >= Optional.ofNullable(passingScore).orElse(70);

        return new GradeInfo(percentage, percentageScore, grade, isPassed);
    }

    // ---------- Helper Methods ----------

    private List<AttemptReviewDto.Option> convertToOptionDtos(List<Map<String, String>> options) {
        if (options == null) {
            return Collections.emptyList();
        }

        return options.stream()
                .filter(Objects::nonNull)
                .map(this::mapToOptionDto)
                .collect(Collectors.toList());
    }

    private AttemptReviewDto.Option mapToOptionDto(Map<String, String> option) {
        return AttemptReviewDto.Option.builder()
                .id(option.get("id"))
                .text(option.get("text"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractQuestions(Map<String, Object> quizData) {
        if (quizData == null) return Collections.emptyList();
        Object questions = quizData.get("questions");
        if (questions instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }

    private int getQuestionPoints(Map<String, Object> question) {
        Object points = question.get("points");
        return (points instanceof Number) ? ((Number) points).intValue() : 1;
    }

    @SuppressWarnings("unchecked")
    private List<String> getCorrectOptionIds(Map<String, Object> question) {
        if (question.containsKey("correctOptionIds")) {
            return (List<String>) question.get("correctOptionIds");
        }
        if (question.containsKey("correctAnswer")) {
            return List.of(Objects.toString(question.get("correctAnswer"), ""));
        }
        return Collections.emptyList();
    }

    private List<String> getSelectedOptionIds(SubmitAttemptRequest.AnswerItem clientAnswer) {
        if (clientAnswer != null && clientAnswer.getSelectedOptionIds() != null) {
            return clientAnswer.getSelectedOptionIds();
        }
        return Collections.emptyList();
    }

    private String getTimeSpentForQuestion(SubmitAttemptRequest.AnswerItem clientAnswer) {
        if (clientAnswer != null && clientAnswer.getTimeSpent() != null) {
            return clientAnswer.getTimeSpent();
        }
        return "0";
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getQuestionOptions(Map<String, Object> question) {
        Object options = question.get("options");
        if (options instanceof List<?> list) {
            return (List<Map<String, String>>) list;
        }
        return Collections.emptyList();
    }

    private Map<String, Object> createAnswerSnapshot(Map<String, Object> question, String questionId,
                                                   int order, List<String> selectedIds, List<String> correctIds,
                                                   boolean isCorrect, int points, int earnedPoints, String timeSpent) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("questionId", questionId);
        snapshot.put("order", order);
        snapshot.put("questionText", Objects.toString(question.get("text"), null));
        snapshot.put("options", getQuestionOptions(question));
        snapshot.put("correctOptionIds", correctIds);
        snapshot.put("selectedOptionIds", selectedIds);
        snapshot.put("isCorrect", isCorrect);
        snapshot.put("pointsPossible", points);
        snapshot.put("pointsEarned", earnedPoints);
        snapshot.put("timeSpent", timeSpent);
        snapshot.put("explanation", Objects.toString(question.get("explanation"), null));
        return snapshot;
    }

    private Map<String, Object> createAnswersData(QuizzesEntity quiz, List<Map<String, Object>> answerSnapshots) {
        return Map.of(
                "quizSnapshot", Map.of(
                        "title", quiz.getTitle(),
                        "totalQuestions", quiz.getTotalQuestions(),
                        "totalPoints", quiz.getTotalPoints(),
                        "passingScore", quiz.getPassingScore()
                ),
                "answers", answerSnapshots
        );
    }

    private QuizAttemptEntity buildAttemptEntity(Long quizId, Long userId, int attemptNumber,
                                               AttemptResult result, TimingInfo timing, GradeInfo gradeInfo,
                                               Map<String, Object> answersData) {
        return QuizAttemptEntity.builder()
                .quizId(quizId)
                .userId(userId)
                .status(AttemptStatus.COMPLETED)
                .attemptNumber(attemptNumber)
                .score(result.score)
                .maxScore(result.maxScore)
                .percentageScore(gradeInfo.percentageScore)
                .grade(gradeInfo.grade)
                .correctAnswers(result.correct)
                .wrongAnswers(result.wrong)
                .skippedAnswers(result.skipped)
                .partialAnswers(0)
                .startedAt(timing.startedAt)
                .completedAt(timing.completedAt)
                .totalTimeSpent(timing.timeSpentSec)
                .activeTimeSpent(timing.timeSpentSec)
                .answersData(answersData)
                .isPassed(gradeInfo.isPassed)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractAnswersFromData(Map<String, Object> answersData) {
        if (answersData == null) return Collections.emptyList();
        Object answers = answersData.get("answers");
        if (answers instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }

    private String calculateGradeLabel(double percentage) {
        if (percentage >= 95) return "A+";
        if (percentage >= 90) return "A";
        if (percentage >= 85) return "B+";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private String getPerformanceLabel(double percentage) {
        if (percentage >= 90) return "Excellent";
        if (percentage >= 75) return "Great";
        if (percentage >= 60) return "Good";
        return "Needs Improvement";
    }

    // ---------- DTO Conversion Methods ----------

    private AttemptReviewDto toReviewDto(QuizAttemptEntity attempt, List<Map<String, Object>> answers) {
        // Use saved percentage score instead of recalculating
        double percentage = attempt.getPercentageScore() != null
                ? attempt.getPercentageScore().doubleValue()
                : 0.0;

        List<AttemptReviewDto.QuestionReview> questionReviews = answers.stream()
                .map(this::mapToQuestionReview)
                .sorted(Comparator.comparing(q -> q.getOrder() == null ? 9999 : q.getOrder()))
                .toList();

        return AttemptReviewDto.builder()
                .attemptId(attempt.getId())
                .quizId(attempt.getQuizId())
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .finalScorePercent(Math.round(percentage * 100.0) / 100.0)
                .correct(attempt.getCorrectAnswers())
                .wrong(attempt.getWrongAnswers())
                .skipped(attempt.getSkippedAnswers())
                .timeSpentSec(attempt.getTotalTimeSpent())
                .performance(getPerformanceLabel(percentage))
                .completedAt(attempt.getCompletedAt())
                .questions(questionReviews)
                .build();
    }

    @SuppressWarnings("unchecked")
    private AttemptReviewDto.QuestionReview mapToQuestionReview(Map<String, Object> answerData) {
        // Properly convert options using helper method instead of direct cast
        List<Map<String, String>> rawOptions = (List<Map<String, String>>)
                answerData.getOrDefault("options", Collections.emptyList());
        List<AttemptReviewDto.Option> options = convertToOptionDtos(rawOptions);

        return AttemptReviewDto.QuestionReview.builder()
                .questionId((String) answerData.get("questionId"))
                .order((Integer) answerData.getOrDefault("order", null))
                .questionText((String) answerData.get("questionText"))
                .isCorrect(Boolean.TRUE.equals(answerData.get("isCorrect")))
                .selectedOptionIds((List<String>) answerData.getOrDefault("selectedOptionIds", Collections.emptyList()))
                .correctOptionIds((List<String>) answerData.getOrDefault("correctOptionIds", Collections.emptyList()))
                .pointsPossible((Integer) answerData.getOrDefault("pointsPossible", 1))
                .pointsEarned((Integer) answerData.getOrDefault("pointsEarned", 0))
                .timeSpent(Objects.toString(answerData.getOrDefault("timeSpent", "0"), "0"))
                .explanation((String) answerData.getOrDefault("explanation", null))
                .options(options)
                .build();
    }

    private QuizAttemptDto toQuizAttemptDto(QuizAttemptEntity attempt) {
        // Use saved percentage score instead of recalculating
        double percentage = attempt.getPercentageScore() != null
                ? attempt.getPercentageScore().doubleValue()
                : 0.0;

        return QuizAttemptDto.builder()
                .attemptId(attempt.getId())
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .finalScorePercent(Math.round(percentage * 100.0) / 100.0)
                .correct(attempt.getCorrectAnswers())
                .wrong(attempt.getWrongAnswers())
                .skipped(attempt.getSkippedAnswers())
                .timeSpentSec(attempt.getTotalTimeSpent())
                .performance(getPerformanceLabel(percentage))
                .completedAt(attempt.getCompletedAt())
                .startedAt(attempt.getStartedAt())
                .build();
    }
}

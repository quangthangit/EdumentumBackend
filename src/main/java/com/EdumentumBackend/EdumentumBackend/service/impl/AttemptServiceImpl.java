
package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.attempt.AttemptReviewDto;
import com.EdumentumBackend.EdumentumBackend.dtos.attempt.SubmitAttemptRequest;
import com.EdumentumBackend.EdumentumBackend.entity.QuizAttemptEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizzesEntity;
import com.EdumentumBackend.EdumentumBackend.enums.AttemptStatus;
import com.EdumentumBackend.EdumentumBackend.repository.QuizAttemptRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import com.EdumentumBackend.EdumentumBackend.service.AttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final QuizzesRepository quizzesRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    @Transactional
    public AttemptReviewDto submit(Long quizId, Long userId, SubmitAttemptRequest req) {
        QuizzesEntity quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Extract questions from quizData
        List<Map<String, Object>> questions = extractQuestions(quiz.getQuizData());
        if (questions.isEmpty())
            throw new RuntimeException("Quiz has no questions");

        // Build lookup of client answers by questionId
        Map<String, SubmitAttemptRequest.AnswerItem> answerById =
                Optional.ofNullable(req.getAnswers()).orElse(List.of())
                        .stream().collect(Collectors.toMap(SubmitAttemptRequest.AnswerItem::getQuestionId, a -> a));

        int score = 0, correct = 0, wrong = 0, skipped = 0, maxScore = 0;

        List<Map<String, Object>> answerSnapshots = new ArrayList<>();

        int order = 1;
        for (Map<String, Object> q : questions) {
            String qid = (String) q.get("id");
            int points = pointsOf(q);
            maxScore += points;

            List<String> correctIds = correctIdsOf(q);
            SubmitAttemptRequest.AnswerItem clientAns = answerById.get(qid);
            List<String> selected = clientAns != null && clientAns.getSelectedOptionIds() != null
                    ? clientAns.getSelectedOptionIds()
                    : List.of();

            boolean isSkipped = selected.isEmpty();
            boolean isCorrect = !isSkipped && new HashSet<>(selected).equals(new HashSet<>(correctIds));

            int earned = isCorrect ? points : 0;

            if (isSkipped) skipped++;
            else if (isCorrect) { correct++; score += earned; }
            else wrong++;

            // snapshot for review
            Map<String, Object> snap = new LinkedHashMap<>();
            snap.put("questionId", qid);
            snap.put("order", order++);
            snap.put("questionText", Objects.toString(q.get("text"), null));
            snap.put("options", mapOptions(q));
            snap.put("correctOptionIds", correctIds);
            snap.put("selectedOptionIds", selected);
            snap.put("isCorrect", isCorrect);
            snap.put("pointsPossible", points);
            snap.put("pointsEarned", earned);
            snap.put("explanation", Objects.toString(q.get("explanation"), null));
            answerSnapshots.add(snap);
        }

        // timing
        LocalDateTime startedAt   = req.getStartedAt()   != null ? req.getStartedAt()   : LocalDateTime.now();
        LocalDateTime completedAt = req.getCompletedAt() != null ? req.getCompletedAt() : LocalDateTime.now();
        int timeSpent = req.getTimeSpentSec() != null
                ? req.getTimeSpentSec()
                : (int) Math.max(0, Duration.between(startedAt, completedAt).getSeconds());

        // percentage & pass/grade
        double pctDouble = maxScore > 0 ? (score * 100.0 / maxScore) : 0.0;
        BigDecimal pct = BigDecimal.valueOf(pctDouble).setScale(2, RoundingMode.HALF_UP);
        boolean isPassed = pctDouble >= Optional.ofNullable(quiz.getPassingScore()).orElse(70);

        // attempt number
        int attemptNumber = quizAttemptRepository.findMaxAttemptNumber(quizId, userId) + 1;

        // answers_data snapshot
        Map<String, Object> answersData = Map.of(
                "quizSnapshot", Map.of(
                        "title", quiz.getTitle(),
                        "totalQuestions", quiz.getTotalQuestions(),
                        "totalPoints", quiz.getTotalPoints(),
                        "passingScore", quiz.getPassingScore()
                ),
                "answers", answerSnapshots
        );

        QuizAttemptEntity attempt = QuizAttemptEntity.builder()
                .quizId(quizId)
                .userId(userId)
                .status(AttemptStatus.COMPLETED)
                .attemptNumber(attemptNumber)
                .score(score)
                .maxScore(maxScore)
                .percentageScore(pct)
                .grade(gradeOf(pctDouble))
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .skippedAnswers(skipped)
                .partialAnswers(0)
                .startedAt(startedAt)
                .completedAt(completedAt)
                .totalTimeSpent(timeSpent)
                .activeTimeSpent(timeSpent)
                .answersData(answersData)
                .isPassed(isPassed)
                .build();

        attempt = quizAttemptRepository.save(attempt);

        // build review dto to return
        return toReviewDto(attempt, answerSnapshots);
    }

    @Override
    public AttemptReviewDto getReview(Long attemptId, Long userId) {
        QuizAttemptEntity at = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<Map<String, Object>> answers = readAnswers(at.getAnswersData());
        return toReviewDto(at, answers);
    }

    @Override
    public AttemptReviewDto getLatest(Long quizId, Long userId) {
        QuizAttemptEntity at = quizAttemptRepository.findTopByUserIdAndQuizIdOrderByCompletedAtDesc(userId, quizId)
                .orElseThrow(() -> new RuntimeException("No attempts yet"));
        List<Map<String, Object>> answers = readAnswers(at.getAnswersData());
        return toReviewDto(at, answers);
    }

    // ---------- Helpers ----------

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractQuestions(Map<String, Object> quizData) {
        if (quizData == null) return List.of();
        Object q = quizData.get("questions");
        if (q instanceof List<?> l) return (List<Map<String, Object>>) (List<?>) l;
        return List.of();
    }

    private int pointsOf(Map<String, Object> q) {
        Object p = q.get("points");
        return (p instanceof Number) ? ((Number) p).intValue() : 1;
    }

    @SuppressWarnings("unchecked")
    private List<String> correctIdsOf(Map<String, Object> q) {
        // Ưu tiên correctOptionIds (array); fallback từ correctAnswer (string) → array 1 phần tử
        if (q.containsKey("correctOptionIds"))
            return (List<String>) q.get("correctOptionIds");
        if (q.containsKey("correctAnswer"))
            return List.of(Objects.toString(q.get("correctAnswer"), ""));
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> mapOptions(Map<String, Object> q) {
        Object opts = q.get("options");
        if (opts instanceof List<?> l) {
            return (List<Map<String, String>>) (List<?>) l;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> readAnswers(Map<String, Object> answersData) {
        if (answersData == null) return List.of();
        Object arr = answersData.get("answers");
        if (arr instanceof List<?> l) return (List<Map<String, Object>>) (List<?>) l;
        return List.of();
    }

    private String gradeOf(double pct) {
        if (pct >= 95) return "A+";
        if (pct >= 90) return "A";
        if (pct >= 85) return "B+";
        if (pct >= 80) return "B";
        if (pct >= 70) return "C";
        if (pct >= 60) return "D";
        return "F";
    }

    private String performanceLabel(double pct) {
        if (pct >= 90) return "Excellent";
        if (pct >= 75) return "Great";
        if (pct >= 60) return "Good";
        return "Needs Improvement";
    }

    private AttemptReviewDto toReviewDto(QuizAttemptEntity at, List<Map<String, Object>> answers) {
        int total = at.getCorrectAnswers() + at.getWrongAnswers() + at.getSkippedAnswers();
        double pct = at.getMaxScore() > 0 ? at.getScore() * 100.0 / at.getMaxScore() : 0.0;

        List<AttemptReviewDto.QuestionReview> items = answers.stream().map(m ->
                        AttemptReviewDto.QuestionReview.builder()
                                .questionId((String) m.get("questionId"))
                                .order((Integer) m.getOrDefault("order", null))
                                .questionText((String) m.get("questionText"))
                                .isCorrect(Boolean.TRUE.equals(m.get("isCorrect")))
                                .selectedOptionIds((List<String>) m.getOrDefault("selectedOptionIds", List.of()))
                                .correctOptionIds((List<String>) m.getOrDefault("correctOptionIds", List.of()))
                                .pointsPossible((Integer) m.getOrDefault("pointsPossible", 1))
                                .pointsEarned((Integer) m.getOrDefault("pointsEarned", 0))
                                .explanation((String) m.getOrDefault("explanation", null))
                                .options((List<AttemptReviewDto.Option>) m.getOrDefault("options", List.of()))
                                .build()
                ).sorted(Comparator.comparing(q -> q.getOrder() == null ? 9999 : q.getOrder()))
                .toList();

        return AttemptReviewDto.builder()
                .attemptId(at.getId())
                .quizId(at.getQuizId())
                .score(at.getScore())
                .maxScore(at.getMaxScore())
                .finalScorePercent(Math.round(pct * 100.0) / 100.0)
                .correct(at.getCorrectAnswers())
                .wrong(at.getWrongAnswers())
                .skipped(at.getSkippedAnswers())
                .timeSpentSec(at.getTotalTimeSpent())
                .performance(performanceLabel(pct))
                .completedAt(at.getCompletedAt())
                .questions(items)
                .build();
    }
}

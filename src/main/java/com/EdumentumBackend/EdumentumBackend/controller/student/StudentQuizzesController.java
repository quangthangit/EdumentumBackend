package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.controller.base.BaseQuizController;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.EdumentumBackend.EdumentumBackend.service.PermissionService;
import com.EdumentumBackend.EdumentumBackend.repository.UsageTrackingRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FeatureRepository;
import java.time.LocalDateTime;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/student/quizzes")
@CrossOrigin(origins = "*")
public class StudentQuizzesController extends BaseQuizController {

    private static final String BASE_PATH = "/api/v1/student/quizzes";
    private final UserService userService;
    private final UsageTrackingRepository usageTrackingRepository;
    private final FeatureRepository featureRepository;

    public StudentQuizzesController(QuizzesService quizzesService, UserService userService, PermissionService permissionService, 
                                  UsageTrackingRepository usageTrackingRepository, FeatureRepository featureRepository) {
        super(quizzesService, permissionService);
        this.userService = userService;
        this.usageTrackingRepository = usageTrackingRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDto user = userService.getUserByEmail(email);
        return user.getUserId();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponseDto>> createQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doCreateQuiz(quizRequestDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizSummaryDto>>> getAllQuizzes() {
        return doGetAllQuizzes();
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getQuizById(@PathVariable Long quizId) {
        return doGetQuizById(quizId);
    }

    @GetMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getQuizByIdAndSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doGetQuizByIdAndSlug(quizId, slug, BASE_PATH);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuiz(quizId, quizRequestDto);
    }

    @PutMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> updateQuizWithSlug(
            @PathVariable Long quizId,
            @PathVariable String slug,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuizWithSlug(quizId, slug, quizRequestDto, BASE_PATH);
    }

    @PatchMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> patchQuiz(@PathVariable Long quizId, @RequestBody Map<String, Object> updates) {
        return doPatchQuiz(quizId, updates);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long quizId) {
        return doDeleteQuiz(quizId);
    }

    @DeleteMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizWithSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doDeleteQuizWithSlug(quizId, slug, BASE_PATH);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<QuizSummaryDto>>> searchQuizzes(@RequestParam String title) {
        return doSearchQuizzes(title);
    }
    
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return doGetAllQuizzes(page, size, sortBy, direction);
    }

    @GetMapping("/search/list")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> searchQuizzes(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return doSearchQuizzes(title, page, size, sortBy, direction);
    }
    
    @GetMapping("/limit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuizLimit() {
        try {
            Long userId = getCurrentUserId();
            
            // Get the CREATE_QUIZ feature
            var featureOpt = featureRepository.findByFeatureKey("CREATE_QUIZ");
            if (featureOpt.isEmpty()) {
                throw new RuntimeException("CREATE_QUIZ feature not found");
            }
            
            Long featureId = featureOpt.get().getId();

            LocalDateTime weekStart = LocalDateTime.now()
                .with(java.time.DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
            
            // Get usage count for this week
            Integer usageCount = usageTrackingRepository.getWeeklyUsageCount(userId, featureId, weekStart);
            
            boolean canCreateQuiz = permissionService.canUseFeature(userId, "CREATE_QUIZ");
            
            Map<String, Object> result = new HashMap<>();
            result.put("canCreateQuiz", canCreateQuiz);
            result.put("quizzesCreatedThisWeek", usageCount);
            result.put("weeklyLimit", 3); // This should come from plan configuration
            
            return ResponseEntity.ok(ApiResponse.success(result, "Quiz limit information retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve quiz limit: " + e.getMessage(), 500));
        }
    }
    
}
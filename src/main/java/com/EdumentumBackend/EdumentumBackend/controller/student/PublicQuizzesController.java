package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import com.EdumentumBackend.EdumentumBackend.service.TagsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/quizzes")
@CrossOrigin(origins = "*")
public class PublicQuizzesController {

    private final QuizzesService quizzesService;
    private final TagsService tagsService;

    public PublicQuizzesController(QuizzesService quizzesService, TagsService tagsService) {
        this.quizzesService = quizzesService;
        this.tagsService = tagsService;
    }

    /**
     * Get all public quizzes with pagination
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Paginated list of public quizzes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> getAllPublicQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            
            // Get only public quizzes
            Page<QuizListDto> quizzes = quizzesService.getPublicQuizzes(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Public quizzes retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving public quizzes", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve public quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Get popular public quizzes with pagination
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param popularityCriteria Criteria to sort by (attemptCount, viewCount, completionCount)
     * @return Paginated list of popular public quizzes
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> getPopularPublicQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "attemptCount") String popularityCriteria) {
        try {
            // Validate popularity criteria
            if (!List.of("attemptCount", "viewCount", "completionCount", "avgScore").contains(popularityCriteria)) {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error("Invalid popularity criteria. Must be one of: attemptCount, viewCount, completionCount, avgScore", 400));
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, popularityCriteria));
            
            Page<QuizListDto> quizzes = quizzesService.getPopularPublicQuizzes(popularityCriteria, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Popular public quizzes retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving popular public quizzes", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve popular public quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Search public quizzes by title with pagination
     * @param title Search term
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Paginated list of public quizzes matching the search term
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> searchPublicQuizzes(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            
            Page<QuizListDto> quizzes = quizzesService.searchPublicQuizzes(title, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Public quizzes search completed successfully"));
        } catch (Exception e) {
            log.error("Error searching public quizzes", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to search public quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Get public quizzes filtered by tag IDs
     * @param tagIds Comma-separated list of tag IDs
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Paginated list of public quizzes filtered by tags
     */
    @GetMapping("/by-tags")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> getPublicQuizzesByTags(
            @RequestParam String tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            
            // Parse tag IDs from comma-separated string
            List<Long> tagIdList = Stream.of(tagIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .toList();
            
            Page<QuizListDto> quizzes = quizzesService.getPublicQuizzesByTags(tagIdList, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Public quizzes by tags retrieved successfully"));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Invalid tag IDs format. Please provide comma-separated numbers.", 400));
        } catch (Exception e) {
            log.error("Error retrieving public quizzes by tags", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve public quizzes by tags: " + e.getMessage(), 500));
        }
    }

    /**
     * Get a single public quiz by ID
     * @param quizId Quiz ID
     * @return Public quiz details
     */
    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getPublicQuizById(@PathVariable Long quizId) {
        try {
            QuizResponseDto quiz = quizzesService.getPublicQuizById(quizId);
            return ResponseEntity.ok(ApiResponse.success(quiz, "Public quiz retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            log.error("Error retrieving public quiz", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve public quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Get all available tags for filtering
     * @return List of all tags
     */
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<?>>> getAllTags() {
        try {
            List<?> tags = tagsService.getAllTags();
            return ResponseEntity.ok(ApiResponse.success(tags, "Tags retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving tags", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve tags: " + e.getMessage(), 500));
        }
    }
}
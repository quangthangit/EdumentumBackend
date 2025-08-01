package com.EdumentumBackend.EdumentumBackend.controller.test;

import com.EdumentumBackend.EdumentumBackend.commom.model.Answer;
import com.EdumentumBackend.EdumentumBackend.commom.model.QuestionData;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuestionType;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCreationType;
import com.EdumentumBackend.EdumentumBackend.service.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/quiz")
@CrossOrigin(origins = "*")
public class QuizTestController {

    private final QuizService quizService;

    public QuizTestController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Quiz test controller is running",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/test-ultra-simple")
    public ResponseEntity<?> testUltraSimpleQuiz() {
        System.out.println("Creating ultra simple quiz...");
        try {
            // Create the simplest possible quiz
            QuizRequestDto ultraSimpleQuiz = QuizRequestDto.builder()
                    .title("Ultra Simple Test")
                    .description("Ultra simple test quiz")
                    .visibility(true)
                    .total(1)
                    .topic("Test")
                    .quizCreationType(QuizCreationType.MANUAL)
                    .questions(Arrays.asList(
                        QuestionData.builder()
                                .id("q1")
                                .question("Is this working?")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .difficulty("EASY")
                                .bloom_level("REMEMBER")
                                .points(1)
                                .order_index(1)
                                .explanation("This is a test.")
                                .answers(Arrays.asList(
                                        Answer.builder().id("a1").text("Yes").correct(true).order_index(1).build()
                                ))
                                .build()
                    ))
                    .build();

            System.out.println("About to call quizService.create...");
            QuizResponseDto response = quizService.create(ultraSimpleQuiz);
            System.out.println("Quiz created successfully with ID: " + response.getQuizId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Ultra simple quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in testUltraSimpleQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create ultra simple quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName(),
                    "stackTrace", e.getStackTrace()
            ));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequestDto quizRequestDto) {
        try {
            System.out.println("Creating quiz with title: " + quizRequestDto.getTitle());
            QuizResponseDto response = quizService.create(quizRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Quiz created successfully",
                    "data", response
            ));
        } catch (JsonProcessingException e) {
            System.err.println("JSON Processing Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to process quiz data: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Internal server error: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/create-sample")
    public ResponseEntity<?> createSampleQuiz() {
        System.out.println("Creating sample quiz...");
        try {
            QuizRequestDto sampleQuiz = createSampleQuizData();
            QuizResponseDto response = quizService.create(sampleQuiz);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Sample quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in createSampleQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create sample quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/test-simple")
    public ResponseEntity<?> testSimpleQuiz() {
        System.out.println("Creating simple quiz...");
        try {
            List<QuestionData> simpleQuestions = Arrays.asList(
                QuestionData.builder()
                        .id("q1")
                        .question("What is 2 + 2?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(1)
                        .explanation("Basic arithmetic: 2 + 2 = 4")
                        .answers(Arrays.asList(
                                Answer.builder().id("a1").text("3").correct(false).order_index(1).build(),
                                Answer.builder().id("a2").text("4").correct(true).order_index(2).build(),
                                Answer.builder().id("a3").text("5").correct(false).order_index(3).build()
                        ))
                        .build()
            );

            QuizRequestDto simpleQuiz = QuizRequestDto.builder()
                    .title("Simple Math Test")
                    .description("A simple math quiz for testing")
                    .visibility(true)
                    .total(1)
                    .topic("Mathematics")
                    .quizCreationType(QuizCreationType.MANUAL)
                    .questions(simpleQuestions)
                    .build();

            QuizResponseDto response = quizService.create(simpleQuiz);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Simple quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in testSimpleQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create simple quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/sample-data")
    public ResponseEntity<?> getSampleQuizData() {
        try {
            QuizRequestDto sampleData = createSampleQuizData();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Sample quiz data for testing",
                    "data", sampleData
            ));
        } catch (Exception e) {
            System.err.println("Error getting sample data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to get sample data: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/create-math-quiz")
    public ResponseEntity<?> createMathQuiz() {
        System.out.println("Creating math quiz...");
        try {
            List<QuestionData> mathQuestions = Arrays.asList(
                QuestionData.builder()
                        .id("q1")
                        .question("What is 5 x 7?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(1)
                        .explanation("5 x 7 = 35")
                        .answers(Arrays.asList(
                                Answer.builder().id("a1").text("30").correct(false).order_index(1).build(),
                                Answer.builder().id("a2").text("35").correct(true).order_index(2).build(),
                                Answer.builder().id("a3").text("40").correct(false).order_index(3).build(),
                                Answer.builder().id("a4").text("45").correct(false).order_index(4).build()
                        ))
                        .build(),
                QuestionData.builder()
                        .id("q2")
                        .question("What is the square root of 16?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(2)
                        .explanation("The square root of 16 is 4 because 4 x 4 = 16")
                        .answers(Arrays.asList(
                                Answer.builder().id("a5").text("2").correct(false).order_index(1).build(),
                                Answer.builder().id("a6").text("4").correct(true).order_index(2).build(),
                                Answer.builder().id("a7").text("8").correct(false).order_index(3).build(),
                                Answer.builder().id("a8").text("16").correct(false).order_index(4).build()
                        ))
                        .build()
            );

            QuizRequestDto mathQuiz = QuizRequestDto.builder()
                    .title("Math Quiz")
                    .description("Basic mathematics quiz")
                    .visibility(true)
                    .total(2)
                    .topic("Mathematics")
                    .quizCreationType(QuizCreationType.MANUAL)
                    .questions(mathQuestions)
                    .build();

            QuizResponseDto response = quizService.create(mathQuiz);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Math quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in createMathQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create math quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/test-minimal")
    public ResponseEntity<?> testMinimalQuiz() {
        System.out.println("Creating minimal quiz...");
        try {
            QuizRequestDto minimalQuiz = QuizRequestDto.builder()
                    .title("Minimal Test")
                    .description("Minimal test quiz")
                    .visibility(true)
                    .total(1)
                    .topic("Test")
                    .quizCreationType(QuizCreationType.MANUAL)
                    .questions(Arrays.asList(
                        QuestionData.builder()
                                .id("q1")
                                .question("Is this a test question?")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .difficulty("EASY")
                                .bloom_level("REMEMBER")
                                .points(1)
                                .order_index(1)
                                .explanation("This is a minimal test question.")
                                .answers(Arrays.asList(
                                        Answer.builder().id("a1").text("Yes").correct(true).order_index(1).build(),
                                        Answer.builder().id("a2").text("No").correct(false).order_index(2).build()
                                ))
                                .build()
                    ))
                    .build();

            QuizResponseDto response = quizService.create(minimalQuiz);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Minimal quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in testMinimalQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create minimal quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/test-geography")
    public ResponseEntity<?> testGeographyQuiz() {
        System.out.println("Creating geography quiz...");
        try {
            List<QuestionData> geographyQuestions = Arrays.asList(
                QuestionData.builder()
                        .id("q1")
                        .question("What is the capital of France?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(1)
                        .explanation("Paris is the capital and largest city of France.")
                        .answers(Arrays.asList(
                                Answer.builder().id("a1").text("London").correct(false).order_index(1).build(),
                                Answer.builder().id("a2").text("Paris").correct(true).order_index(2).build(),
                                Answer.builder().id("a3").text("Berlin").correct(false).order_index(3).build(),
                                Answer.builder().id("a4").text("Madrid").correct(false).order_index(4).build()
                        ))
                        .build(),
                QuestionData.builder()
                        .id("q2")
                        .question("Which planet is closest to the Sun?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(2)
                        .explanation("Mercury is the first planet from the Sun.")
                        .answers(Arrays.asList(
                                Answer.builder().id("a5").text("Venus").correct(false).order_index(1).build(),
                                Answer.builder().id("a6").text("Mercury").correct(true).order_index(2).build(),
                                Answer.builder().id("a7").text("Earth").correct(false).order_index(3).build(),
                                Answer.builder().id("a8").text("Mars").correct(false).order_index(4).build()
                        ))
                        .build()
            );

            QuizRequestDto geographyQuiz = QuizRequestDto.builder()
                    .title("Geography Quiz")
                    .description("Basic geography and astronomy quiz")
                    .visibility(true)
                    .total(2)
                    .topic("Geography")
                    .quizCreationType(QuizCreationType.MANUAL)
                    .questions(geographyQuestions)
                    .build();

            QuizResponseDto response = quizService.create(geographyQuiz);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Geography quiz created successfully",
                    "data", response
            ));
        } catch (Exception e) {
            System.err.println("Error in testGeographyQuiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to create geography quiz: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    private QuizRequestDto createSampleQuizData() {
        List<QuestionData> questions = Arrays.asList(
                QuestionData.builder()
                        .id("q1")
                        .question("What is the capital of France?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(1)
                        .explanation("Paris is the capital and largest city of France.")
                        .answers(Arrays.asList(
                                Answer.builder().id("a1").text("London").correct(false).order_index(1).build(),
                                Answer.builder().id("a2").text("Paris").correct(true).order_index(2).build(),
                                Answer.builder().id("a3").text("Berlin").correct(false).order_index(3).build(),
                                Answer.builder().id("a4").text("Madrid").correct(false).order_index(4).build()
                        ))
                        .build(),
                QuestionData.builder()
                        .id("q2")
                        .question("What is 2 + 2?")
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .difficulty("EASY")
                        .bloom_level("REMEMBER")
                        .points(1)
                        .order_index(2)
                        .explanation("Basic arithmetic: 2 + 2 = 4")
                        .answers(Arrays.asList(
                                Answer.builder().id("a5").text("3").correct(false).order_index(1).build(),
                                Answer.builder().id("a6").text("4").correct(true).order_index(2).build(),
                                Answer.builder().id("a7").text("5").correct(false).order_index(3).build(),
                                Answer.builder().id("a8").text("6").correct(false).order_index(4).build()
                        ))
                        .build()
        );

        return QuizRequestDto.builder()
                .title("Sample Test Quiz")
                .description("This is a sample quiz for testing purposes")
                .visibility(true)
                .total(2)
                .topic("General Knowledge")
                .quizCreationType(QuizCreationType.MANUAL)
                .questions(questions)
                .build();
    }
} 
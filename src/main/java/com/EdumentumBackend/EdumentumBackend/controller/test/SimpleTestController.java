package com.EdumentumBackend.EdumentumBackend.controller.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*")
public class SimpleTestController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "API is working!",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/echo")
    public ResponseEntity<?> echo(@RequestBody(required = false) Object body) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Echo endpoint working",
                "receivedData", body,
                "timestamp", System.currentTimeMillis()
        ));
    }

//    @GetMapping("/db-test")
//    public ResponseEntity<?> testDatabase() {
//        try {
//            // Test if we can access the repository
//            return ResponseEntity.ok(Map.of(
//                    "status", "success",
//                    "message", "Database connection test",
//                    "timestamp", System.currentTimeMillis(),
//                    "javaVersion", System.getProperty("java.version"),
//                    "springProfiles", System.getProperty("spring.profiles.active", "default")
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "status", "error",
//                    "message", "Database test failed: " + e.getMessage(),
//                    "errorType", e.getClass().getSimpleName()
//            ));
//        }
//    }
} 
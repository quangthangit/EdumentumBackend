package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.FileProps;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/mindmaps")
@RequiredArgsConstructor
public class StudentMindMapController {

        private final MindMapService mindMapService;
        private final JwtService jwtService;

        // File-based operations for frontend compatibility
        @GetMapping("/files")
        public ResponseEntity<?> getFiles(@RequestHeader("Authorization") String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                List<FileProps> files = mindMapService.getFilesByUserId(userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Files retrieved successfully",
                                "data", files));
        }

        @PostMapping("/files")
        public ResponseEntity<?> createFile(
                        @RequestBody Map<String, String> request,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                String name = request.get("name");
                String data = request.get("data");

                if (name == null || data == null) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "error",
                                        "error", "Name and data are required"));
                }

                FileProps createdFile = mindMapService.createFile(name, data, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                                "status", "success",
                                "message", "File created successfully",
                                "data", createdFile));
        }

        @PutMapping("/files/{id}")
        public ResponseEntity<?> updateFile(
                        @PathVariable String id,
                        @RequestBody Map<String, String> request,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                String data = request.get("data");
                if (data == null) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "error",
                                        "error", "Data is required"));
                }

                FileProps updatedFile = mindMapService.updateFile(id, data, userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "File updated successfully",
                                "data", updatedFile));
        }

        @DeleteMapping("/files/{id}")
        public ResponseEntity<?> deleteFile(
                        @PathVariable String id,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                mindMapService.deleteFile(id, userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "File deleted successfully"));
        }

        @GetMapping("/files/{id}")
        public ResponseEntity<?> getFileById(
                        @PathVariable String id,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                FileProps file = mindMapService.getFileById(id, userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "File retrieved successfully",
                                "data", file));
        }

        // Legacy endpoints for backward compatibility
        @PostMapping
        public ResponseEntity<?> createMindMap(
                        @RequestBody MindMapDto mindMapDto,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                // Set userId from token instead of from request body
                mindMapDto.setUserId(userId);
                MindMapDto createdMindMap = mindMapService.createMindMap(mindMapDto);

                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                                "status", "success",
                                "message", "Mind map created successfully",
                                "data", createdMindMap));
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getMindMapById(@PathVariable Long id) {
                MindMapDto mindMap = mindMapService.getMindMapById(id);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Mind map retrieved successfully",
                                "data", mindMap));
        }

        @GetMapping("/user")
        public ResponseEntity<?> getMindMapsByUserId(@RequestHeader("Authorization") String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                List<MindMapDto> mindMaps = mindMapService.getAllMindMapsByUserId(userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Mind maps retrieved successfully",
                                "data", mindMaps));
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> updateMindMap(
                        @PathVariable Long id,
                        @RequestBody MindMapDto mindMapDto,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                // Set userId from token instead of from request body
                mindMapDto.setUserId(userId);
                MindMapDto updatedMindMap = mindMapService.updateMindMap(id, mindMapDto);

                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Mind map updated successfully",
                                "data", updatedMindMap));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteMindMap(
                        @PathVariable Long id,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                mindMapService.deleteMindMap(id);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Mind map deleted successfully"));
        }
}
package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapType;

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

                List<MindMapFileResponseDto> files = mindMapService.getFilesByUserId(userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Files retrieved successfully",
                                "data", files));
        }

        @PostMapping("/files")
        public ResponseEntity<?> createFile(
                        @RequestBody MindMapFileRequestDto mindMapFileRequestDto,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                MindMapFileResponseDto createdFile = mindMapService.createFile(mindMapFileRequestDto, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                                "status", "success",
                                "message", "File created successfully",
                                "data", createdFile));
        }

        @PutMapping("/files/{id}")
        public ResponseEntity<?> updateFile(
                        @PathVariable String id,
                        @RequestBody MindMapFileRequestDto mindMapFileRequestDto,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                MindMapFileResponseDto updatedFile = mindMapService.updateFile(id, mindMapFileRequestDto, userId);
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

                MindMapFileResponseDto file = mindMapService.getFileById(id, userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "File retrieved successfully",
                                "data", file));
        }

        @PutMapping("/files/{id}/name")
        public ResponseEntity<?> updateFileName(
                @PathVariable String id,
                @RequestBody Map<String, String> request,
                @RequestHeader("Authorization") String authHeader) {

                if ((authHeader == null || !authHeader.startsWith("Bearer "))) {
                        return ResponseEntity.status(401).body(Map.of(
                                "status", "error",
                                "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                String newName = request.get("name");
                if (newName == null || newName.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "status", "error",
                                "error", "File name is required"));
                }

                MindMapFileResponseDto updatedFile = mindMapService.updateFileName(id, newName, userId);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "File name updated successfully",
                        "data", updatedFile));
        }


        // Legacy endpoints for backward compatibility
        @PostMapping
        public ResponseEntity<?> createMindMap(
                        @RequestBody MindMapRequestDto mindMapRequestDto,
                        @RequestHeader("Authorization") String authHeader) {

                if ((authHeader == null) || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                MindMapResponseDto createdMindMap = mindMapService.createMindMap(mindMapRequestDto, userId);

                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                                "status", "success",
                                "message", "Mind map created successfully",
                                "data", createdMindMap));
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getMindMapById(@PathVariable Long id) {
                MindMapResponseDto mindMap = mindMapService.getMindMapById(id);
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

                List<MindMapResponseDto> mindMaps = mindMapService.getAllMindMapsByUserId(userId);
                return ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "Mind maps retrieved successfully",
                                "data", mindMaps));
        }
        
        @GetMapping("/user/type/{type}")
        public ResponseEntity<?> getMindMapsByUserIdAndType(
                @PathVariable String type,
                @RequestHeader("Authorization") String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                try {
                        MindMapType mindMapType = MindMapType.valueOf(type.toUpperCase());
                        List<MindMapResponseDto> mindMaps = mindMapService.getMindMapsByUserIdAndType(userId, mindMapType);
                        return ResponseEntity.ok(Map.of(
                                        "status", "success",
                                        "message", "Mind maps retrieved successfully",
                                        "data", mindMaps));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "error",
                                        "error", "Invalid mind map type"));
                }
        }
        
        @GetMapping("/type/{type}")
        public ResponseEntity<?> getMindMapsByType(@PathVariable String type) {
                try {
                        MindMapType mindMapType = MindMapType.valueOf(type.toUpperCase());
                        List<MindMapResponseDto> mindMaps = mindMapService.getMindMapsByType(mindMapType);
                        return ResponseEntity.ok(Map.of(
                                        "status", "success",
                                        "message", "Mind maps retrieved successfully",
                                        "data", mindMaps));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "error",
                                        "error", "Invalid mind map type"));
                }
        }
        
        @GetMapping("/files/type/{type}")
        public ResponseEntity<?> getFilesByType(
                @PathVariable String type,
                @RequestHeader("Authorization") String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                try {
                        MindMapType mindMapType = MindMapType.valueOf(type.toUpperCase());
                        List<MindMapFileResponseDto> files = mindMapService.getFilesByUserIdAndType(userId, mindMapType);
                        return ResponseEntity.ok(Map.of(
                                        "status", "success",
                                        "message", "Files retrieved successfully",
                                        "data", files));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "error",
                                        "error", "Invalid mind map type"));
                }
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> updateMindMap(
                        @PathVariable Long id,
                        @RequestBody MindMapRequestDto mindMapRequestDto,
                        @RequestHeader("Authorization") String authHeader) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(401).body(Map.of(
                                        "status", "error",
                                        "error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Long userId = jwtService.extractUserId(token);

                MindMapResponseDto updatedMindMap = mindMapService.updateMindMap(id, mindMapRequestDto, userId);

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
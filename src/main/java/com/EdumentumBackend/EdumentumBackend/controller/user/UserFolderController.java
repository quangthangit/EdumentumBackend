package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.FileType;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.FileService;
import com.EdumentumBackend.EdumentumBackend.service.FirebaseStorageService;
import com.EdumentumBackend.EdumentumBackend.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/user/groups")
public class UserFolderController {

    private final FolderService folderService;
    private final FirebaseStorageService firebaseStorageService;
    private final FileService fileService;

    public UserFolderController(FileService fileService, FirebaseStorageService firebaseStorageService, FolderService folderService) {
        this.folderService = folderService;
        this.firebaseStorageService = firebaseStorageService;
        this.fileService = fileService;
    }

    @PostMapping("/{groupId}/folders")
    public ResponseEntity<?> createFolder(@Valid @RequestBody FolderRequestDto folderRequestDto, @PathVariable Long groupId) {
        try {
            Long userId = getCurrentUserId();
            FolderResponseDto folderResponseDto = folderService.createFolder(folderRequestDto, groupId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Folder created successfully",
                    "data", folderResponseDto
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/folders/{groupId}")
    public ResponseEntity<?> getFolderByGroup(@PathVariable Long groupId) {
        try {
            Long userId = getCurrentUserId();
            List<FolderResponseDto> folderResponseDtos = folderService.getAllFolderByGroup(groupId, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Folders retrieved successfully",
                    "data", folderResponseDtos
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PostMapping("/folders/upload-file")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                         @RequestParam("folderId") Long folderId) {
        try {
            Long userId = getCurrentUserId();

            List<CompletableFuture<FileDto>> futures = files.stream()
                    .map(file -> firebaseStorageService.uploadFileAsync(file)
                            .thenApply(url -> {
                                if (url == null) throw new RuntimeException("Upload failed for file: " + file.getOriginalFilename());
                                FileDto dto = new FileDto();
                                dto.setFilename(file.getOriginalFilename());
                                dto.setFileSize(file.getSize());
                                dto.setFileType(getFileType(file));
                                dto.setFileUrl(url);
                                return dto;
                            }))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<FileDto> fileDtos = futures.stream().map(CompletableFuture::join).toList();
            FileRequestDto fileRequestDto = new FileRequestDto();
            fileRequestDto.setFiles(fileDtos);

            List<FileDto> responseDto = fileService.uploadFileResponseDto(fileRequestDto, userId, folderId);
            return ResponseEntity.ok(Map.of(
                    "data",responseDto
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/folders/delete-file/{fileId}")
    private ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Delete file successfully"
                ));
    }

    private FileType getFileType(MultipartFile file) {
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase())
                .orElse("");
        return switch (ext) {
            case "png" -> FileType.IMAGE_PNG;
            case "jpg", "jpeg" -> FileType.IMAGE_JPG;
            case "gif" -> FileType.IMAGE_GIF;
            case "pdf" -> FileType.PDF;
            case "doc" -> FileType.DOC;
            case "docx" -> FileType.DOCX;
            case "ppt" -> FileType.PPT;
            case "pptx" -> FileType.PPTX;
            case "xls" -> FileType.XLS;
            case "xlsx" -> FileType.XLSX;
            case "txt" -> FileType.TXT;
            case "mp4" -> FileType.VIDEO_MP4;
            case "mkv" -> FileType.VIDEO_MKV;
            case "mp3" -> FileType.AUDIO_MP3;
            case "wav" -> FileType.AUDIO_WAV;
            case "zip" -> FileType.ZIP;
            case "rar" -> FileType.RAR;
            default -> FileType.OTHER;
        };
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthorized");
        }
        return userDetails.getId();
    }

    private ResponseEntity<?> buildServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "error", "Internal server error: " + e.getMessage()
        ));
    }
}

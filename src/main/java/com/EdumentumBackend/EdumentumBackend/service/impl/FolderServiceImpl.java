package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FolderEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.*;
import com.EdumentumBackend.EdumentumBackend.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    public FolderResponseDto createFolder(FolderRequestDto folderRequestDto, String publicGroupId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findGroupByPublicId(publicGroupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        GroupMemberEntity groupMemberEntity = groupMemberRepository.findByGroupAndUser(group, user);
        if (groupMemberEntity == null) {
            throw new AuthenticationFailedException("You are not a member of this group");
        }

        FolderEntity folderEntity = FolderEntity.builder()
                .name(folderRequestDto.getName())
                .group(group)
                .user(user)
                .build();

        FolderEntity savedFolder = folderRepository.save(folderEntity);

        return FolderResponseDto.builder()
                .id(savedFolder.getId())
                .folderName(savedFolder.getName())
                .createdAt(savedFolder.getCreatedAt())
                .ownerId(user.getUserId())
                .ownerName(user.getUsername())
                .build();
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

    @Override
    public List<FolderResponseDto> getAllFolderByGroup(String publicGroupId, Long userId) {
        GroupEntity group = groupRepository.findGroupByPublicId(publicGroupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        List<FolderEntity> folderEntities = folderRepository.findAllByGroupId(group.getId());
        return folderEntities.stream()
                .map(folder -> {
                    List<FileDto> fileDtos = folder.getFiles().stream()
                            .map(file -> {
                                FileDto dto = new FileDto();
                                dto.setId(file.getId());
                                dto.setOwnerName(file.getUserEntity().getUsername());
                                dto.setOwnerId(file.getUserEntity().getUserId());
                                dto.setFilename(file.getFilename());
                                dto.setFileType(file.getFileType());
                                dto.setFileUrl(file.getFileUrl());
                                dto.setFileSize(file.getFileSize());
                                return dto;
                            })
                            .toList();

                    List<QuizListDto> quizDtos = folder.getQuizzes().stream()
                            .map(quiz -> QuizListDto.builder()
                                    .id(quiz.getId())
                                    .title(quiz.getTitle())
                                    .slug(quiz.getSlug())
                                    .description(quiz.getDescription())
                                    .difficulty(quiz.getDifficulty())
                                    .maxAttempts(quiz.getMaxAttempts())
                                    .keywords(List.of(quiz.getKeywords()))
                                    .createdAt(quiz.getCreatedAt())
                                    .publishedAt(quiz.getPublishedAt())
                                    .totalQuestions(quiz.getTotalQuestions())
                                    .build()
                            )
                            .toList();

                    enrichQuizzesWithAttemptStats(quizDtos,userId);

                    return FolderResponseDto.builder()
                            .id(folder.getId())
                            .folderName(folder.getName())
                            .files(fileDtos)
                            .quiz(quizDtos)
                            .ownerId(folder.getUser().getUserId())
                            .ownerName(folder.getUser().getUsername())
                            .createdAt(folder.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public void deleteFolderById(Long folderId, Long userId, String publicGroupId) {
//        if (groupMemberRepository.existsByGroup_IdAndUser_UserId(groupId, userId)) {
//            throw new BadRequestException("User has already joined the group");
//        }
        folderRepository.deleteById(folderId);
    }
}

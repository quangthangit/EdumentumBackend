package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardCategoryEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardCategoryRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardCategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardCategoryServiceImpl implements FlashcardCategoryService {

    private final FlashcardCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public FlashcardCategoryResponseDto createCategory(FlashcardCategoryRequestDto requestDto, Long userId) {
        // Tìm user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Kiểm tra xem user đã có category với tên này chưa
        if (categoryRepository.existsByNameAndUserUserId(requestDto.getName(), userId)) {
            throw new IllegalArgumentException("Category with name '" + requestDto.getName() + "' already exists for this user");
        }

        FlashcardCategoryEntity entity = FlashcardCategoryEntity.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isActive(true)
                .user(user)
                .build();

        FlashcardCategoryEntity saved = categoryRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public FlashcardCategoryResponseDto updateCategory(Long id, FlashcardCategoryRequestDto requestDto, Long userId) {
        FlashcardCategoryEntity entity = categoryRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id + " for user: " + userId));

        // Kiểm tra tên trùng (nếu có thay đổi tên)
        if (requestDto.getName() != null && !requestDto.getName().trim().isEmpty()
            && !entity.getName().equals(requestDto.getName())) {
            if (categoryRepository.existsByNameAndUserUserId(requestDto.getName(), userId)) {
                throw new IllegalArgumentException("Category with name '" + requestDto.getName() + "' already exists for this user");
            }
            entity.setName(requestDto.getName());
        }

        if (requestDto.getDescription() != null) {
            entity.setDescription(requestDto.getDescription());
        }

        FlashcardCategoryEntity saved = categoryRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id, Long userId) {
        FlashcardCategoryEntity entity = categoryRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id + " for user: " + userId));

        // Soft delete
        entity.setIsActive(false);
        categoryRepository.save(entity);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public FlashcardCategoryResponseDto getCategoryById(Long id, Long userId) {
        FlashcardCategoryEntity entity = categoryRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id + " for user: " + userId));
        return toResponse(entity);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FlashcardCategoryResponseDto> getAllActiveCategoriesByUser(Long userId) {
        return categoryRepository.findByUserUserIdAndIsActiveTrueOrderByNameAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FlashcardCategoryResponseDto toResponse(FlashcardCategoryEntity e) {
        return FlashcardCategoryResponseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .isActive(e.getIsActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .userId(e.getUser() != null ? e.getUser().getUserId() : null)
                .build();
    }
}

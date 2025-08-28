package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardCategoryEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardCategoryRepository;
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

    @Override
    public FlashcardCategoryResponseDto createCategory(FlashcardCategoryRequestDto requestDto, Long userId) {
        FlashcardCategoryEntity entity = FlashcardCategoryEntity.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isActive(true)
                .build();

        FlashcardCategoryEntity saved = categoryRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public FlashcardCategoryResponseDto updateCategory(Long id, FlashcardCategoryRequestDto requestDto, Long userId) {
        FlashcardCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        if (requestDto.getName() != null && !requestDto.getName().trim().isEmpty()) {
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
        FlashcardCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        categoryRepository.delete(entity);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public FlashcardCategoryResponseDto getCategoryById(Long id) {
        FlashcardCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return toResponse(entity);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FlashcardCategoryResponseDto> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc()
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
                .build();
    }
}

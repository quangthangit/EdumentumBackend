package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryResponseDto;

import java.util.List;


public interface FlashcardCategoryService {
    FlashcardCategoryResponseDto createCategory(FlashcardCategoryRequestDto requestDto, Long userId);
    FlashcardCategoryResponseDto updateCategory(Long id, FlashcardCategoryRequestDto requestDto, Long userId);
    void deleteCategory(Long id, Long userId);
    FlashcardCategoryResponseDto getCategoryById(Long id, Long userId);
    List<FlashcardCategoryResponseDto> getAllActiveCategoriesByUser(Long userId);
}

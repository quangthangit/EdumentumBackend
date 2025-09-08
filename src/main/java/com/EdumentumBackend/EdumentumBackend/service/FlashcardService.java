package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;

public interface FlashcardService {
    FlashcardSetResponseDto createFlashcardSet(FlashcardSetRequestDto flashcardSetRequestDto, Long userId) throws JsonProcessingException;
    PaginatedResponse<FlashcardSetResponseDto> getAllFlashcardSets(Long userId, Pageable pageable, String search, String sortBy);
    PaginatedResponse<FlashcardSetResponseDto> getPublicFlashcardSets(Pageable pageable, String search, String sortBy);
    FlashcardSetResponseDto getFlashcardSetById(Long flashcardSetId, Long userId);
    FlashcardSetResponseDto updateFlashcardSet(Long flashcardSetId, FlashcardSetRequestDto flashcardSetRequestDto, Long userId);
    void deleteFlashcardSet(Long flashcardSetId, Long userId);
}
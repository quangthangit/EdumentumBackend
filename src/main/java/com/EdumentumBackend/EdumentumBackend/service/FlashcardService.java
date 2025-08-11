package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface FlashcardService {
    FlashcardSetResponseDto createFlashcardSet(FlashcardSetRequestDto flashcardSetRequestDto, Long userId) throws JsonProcessingException;
    List<FlashcardSetResponseDto> getAllFlashcardSets(Long userId);
    FlashcardSetResponseDto getFlashcardSetById(Long flashcardSetId, Long userId);
    FlashcardSetResponseDto updateFlashcardSet(Long flashcardSetId, FlashcardSetRequestDto flashcardSetRequestDto, Long userId);
    void deleteFlashcardSet(Long flashcardSetId, Long userId);
} 
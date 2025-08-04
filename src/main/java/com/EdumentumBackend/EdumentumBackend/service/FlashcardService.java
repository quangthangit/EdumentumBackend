package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FlashcardService {
    FlashcardSetDto createFlashcardSet(FlashcardSetDto flashcardSetDto, Long userId) throws JsonProcessingException;
} 
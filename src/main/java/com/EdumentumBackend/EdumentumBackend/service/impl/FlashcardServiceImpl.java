package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetDto;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardEntity;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardSetEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardSetRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;

    public FlashcardServiceImpl(FlashcardSetRepository flashcardSetRepository, 
                              FlashcardRepository flashcardRepository, 
                              UserRepository userRepository) {
        this.flashcardSetRepository = flashcardSetRepository;
        this.flashcardRepository = flashcardRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FlashcardSetDto createFlashcardSet(FlashcardSetDto flashcardSetDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        FlashcardSetEntity flashcardSet = FlashcardSetEntity.builder()
                .title(flashcardSetDto.getTitle())
                .isPublic(false) // Set default value for is_public
                .user(user)
                .flashcards(new ArrayList<>()) // Initialize empty list
                .build();

        FlashcardSetEntity savedFlashcardSet = flashcardSetRepository.save(flashcardSet);

        if (flashcardSetDto.getFlashcards() != null && !flashcardSetDto.getFlashcards().isEmpty()) {
            List<FlashcardEntity> flashcards = flashcardSetDto.getFlashcards().stream()
                    .map(dto -> convertToEntity(dto, savedFlashcardSet, user))
                    .collect(Collectors.toList());
            
            List<FlashcardEntity> savedFlashcards = flashcardRepository.saveAll(flashcards);
            savedFlashcardSet.getFlashcards().addAll(savedFlashcards);
        }

        return convertToDto(savedFlashcardSet);
    }

    private FlashcardSetDto convertToDto(FlashcardSetEntity entity) {
        List<FlashcardDto> flashcardDtos = new ArrayList<>();
        
        if (entity.getFlashcards() != null) {
            flashcardDtos = entity.getFlashcards().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        return FlashcardSetDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .flashcards(flashcardDtos)
                .build();
    }

    private FlashcardDto convertToDto(FlashcardEntity entity) {
        return FlashcardDto.builder()
                .question(entity.getQuestion())
                .choices(entity.getChoices())
                .correctAnswer(entity.getCorrectAnswer())
                .explanation(entity.getExplanation())
                .build();
    }

    private FlashcardEntity convertToEntity(FlashcardDto dto, FlashcardSetEntity flashcardSet, UserEntity user) {
        return FlashcardEntity.builder()
                .question(dto.getQuestion())
                .choices(dto.getChoices())
                .correctAnswer(dto.getCorrectAnswer())
                .explanation(dto.getExplanation())
                .flashcardSet(flashcardSet)
                .user(user)
                .build();
    }
} 
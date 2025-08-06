package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
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
    public FlashcardSetResponseDto createFlashcardSet(FlashcardSetRequestDto flashcardSetRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        FlashcardSetEntity flashcardSet = FlashcardSetEntity.builder()
                .title(flashcardSetRequestDto.getTitle())
                .description(flashcardSetRequestDto.getDescription())
                .isPublic(false) // Set default value for is_public
                .user(user)
                .flashcards(new ArrayList<>()) // Initialize empty list
                .build();

        FlashcardSetEntity savedFlashcardSet = flashcardSetRepository.save(flashcardSet);

        if (flashcardSetRequestDto.getFlashcards() != null && !flashcardSetRequestDto.getFlashcards().isEmpty()) {
            List<FlashcardEntity> flashcards = flashcardSetRequestDto.getFlashcards().stream()
                    .map(dto -> convertToEntity(dto, savedFlashcardSet, user))
                    .collect(Collectors.toList());
            
            List<FlashcardEntity> savedFlashcards = flashcardRepository.saveAll(flashcards);
            savedFlashcardSet.getFlashcards().addAll(savedFlashcards);
        }

        return convertToResponseDto(savedFlashcardSet);
    }

    @Override
    public List<FlashcardSetResponseDto> getAllFlashcardSets(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        List<FlashcardSetEntity> flashcardSets = flashcardSetRepository.findByUserOrderByCreatedAtDesc(user);
        
        return flashcardSets.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlashcardSetResponseDto getFlashcardSetById(Long flashcardSetId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        FlashcardSetEntity flashcardSet = flashcardSetRepository.findByIdAndUser(flashcardSetId, user)
                .orElseThrow(() -> new NotFoundException("Flashcard set not found with id: " + flashcardSetId));

        return convertToResponseDto(flashcardSet);
    }

    @Override
    @Transactional
    public FlashcardSetResponseDto updateFlashcardSet(Long flashcardSetId, FlashcardSetRequestDto flashcardSetRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        FlashcardSetEntity flashcardSet = flashcardSetRepository.findByIdAndUser(flashcardSetId, user)
                .orElseThrow(() -> new NotFoundException("Flashcard set not found with id: " + flashcardSetId));

        // Update only provided fields (PATCH behavior)
        if (flashcardSetRequestDto.getTitle() != null && !flashcardSetRequestDto.getTitle().trim().isEmpty()) {
            flashcardSet.setTitle(flashcardSetRequestDto.getTitle());
        }
        
        if (flashcardSetRequestDto.getDescription() != null) {
            flashcardSet.setDescription(flashcardSetRequestDto.getDescription());
        }

        // Update flashcards only if provided
        if (flashcardSetRequestDto.getFlashcards() != null) {
            // Delete existing flashcards
            flashcardRepository.deleteAll(flashcardSet.getFlashcards());
            flashcardSet.getFlashcards().clear();

            // Add new flashcards if list is not empty
            if (!flashcardSetRequestDto.getFlashcards().isEmpty()) {
                List<FlashcardEntity> newFlashcards = flashcardSetRequestDto.getFlashcards().stream()
                        .map(dto -> convertToEntity(dto, flashcardSet, user))
                        .collect(Collectors.toList());
                
                List<FlashcardEntity> savedFlashcards = flashcardRepository.saveAll(newFlashcards);
                flashcardSet.getFlashcards().addAll(savedFlashcards);
            }
        }

        FlashcardSetEntity updatedFlashcardSet = flashcardSetRepository.save(flashcardSet);
        return convertToResponseDto(updatedFlashcardSet);
    }

    @Override
    @Transactional
    public void deleteFlashcardSet(Long flashcardSetId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        FlashcardSetEntity flashcardSet = flashcardSetRepository.findByIdAndUser(flashcardSetId, user)
                .orElseThrow(() -> new NotFoundException("Flashcard set not found with id: " + flashcardSetId));

        // Delete all flashcards first (if needed due to foreign key constraints)
        flashcardRepository.deleteAll(flashcardSet.getFlashcards());
        
        // Delete the flashcard set
        flashcardSetRepository.delete(flashcardSet);
    }

    private FlashcardSetResponseDto convertToResponseDto(FlashcardSetEntity entity) {
        List<FlashcardResponseDto> flashcardDtos = new ArrayList<>();
        
        if (entity.getFlashcards() != null) {
            flashcardDtos = entity.getFlashcards().stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        }

        UserResponseDto userDto = UserResponseDto.builder()
                .userId(entity.getUser().getUserId())
                .username(entity.getUser().getUsername())
                .email(entity.getUser().getEmail())
                .build();

        return FlashcardSetResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isPublic(entity.getIsPublic())
                .createdAt(entity.getCreatedAt())
                .user(convertUserToDto(entity.getUser()))
                .flashcards(flashcardDtos)
                .build();
    }

    private UserResponseDto convertUserToDto(UserEntity user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isActive(user.getIsActive())
                .build();
    }

    private FlashcardResponseDto convertToResponseDto(FlashcardEntity entity) {
        return FlashcardResponseDto.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .choices(entity.getChoices())
                .correctAnswer(entity.getCorrectAnswer())
                .explanation(entity.getExplanation())
                .build();
    }

    private FlashcardEntity convertToEntity(FlashcardRequestDto dto, FlashcardSetEntity flashcardSet, UserEntity user) {
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
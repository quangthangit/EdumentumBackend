package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.*;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardCategoryEntity;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardEntity;
import com.EdumentumBackend.EdumentumBackend.entity.FlashcardSetEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.FlashcardType;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardCategoryRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FlashcardSetRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final FlashcardCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public FlashcardServiceImpl(FlashcardSetRepository flashcardSetRepository,
                                FlashcardRepository flashcardRepository,
                                FlashcardCategoryRepository categoryRepository,
                                UserRepository userRepository) {
        this.flashcardSetRepository = flashcardSetRepository;
        this.flashcardRepository = flashcardRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FlashcardSetResponseDto createFlashcardSet(FlashcardSetRequestDto flashcardSetRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate flashcard type
        FlashcardType flashcardType = flashcardSetRequestDto.getFlashcardType();
        if(flashcardType == null){
            throw new BadRequestException("FlashcardType type is required");
        }

        // Get category if provided
        FlashcardCategoryEntity category = null;
        if (flashcardSetRequestDto.getCategoryId() != null) {
            category = categoryRepository.findById(flashcardSetRequestDto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + flashcardSetRequestDto.getCategoryId()));
        }

        FlashcardSetEntity flashcardSet = FlashcardSetEntity.builder()
                .title(flashcardSetRequestDto.getTitle())
                .description(flashcardSetRequestDto.getDescription())
                .isPublic(flashcardSetRequestDto.getIsPublic() != null ? flashcardSetRequestDto.getIsPublic() : false)
                .flashcardType(flashcardType)
                .category(category)
                .user(user)
                .flashcards(new ArrayList<>()) // Initialize empty list
                .build();

        FlashcardSetEntity savedFlashcardSet = flashcardSetRepository.save(flashcardSet);

        if (flashcardSetRequestDto.getFlashcards() != null && !flashcardSetRequestDto.getFlashcards().isEmpty()) {
            // Validate flashcards based on type
            for (FlashcardRequestDto flashcard : flashcardSetRequestDto.getFlashcards()) {
                validateFlashcardByType(flashcard, flashcardType);
            }

            List<FlashcardEntity> flashcards = flashcardSetRequestDto.getFlashcards().stream()
                    .map(dto -> convertToEntity(dto, savedFlashcardSet, user, flashcardType))
                    .collect(Collectors.toList());

            List<FlashcardEntity> savedFlashcards = flashcardRepository.saveAll(flashcards);
            savedFlashcardSet.getFlashcards().addAll(savedFlashcards);
        }

        return convertToResponseDto(savedFlashcardSet);
    }

    @Override
    public PaginatedResponse<FlashcardSetResponseDto> getAllFlashcardSets(Long userId, Pageable pageable, String search, String sortBy) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Page<FlashcardSetEntity> flashcardSetsPage;

        boolean sortByTitle = "title".equalsIgnoreCase(sortBy);

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm theo title only
            if (sortByTitle) {
                flashcardSetsPage = flashcardSetRepository.findByUserAndTitleContainingIgnoreCaseOrderByTitleAsc(
                    user, search.trim(), pageable);
            } else {
                // Mặc định hoặc sortBy=recent -> sort theo createdAt desc
                flashcardSetsPage = flashcardSetRepository.findByUserAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                    user, search.trim(), pageable);
            }
        } else {
            // Không có search
            if (sortByTitle) {
                flashcardSetsPage = flashcardSetRepository.findByUserOrderByTitleAsc(user, pageable);
            } else {
                // Mặc định hoặc sortBy=recent -> sort theo createdAt desc
                flashcardSetsPage = flashcardSetRepository.findByUserOrderByCreatedAtDesc(user, pageable);
            }
        }

        Page<FlashcardSetResponseDto> responsePage = flashcardSetsPage.map(this::convertToResponseDto);
        return PaginatedResponse.fromPage(responsePage);
    }

    @Override
    public PaginatedResponse<FlashcardSetResponseDto> getPublicFlashcardSets(Pageable pageable, String search, String sortBy) {
        Page<FlashcardSetEntity> publicFlashcardSetsPage;

        boolean sortByTitle = "title".equalsIgnoreCase(sortBy);

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm flashcard sets công khai theo title only
            if (sortByTitle) {
                publicFlashcardSetsPage = flashcardSetRepository.findByIsPublicTrueAndTitleContainingIgnoreCaseOrderByTitleAsc(
                    search.trim(), pageable);
            } else {
                // Mặc định hoặc sortBy=recent -> sort theo createdAt desc
                publicFlashcardSetsPage = flashcardSetRepository.findByIsPublicTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                    search.trim(), pageable);
            }
        } else {
            // Không có search
            if (sortByTitle) {
                publicFlashcardSetsPage = flashcardSetRepository.findByIsPublicTrueOrderByTitleAsc(pageable);
            } else {
                // Mặc định hoặc sortBy=recent -> sort theo createdAt desc
                publicFlashcardSetsPage = flashcardSetRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
            }
        }

        Page<FlashcardSetResponseDto> responsePage = publicFlashcardSetsPage.map(this::convertToResponseDto);
        return PaginatedResponse.fromPage(responsePage);
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

        // Update isPublic field if provided
        if (flashcardSetRequestDto.getIsPublic() != null) {
            flashcardSet.setIsPublic(flashcardSetRequestDto.getIsPublic());
        }

        if (flashcardSetRequestDto.getCategoryId() != null) {
            FlashcardCategoryEntity category = categoryRepository.findById(flashcardSetRequestDto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + flashcardSetRequestDto.getCategoryId()));
            flashcardSet.setCategory(category);
        }

        // Update flashcards only if provided
        if (flashcardSetRequestDto.getFlashcards() != null) {
            // Validate flashcards based on current type
            FlashcardType currentType = flashcardSet.getFlashcardType();
            for (FlashcardRequestDto flashcard : flashcardSetRequestDto.getFlashcards()) {
                validateFlashcardByType(flashcard, currentType);
            }

            // Delete existing flashcards
            flashcardRepository.deleteAll(flashcardSet.getFlashcards());
            flashcardSet.getFlashcards().clear();

            // Add new flashcards if list is not empty
            if (!flashcardSetRequestDto.getFlashcards().isEmpty()) {
                List<FlashcardEntity> newFlashcards = flashcardSetRequestDto.getFlashcards().stream()
                        .map(dto -> convertToEntity(dto, flashcardSet, user, currentType))
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

    // -------------------- PRIVATE HELPER METHODS --------------------

    // Validate flashcard based on its type
    private void validateFlashcardByType(FlashcardRequestDto flashcard, FlashcardType type) {
        if (type == FlashcardType.QUESTIONS) {
            validateQuestionsFlashcard(flashcard);
        } else if (type == FlashcardType.VOCABULARY) {
            validateVocabularyFlashcard(flashcard);
        }
    }

    // Validate QUESTIONS type flashcard
    private void validateQuestionsFlashcard(FlashcardRequestDto flashcard) {
        if (flashcard.getQuestion() == null || flashcard.getQuestion().trim().isEmpty()) {
            throw new BadRequestException("Question is required for QUESTIONS type");
        }
        if (flashcard.getChoices() == null || flashcard.getChoices().size() < 2) {
            throw new BadRequestException("At least 2 choices are required for QUESTIONS type");
        }
        if (flashcard.getCorrectAnswer() == null || flashcard.getCorrectAnswer() < 0 ||
                flashcard.getCorrectAnswer() >= flashcard.getChoices().size()) {
            throw new BadRequestException("Valid correct answer index is required for QUESTIONS type");
        }
    }

    // Validate VOCABULARY type flashcard
    private void validateVocabularyFlashcard(FlashcardRequestDto flashcard) {
        if (flashcard.getVocabulary() == null || flashcard.getVocabulary().trim().isEmpty()) {
            throw new BadRequestException("Vocabulary is required for VOCABULARY type");
        }
        if (flashcard.getMeaning() == null || flashcard.getMeaning().trim().isEmpty()) {
            throw new BadRequestException("Meaning is required for VOCABULARY type");
        }
    }

    private FlashcardSetResponseDto convertToResponseDto(FlashcardSetEntity entity) {
        List<FlashcardResponseDto> flashcardDtos = new ArrayList<>();

        if (entity.getFlashcards() != null) {
            flashcardDtos = entity.getFlashcards().stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        }

        // Convert category to DTO if exists
        FlashcardCategoryResponseDto categoryDto = null;
        if (entity.getCategory() != null) {
            categoryDto = convertCategoryToDto(entity.getCategory());
        }

        return FlashcardSetResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isPublic(entity.getIsPublic())
                .flashcardType(entity.getFlashcardType())
                .category(categoryDto)
                .createdAt(entity.getCreatedAt())
                .user(convertUserToDto(entity.getUser()))
                .flashcards(flashcardDtos)
                .build();
    }

    private FlashcardCategoryResponseDto convertCategoryToDto(FlashcardCategoryEntity category) {
        return FlashcardCategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
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
        FlashcardType type = entity.getFlashcardSet().getFlashcardType();

        FlashcardResponseDto.FlashcardResponseDtoBuilder b = FlashcardResponseDto.builder()
                .id(entity.getId())
                .explanation(entity.getExplanation());

        if (type == FlashcardType.QUESTIONS) {
            b.question(entity.getQuestion())
                    .choices(entity.getChoices())
                    .correctAnswer(entity.getCorrectAnswer());
        } else {
            b.vocabulary(entity.getVocabulary())
                    .meaning(entity.getMeaning())
                    .example(entity.getExample());
        }
        return b.build();
    }

    private FlashcardEntity convertToEntity(FlashcardRequestDto dto, FlashcardSetEntity flashcardSet, UserEntity user, FlashcardType type) {
        FlashcardEntity.FlashcardEntityBuilder builder = FlashcardEntity.builder()
                .flashcardSet(flashcardSet)
                .user(user)
                .explanation(dto.getExplanation());

        if (type == FlashcardType.QUESTIONS) {
            builder.question(dto.getQuestion())
                    .choices(dto.getChoices())
                    .correctAnswer(dto.getCorrectAnswer())
                    .vocabulary(null)
                    .meaning(null)
                    .example(null);
        } else {
            builder.vocabulary(dto.getVocabulary())
                    .meaning(dto.getMeaning())
                    .example(dto.getExample())
                    .question(null)
                    .choices(null)
                    .correctAnswer(null);
        }
        return builder.build();
    }
}

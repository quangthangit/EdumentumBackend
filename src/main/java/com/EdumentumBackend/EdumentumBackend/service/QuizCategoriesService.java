package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCategoriesEntity;
import com.EdumentumBackend.EdumentumBackend.repository.QuizCategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizCategoriesService {
    @Autowired
    private QuizCategoriesRepository quizCategoriesRepository;

    private QuizCategoriesResponseDto toResponseDto(QuizCategoriesEntity entity) {
        return QuizCategoriesResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private QuizCategoriesEntity toEntity(QuizCategoriesRequestDto dto) {
        return QuizCategoriesEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    public List<QuizCategoriesResponseDto> getAllActiveCategories() {
        return quizCategoriesRepository.findByIsActiveTrue().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public QuizCategoriesResponseDto createCategory(QuizCategoriesRequestDto requestDto) {
        QuizCategoriesEntity entity = toEntity(requestDto);
        QuizCategoriesEntity savedEntity = quizCategoriesRepository.save(entity);
        return toResponseDto(savedEntity);
    }

    public QuizCategoriesResponseDto getCategoryById(Long id) {
        Optional<QuizCategoriesEntity> entity = quizCategoriesRepository.findById(id);
        return entity.map(this::toResponseDto).orElse(null);
    }

    public QuizCategoriesResponseDto updateCategory(Long id, QuizCategoriesRequestDto requestDto) {
        Optional<QuizCategoriesEntity> optionalEntity = quizCategoriesRepository.findById(id);
        if (optionalEntity.isPresent()) {
            QuizCategoriesEntity entity = optionalEntity.get();
            entity.setName(requestDto.getName());
            entity.setDescription(requestDto.getDescription());
            entity.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : entity.getIsActive());
            QuizCategoriesEntity savedEntity = quizCategoriesRepository.save(entity);
            return toResponseDto(savedEntity);
        }
        return null;
    }

    public boolean deleteCategory(Long id) {
        if (quizCategoriesRepository.existsById(id)) {
            quizCategoriesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<QuizCategoriesResponseDto> getAllCategories() {
        return quizCategoriesRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}

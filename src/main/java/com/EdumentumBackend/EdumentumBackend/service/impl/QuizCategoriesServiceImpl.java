package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCategoriesEntity;
import com.EdumentumBackend.EdumentumBackend.repository.QuizCategoriesRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizCategoriesServiceImpl implements QuizCategoriesService {
    
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

    @Override
    public QuizCategoriesResponseDto createCategory(QuizCategoriesRequestDto requestDto) {
        QuizCategoriesEntity entity = toEntity(requestDto);
        QuizCategoriesEntity savedEntity = quizCategoriesRepository.save(entity);
        return toResponseDto(savedEntity);
    }

    @Override
    public List<QuizCategoriesResponseDto> getAllCategories() {
        List<QuizCategoriesEntity> categories = quizCategoriesRepository.findAll();
        return categories.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizCategoriesResponseDto getCategoryById(Long id) {
        Optional<QuizCategoriesEntity> categoryOpt = quizCategoriesRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        return toResponseDto(categoryOpt.get());
    }

    @Override
    public QuizCategoriesResponseDto updateCategory(Long id, QuizCategoriesRequestDto requestDto) {
        Optional<QuizCategoriesEntity> categoryOpt = quizCategoriesRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        QuizCategoriesEntity category = categoryOpt.get();
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        if (requestDto.getIsActive() != null) {
            category.setIsActive(requestDto.getIsActive());
        }

        QuizCategoriesEntity savedEntity = quizCategoriesRepository.save(category);
        return toResponseDto(savedEntity);
    }

    @Override
    public boolean deleteCategory(Long id) {
        Optional<QuizCategoriesEntity> categoryOpt = quizCategoriesRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            return false;
        }
        quizCategoriesRepository.deleteById(id);
        return true;
    }

    @Override
    public List<QuizCategoriesResponseDto> getActiveCategories() {
        List<QuizCategoriesEntity> activeCategories = quizCategoriesRepository.findByIsActiveTrue();
        return activeCategories.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}

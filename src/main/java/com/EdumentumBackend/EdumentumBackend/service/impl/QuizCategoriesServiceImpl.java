package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizCategoriesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder implementation for QuizCategoriesService.
 * This implementation returns empty results as categories have been deprecated.
 */
@Service
public class QuizCategoriesServiceImpl implements QuizCategoriesService {

    @Override
    public QuizCategoriesResponseDto createCategory(QuizCategoriesRequestDto requestDto) {
        // Return null as categories are deprecated
        return null;
    }

    @Override
    public List<QuizCategoriesResponseDto> getAllCategories() {
        // Return empty list as categories are deprecated
        return new ArrayList<>();
    }

    @Override
    public QuizCategoriesResponseDto getCategoryById(Long id) {
        // Return null as categories are deprecated
        return null;
    }

    @Override
    public QuizCategoriesResponseDto updateCategory(Long id, QuizCategoriesRequestDto requestDto) {
        // Return null as categories are deprecated
        return null;
    }

    @Override
    public boolean deleteCategory(Long id) {
        // Return false as categories are deprecated
        return false;
    }

    @Override
    public List<QuizCategoriesResponseDto> getActiveCategories() {
        // Return empty list as categories are deprecated
        return new ArrayList<>();
    }
}

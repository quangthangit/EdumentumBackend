package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;

import java.util.List;

public interface QuizCategoriesService {

    QuizCategoriesResponseDto createCategory(QuizCategoriesRequestDto requestDto);

    List<QuizCategoriesResponseDto> getAllCategories();

    QuizCategoriesResponseDto getCategoryById(Long id);

    QuizCategoriesResponseDto updateCategory(Long id, QuizCategoriesRequestDto requestDto);

    boolean deleteCategory(Long id);

    List<QuizCategoriesResponseDto> getActiveCategories();
}

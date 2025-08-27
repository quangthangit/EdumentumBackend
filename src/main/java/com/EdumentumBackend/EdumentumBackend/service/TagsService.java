package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.TagsEntity;

import java.util.List;

public interface TagsService {
    TagResponseDto createTag(TagRequestDto tagRequestDto);
    TagResponseDto getOrCreateTag(TagRequestDto tagRequestDto);
    TagResponseDto getTagById(Long id);
    TagResponseDto getTagByName(String name);
    List<TagResponseDto> getAllTags();
    boolean existsByName(String name);
}

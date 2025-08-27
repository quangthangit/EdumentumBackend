package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.TagsEntity;
import com.EdumentumBackend.EdumentumBackend.repository.TagsRepository;
import com.EdumentumBackend.EdumentumBackend.service.TagsService;
import com.EdumentumBackend.EdumentumBackend.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagsServiceImpl implements TagsService {

    @Autowired
    private TagsRepository tagsRepository;

    @Override
    public TagResponseDto createTag(TagRequestDto tagRequestDto) {
        // Check if tag already exists
        if (tagsRepository.existsByNameIgnoreCase(tagRequestDto.getName())) {
            return getTagByName(tagRequestDto.getName());
        }

        // Create new tag
        TagsEntity tag = TagsEntity.builder()
                .name(tagRequestDto.getName())
                .slug(SlugUtil.toSlug(tagRequestDto.getName()))
                .description(tagRequestDto.getDescription())
                .isActive(true)
                .usageCount(0)
                .popularityScore(BigDecimal.ZERO)
                .quizCount(0)
                .totalAttempts(0)
                .avgSuccessRate(BigDecimal.ZERO)
                .build();

        TagsEntity savedTag = tagsRepository.save(tag);
        return mapToResponseDto(savedTag);
    }

    @Override
    public TagResponseDto getOrCreateTag(TagRequestDto tagRequestDto) {
        // If tag has ID, try to find it
        if (tagRequestDto.getId() != null) {
            Optional<TagsEntity> existingTag = tagsRepository.findById(tagRequestDto.getId());
            if (existingTag.isPresent()) {
                return mapToResponseDto(existingTag.get());
            }
        }

        // Try to find by name
        if (tagRequestDto.getName() != null && !tagRequestDto.getName().trim().isEmpty()) {
            Optional<TagsEntity> existingTag = tagsRepository.findByNameIgnoreCase(tagRequestDto.getName());
            if (existingTag.isPresent()) {
                return mapToResponseDto(existingTag.get());
            }

            // If tag doesn't exist, create it
            return createTag(tagRequestDto);
        }

        throw new RuntimeException("Tag must have either an ID or a name");
    }

    @Override
    public TagResponseDto getTagById(Long id) {
        Optional<TagsEntity> tag = tagsRepository.findById(id);
        return tag.map(this::mapToResponseDto)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    @Override
    public TagResponseDto getTagByName(String name) {
        Optional<TagsEntity> tag = tagsRepository.findByNameIgnoreCase(name);
        return tag.map(this::mapToResponseDto)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));
    }

    @Override
    public List<TagResponseDto> getAllTags() {
        return tagsRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return tagsRepository.existsByNameIgnoreCase(name);
    }

    private TagResponseDto mapToResponseDto(TagsEntity entity) {
        return TagResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .build();
    }
}

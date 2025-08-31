package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.TagRequestDto;
import com.EdumentumBackend.EdumentumBackend.entity.TagsEntity;
import com.EdumentumBackend.EdumentumBackend.exception.ResourceNotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.TagsRepository;
import com.EdumentumBackend.EdumentumBackend.utils.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagsRepository tagsRepository;
    private final SlugGenerator slugGenerator;

    /**
     * Find tag by ID or create new tag if name is provided
     */
    @Transactional
    public TagsEntity findOrCreateTag(TagRequestDto tagDto) {

        if (tagDto.getId() != null) {
            return tagsRepository.findById(tagDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagDto.getId()));
        }


        if (tagDto.getName() != null && !tagDto.getName().trim().isEmpty()) {
            String normalizedName = tagDto.getName().trim();
            return tagsRepository.findByNameIgnoreCase(normalizedName)
                    .orElseGet(() -> {
                        String slug = slugGenerator.generateSlug(normalizedName);

                        TagsEntity newTag = TagsEntity.builder()
                                .name(normalizedName)
                                .slug(slug)
                                .description(tagDto.getDescription())
//                                .color(tagDto.getColor())
                                .isActive(true)
                                .build();

                        return tagsRepository.save(newTag);
                    });
        }

        throw new IllegalArgumentException("Tag must have either id or name");
    }

    /**
     * Search tags by name (partial match)
     */
    public List<TagsEntity> searchTagsByName(String query) {
        return tagsRepository.findByNameContainingIgnoreCase(query);
    }

    /**
     * Get all tags
     */
    public List<TagsEntity> getAllTags() {
        return tagsRepository.findAll();
    }
}
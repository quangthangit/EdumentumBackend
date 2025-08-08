package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MindMapRequestDto {

    @NotBlank(message = "Mind map name is required")
    @Size(min = 1, max = 255, message = "Mind map name must be between 1 and 255 characters")
    private String name;

    @NotNull(message = "Mind map data is required")
    private MindMapDataDto data;

    @NotNull(message = "Mind map type is required")
    private MindMapType type;

    // Custom validation method
    public boolean isValidType() {
        return type != null && (type == MindMapType.STUDY_NOTES ||
                type == MindMapType.PROJECT_PLANNING ||
                type == MindMapType.CONCEPT_MAPPING ||
                type == MindMapType.BRAINSTORMING ||
                type == MindMapType.LESSON_PLAN ||
                type == MindMapType.RESEARCH ||
                type == MindMapType.PRESENTATION ||
                type == MindMapType.PERSONAL);
    }
}

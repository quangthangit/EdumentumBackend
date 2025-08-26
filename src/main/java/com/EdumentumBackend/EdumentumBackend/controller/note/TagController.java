package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.entity.TagEntity;
import com.EdumentumBackend.EdumentumBackend.repository.TagRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<TagEntity>> list() {
        return ResponseEntity.ok(tagRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<TagEntity> create(@RequestParam @NotBlank String name) {
        return ResponseEntity.ok(tagRepository.save(TagEntity.builder().name(name).build()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagEntity> rename(@PathVariable Long id, @RequestParam @NotBlank String name) {
        TagEntity tag = tagRepository.findById(id).orElseThrow();
        tag.setName(name);
        return ResponseEntity.ok(tagRepository.save(tag));
    }
}



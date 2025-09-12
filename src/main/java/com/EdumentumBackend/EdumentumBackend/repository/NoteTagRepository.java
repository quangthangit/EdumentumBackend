package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteTagRepository extends JpaRepository<NoteTagEntity, Long> {
    List<NoteTagEntity> findByNote(NoteEntity note);
    List<NoteTagEntity> findByTagName(String tagName);
    boolean existsByNoteAndTagName(NoteEntity note, String tagName);
    void deleteByNoteAndTagName(NoteEntity note, String tagName);
}

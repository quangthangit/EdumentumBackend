package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteTagEntity;
import com.EdumentumBackend.EdumentumBackend.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteTagRepository extends JpaRepository<NoteTagEntity, Long> {
    List<NoteTagEntity> findByNote(NoteEntity note);
    List<NoteTagEntity> findByTag(TagEntity tag);
    boolean existsByNoteAndTag(NoteEntity note, TagEntity tag);
    void deleteByNoteAndTag(NoteEntity note, TagEntity tag);
}



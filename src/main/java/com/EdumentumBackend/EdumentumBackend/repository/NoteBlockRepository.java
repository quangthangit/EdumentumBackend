package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteBlockEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteBlockRepository extends JpaRepository<NoteBlockEntity, Long> {
    List<NoteBlockEntity> findByNoteAndIsDeletedFalseOrderByOrderIndexAsc(NoteEntity note);
}



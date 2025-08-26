package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteVersionRepository extends JpaRepository<NoteVersionEntity, Long> {
    List<NoteVersionEntity> findByNoteOrderByCreatedAtDesc(NoteEntity note);
}



package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteCommentEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteCommentRepository extends JpaRepository<NoteCommentEntity, Long> {
    List<NoteCommentEntity> findByNoteAndIsDeletedFalseOrderByCreatedAtAsc(NoteEntity note);
}



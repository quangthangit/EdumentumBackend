package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.MindMapNodeEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindMapNodeRepository extends JpaRepository<MindMapNodeEntity, Long> {
    List<MindMapNodeEntity> findByNote(NoteEntity note);

    List<MindMapNodeEntity> findByNoteIdAndParentIsNull(Long noteId);
}

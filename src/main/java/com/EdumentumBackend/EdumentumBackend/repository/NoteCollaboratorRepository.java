package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteCollaboratorEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteCollaboratorRepository extends JpaRepository<NoteCollaboratorEntity, Long> {
    List<NoteCollaboratorEntity> findByNote(NoteEntity note);
    Optional<NoteCollaboratorEntity> findByNoteAndUser(NoteEntity note, UserEntity user);
    boolean existsByNoteAndUserAndPermissionIn(NoteEntity note, UserEntity user, List<NotePermission> permissions);
}



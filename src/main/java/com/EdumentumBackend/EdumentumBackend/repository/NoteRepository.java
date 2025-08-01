package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByUser(UserEntity user);
}
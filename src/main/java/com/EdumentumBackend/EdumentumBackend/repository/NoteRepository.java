package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    @Query("SELECT n FROM NoteEntity n WHERE n.isDeleted = false AND (n.owner = :user OR EXISTS (SELECT 1 FROM NoteCollaboratorEntity c WHERE c.note = n AND c.user = :user))")
    Page<NoteEntity> findAccessibleNotes(@Param("user") UserEntity user, Pageable pageable);

    @Query("SELECT n FROM NoteEntity n WHERE n.id = :id AND n.isDeleted = false")
    Optional<NoteEntity> findActiveById(@Param("id") Long id);

    @Query("SELECT DISTINCT n FROM NoteEntity n " +
            "LEFT JOIN NoteTagEntity nt ON nt.note = n " +
            "WHERE n.isDeleted = false " +
            "AND (n.owner = :user OR EXISTS (SELECT 1 FROM NoteCollaboratorEntity c WHERE c.note = n AND c.user = :user)) " +
            "AND (:ownerId IS NULL OR n.owner.userId = :ownerId) " +
            "AND (:query IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:tag IS NULL OR nt.tagName = :tag)")
    Page<NoteEntity> searchAccessibleNotes(@Param("user") UserEntity user,
                                           @Param("query") String query,
                                           @Param("ownerId") Long ownerId,
                                           @Param("tag") String tag,
                                           Pageable pageable);
}

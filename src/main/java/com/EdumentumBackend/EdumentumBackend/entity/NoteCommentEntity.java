package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "note_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private NoteEntity note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private NoteBlockEntity block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NoteCommentEntity parent;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}



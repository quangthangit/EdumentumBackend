package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "note_tags",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"note_id", "tag_name"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private NoteEntity note;

    @Column(name = "tag_name", nullable = false)
    private String tagName;
}


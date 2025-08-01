package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MindMapNodeEntity> nodes = new ArrayList<>();
}
package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "folders")
@Data
public class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private RepositoryEntity repository;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}


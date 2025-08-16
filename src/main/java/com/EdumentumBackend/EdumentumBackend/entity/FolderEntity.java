package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "folders")
@Getter
@Setter
public class FolderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity groupEntity;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}


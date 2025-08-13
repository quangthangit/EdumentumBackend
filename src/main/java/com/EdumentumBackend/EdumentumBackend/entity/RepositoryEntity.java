package com.EdumentumBackend.EdumentumBackend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "repositories")
public class RepositoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    private List<FolderEntity> folders;
}

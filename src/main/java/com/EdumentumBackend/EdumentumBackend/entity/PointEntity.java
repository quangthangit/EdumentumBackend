package com.EdumentumBackend.EdumentumBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "points")
public class PointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int point = 0;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}

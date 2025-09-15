package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.enums.ToDoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "simple_todos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameTask;

    @Enumerated(EnumType.STRING)
    private ToDoStatus status = ToDoStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        this.creationAt = LocalDateTime.now();
    }
}

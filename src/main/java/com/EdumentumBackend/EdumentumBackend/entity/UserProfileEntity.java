package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.entity.listener.UserProfileEntityListener;
import com.EdumentumBackend.EdumentumBackend.enums.LevelProgress;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(UserProfileEntityListener.class)
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    private int streak;
    private int maxStreak;

    @Enumerated(EnumType.STRING)
    private LevelProgress levelProgress;

    private int totalStudyTime;
    private int totalQuizzesCreated;
    private int totalQuizzesCompleted;
    private int totalFlashCardCreated;
    private int totalFlashCardCompleted;
    private int totalAttendance;
}

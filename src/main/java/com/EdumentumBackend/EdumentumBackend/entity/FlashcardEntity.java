package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @ElementCollection
    @CollectionTable(name = "flashcard_choices", joinColumns = @JoinColumn(name = "flashcard_id"))
    @Column(name = "choice", columnDefinition = "TEXT")
    private List<String> choices;

    @Column(name = "correct_answer")
    private Integer correctAnswer;

    @Column(name = "vocabulary")
    private String vocabulary;

    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;

    @Column(name = "example", columnDefinition = "TEXT")
    private String example;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_set_id", nullable = false)
    private FlashcardSetEntity flashcardSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}

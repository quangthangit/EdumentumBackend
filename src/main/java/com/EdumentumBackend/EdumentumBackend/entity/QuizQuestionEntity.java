//package com.EdumentumBackend.EdumentumBackend.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import jakarta.validation.constraints.NotBlank;
//
//
//@Entity
//@Table(name = "quizzes_questions")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class QuizQuestionEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long quizQuestionId;
//
//    @OneToOne(mappedBy = "quizQuestion")
//    @JsonIgnore
//    private QuizEntity quiz;
//
//    @Lob
//    @Column(columnDefinition = "TEXT")
//    @NotBlank(message = "Questions field cannot be blank")
//    private String questions;
//}

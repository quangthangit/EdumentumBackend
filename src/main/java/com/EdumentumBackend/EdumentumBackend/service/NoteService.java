package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.NoteRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.NoteResponseDto;

import java.util.List;

public interface NoteService {
    NoteResponseDto createNote(NoteRequestDto dto, Long userId);

    NoteResponseDto updateNote(Long id, NoteRequestDto dto, Long userId);

    void deleteNote(Long id, Long userId);

    NoteResponseDto getNoteById(Long id, Long userId);

    List<NoteResponseDto> getNotesByUserId(Long userId);
}
package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.NoteRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.NoteResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.NoteRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public NoteResponseDto createNote(NoteRequestDto dto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        NoteEntity note = NoteEntity.builder()
                .title(dto.getTitle())
                .user(user)
                .build();

        NoteEntity saved = noteRepository.save(note);
        return toDto(saved);
    }

    @Override
    public NoteResponseDto updateNote(Long id, NoteRequestDto dto, Long userId) {
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        // Check if user owns this note
        if (!note.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Note not found or access denied");
        }

        note.setTitle(dto.getTitle());
        return toDto(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long id, Long userId) {
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        // Check if user owns this note
        if (!note.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Note not found or access denied");
        }

        noteRepository.delete(note);
    }

    @Override
    public NoteResponseDto getNoteById(Long id, Long userId) {
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        // Check if user owns this note
        if (!note.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Note not found or access denied");
        }

        return toDto(note);
    }

    @Override
    public List<NoteResponseDto> getNotesByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<NoteEntity> notes = noteRepository.findByUser(user);
        return notes.stream().map(this::toDto).collect(Collectors.toList());
    }

    private NoteResponseDto toDto(NoteEntity note) {
        return NoteResponseDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .userId(note.getUser().getUserId())
                .build();
    }
}
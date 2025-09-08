package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.UserProfileEntity;
import com.EdumentumBackend.EdumentumBackend.event.*;
import com.EdumentumBackend.EdumentumBackend.repository.UserProfileRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class UserProfileEventListener {

    private final UserProfileRepository userProfileRepository;

    private void updateProfileCounter(Long userId, Consumer<UserProfileEntity> updater) {
        UserProfileEntity profile = userProfileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        updater.accept(profile);
        userProfileRepository.save(profile);
    }

    @EventListener
    @Transactional
    public void handleAttendanceCreated(AttendanceCreatedEvent event) {
        updateProfileCounter(event.getUserId(), profile -> {
            profile.setTotalAttendance(profile.getTotalAttendance() + 1);

            if (event.getLoggedYesterday()) {
                profile.setStreak(profile.getStreak() + 1);
            } else {
                profile.setStreak(1);
            }

            if (profile.getStreak() > profile.getMaxStreak()) {
                profile.setMaxStreak(profile.getStreak());
            }
        });
    }

    @EventListener
    @Transactional
    public void handleQuizCreated(QuizCreatedEvent event) {
        updateProfileCounter(event.getUserId(),
                profile -> profile.setTotalQuizzesCreated(profile.getTotalQuizzesCreated() + 1));
    }

    @EventListener
    @Transactional
    public void handleFlashCardCreated(FlashCardCreatedEvent event) {
        updateProfileCounter(event.getUserId(),
                profile -> profile.setTotalFlashCardCreated(profile.getTotalFlashCardCreated() + 1));
    }

    @EventListener
    @Transactional
    public void handleQuizComplete(QuizCompletedEvent event) {
        updateProfileCounter(event.getUserId(),
                profile -> profile.setTotalQuizzesCompleted(profile.getTotalQuizzesCompleted() + 1));
    }

    @EventListener
    @Transactional
    public void handleFlashCardComplete(FlashCardCompletedEvent event) {
        updateProfileCounter(event.getUserId(),
                profile -> profile.setTotalFlashCardCompleted(profile.getTotalFlashCardCompleted() + 1));
    }
}

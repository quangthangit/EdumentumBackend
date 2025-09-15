package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.dtos.user.UserProfileRequestDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.FirebaseStorageService;
import com.EdumentumBackend.EdumentumBackend.service.UserProfileService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final FirebaseStorageService firebaseStorageService;
    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping()
    public ResponseEntity<?> getUserProfileById() {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Get User Profile successfully",
                    "data", userProfileService.getUserProfileInfo(userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/attendance")
    public ResponseEntity<?> getAttendance() {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Get attandence Profile successfully",
                    "data", userProfileService.findAllByUserId(userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestParam(value = "imageUrl", required = false) MultipartFile imageUrl,
            @RequestParam(value = "bannerUrl", required = false) MultipartFile bannerUrl,
            @RequestParam(value = "username", required = false) String username
    ) {
        try {
            Long userId = getCurrentUserId();
            UserProfileRequestDto userRequestDto = new UserProfileRequestDto();
            userRequestDto.setUsername(username);
            String imageLink = null;
            String bannerLink = null;

            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageLink = firebaseStorageService.uploadFileAsync(imageUrl).get();
                userRequestDto.setImageUrl(imageLink);

            }

            if (bannerUrl != null && !bannerUrl.isEmpty()) {
                bannerLink = firebaseStorageService.uploadFileAsync(bannerUrl).get();
                userRequestDto.setBannerUrl(bannerLink);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Update User Profile successfully",
                    "data", userService .updateUserProfile(userRequestDto,userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }



    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthorized");
        }

        return userDetails.getId();
    }

    private ResponseEntity<?> buildServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "error", "Internal server error: " + e.getMessage()
        ));
    }
}

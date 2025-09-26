package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.controller.base.BaseController;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.entity.SubscriptionEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import com.EdumentumBackend.EdumentumBackend.repository.SubscriptionRepository;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/student/subscription")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentSubscriptionController extends BaseController {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    @Override
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userService.getUserByEmail(email);
        return user.getUserId();
    }

    @PostMapping("/payment/confirm")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            Long userId = getCurrentUserId();
            
            // Extract payment information
            String packageId = (String) paymentData.get("packageId");
            String paymentMethod = (String) paymentData.get("paymentMethod");
            String transactionId = (String) paymentData.get("transactionId");
            
            // Validate required fields
            if (packageId == null || paymentMethod == null) {
                return errorResponse("Missing required payment information", 400);
            }
            
            // Determine subscription plan based on package ID
            SubscriptionPlan planType;
            LocalDateTime endDate;
            LocalDateTime startDate = LocalDateTime.now();
            
            switch (packageId) {
                case "PRO_MONTHLY":
                    planType = SubscriptionPlan.PRO_MONTHLY;
                    endDate = startDate.plusMonths(1);
                    break;
                case "PRO_YEARLY":
                    planType = SubscriptionPlan.PRO_YEARLY;
                    endDate = startDate.plusYears(1);
                    break;
                default:
                    return errorResponse("Invalid package ID: " + packageId, 400);
            }
            
            // Deactivate any existing active subscriptions for this user
            Optional<SubscriptionEntity> existingSubscription = subscriptionRepository.findActiveSubscriptionByUserId(userId);
            if (existingSubscription.isPresent()) {
                SubscriptionEntity existing = existingSubscription.get();
                existing.setIsActive(false);
                subscriptionRepository.save(existing);
            }
            
            // Create new subscription
            SubscriptionEntity subscription = SubscriptionEntity.builder()
                .userId(userId)
                .planType(planType)
                .startDate(startDate)
                .endDate(endDate)
                .isActive(true)
                .paymentMethod(paymentMethod)
                .transactionId(transactionId)
                .build();
            
            SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
            
            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("subscriptionId", savedSubscription.getId());
            result.put("planType", savedSubscription.getPlanType());
            result.put("startDate", savedSubscription.getStartDate());
            result.put("endDate", savedSubscription.getEndDate());
            result.put("isActive", savedSubscription.getIsActive());
            
            return successResponse(result, "Payment confirmed and subscription activated successfully");
        } catch (Exception e) {
            return errorResponse("Failed to process payment: " + e.getMessage(), 500);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionStatus() {
        try {
            Long userId = getCurrentUserId();
            
            // Get user's active subscription
            Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findActiveSubscriptionByUserId(userId);
            
            Map<String, Object> result = new HashMap<>();
            if (subscriptionOpt.isPresent()) {
                SubscriptionEntity subscription = subscriptionOpt.get();
                result.put("hasActiveSubscription", true);
                result.put("planType", subscription.getPlanType());
                result.put("startDate", subscription.getStartDate());
                result.put("endDate", subscription.getEndDate());
                result.put("isActive", subscription.getIsActive());
            } else {
                result.put("hasActiveSubscription", false);
                result.put("planType", "FREE");
            }
            
            return successResponse(result, "Subscription status retrieved successfully");
        } catch (Exception e) {
            return errorResponse("Failed to retrieve subscription status: " + e.getMessage(), 500);
        }
    }
}
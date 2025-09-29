package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.controller.base.BaseController;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.entity.SubscriptionEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import com.EdumentumBackend.EdumentumBackend.repository.SubscriptionRepository;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.EdumentumBackend.EdumentumBackend.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/subscription")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentSubscriptionController extends BaseController {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final VNPayService vnPayService;

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
            List<SubscriptionEntity> activeSubscriptions = subscriptionRepository.findActiveSubscriptionsByUserId(userId);
            for (SubscriptionEntity subscription : activeSubscriptions) {
                subscription.setIsActive(false);
                subscriptionRepository.save(subscription);
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
    
    @PostMapping("/payment/vnpay/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createVNPayPayment(@RequestBody Map<String, Object> paymentData, HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            String packageId = (String) paymentData.get("packageId");

            if (packageId == null) {
                return errorResponse("Missing required payment information", 400);
            }
            String origin = request.getHeader("Origin");
            if (origin == null || origin.isEmpty()) {
                origin = "http://localhost:3000";
            }
            
            System.out.println("Request Origin: " + origin);
            long amount = 0;
            String orderInfo = "";
            
            switch (packageId) {
                case "PRO_MONTHLY":
                    amount = 50000; // 50,000 VND for monthly
                    orderInfo = "Thanh toan goi Pro thang";
                    break;
                case "PRO_YEARLY":
                    amount = 300000; // 300,000 VND for yearly
                    orderInfo = "Thanh toan goi Pro nam";
                    break;
                default:
                    return errorResponse("Invalid package ID: " + packageId, 400);
            }
            
            System.out.println("Creating VNPay payment for user " + userId + " with amount: " + amount);
            
            // Create VNPay payment URL
            String paymentUrl = vnPayService.createOrder(amount, orderInfo, origin);
            
            System.out.println("Generated payment URL: " + paymentUrl);
            
            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("paymentUrl", paymentUrl);
            result.put("packageId", packageId);
            
            return successResponse(result, "VNPay payment URL created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse("Failed to create VNPay payment: " + e.getMessage(), 500);
        }
    }
    
    @GetMapping("/payment/vnpay/callback")
    public RedirectView handleVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            System.out.println("VNPay callback received with params: " + params);
            
            // Log all parameters for debugging
            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println("Param: " + entry.getKey() + " = " + entry.getValue());
            }
            
            // Process the callback
            int paymentStatus = vnPayService.orderReturn(params);
            
            String packageId = params.get("vnp_OrderInfo") != null && params.get("vnp_OrderInfo").contains("nam") ? "PRO_YEARLY" : "PRO_MONTHLY";
            
            if (paymentStatus == 1) {
                // Payment successful - redirect to success page
                System.out.println("VNPay payment successful");
                return new RedirectView("http://localhost:3000/payment/success?vnpay=success&packageId=" + packageId);
            } else {
                // Payment failed - redirect to failure page
                System.out.println("VNPay payment failed with status: " + paymentStatus);
                // Add more details to the failure redirect for debugging
                StringBuilder failureParams = new StringBuilder();
                failureParams.append("vnpay=failed");
                failureParams.append("&status=").append(paymentStatus);
                if (params.containsKey("vnp_ResponseCode")) {
                    failureParams.append("&responseCode=").append(params.get("vnp_ResponseCode"));
                }
                if (params.containsKey("vnp_TransactionStatus")) {
                    failureParams.append("&transactionStatus=").append(params.get("vnp_TransactionStatus"));
                }
                return new RedirectView("http://localhost:3000/payment/failure?" + failureParams.toString());
            }
        } catch (Exception e) {
            System.err.println("Error processing VNPay callback: " + e.getMessage());
            e.printStackTrace();
            return new RedirectView("http://localhost:3000/payment/failure?vnpay=error&message=" + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionStatus() {
        try {
            Long userId = getCurrentUserId();
            
            // Get user's active subscription
            List<SubscriptionEntity> activeSubscriptions = subscriptionRepository.findActiveSubscriptionsByUserId(userId);
            
            Map<String, Object> result = new HashMap<>();
            if (!activeSubscriptions.isEmpty()) {
                // Use the most recent active subscription
                SubscriptionEntity subscription = activeSubscriptions.get(0);
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
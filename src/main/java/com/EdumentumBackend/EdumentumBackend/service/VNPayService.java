package com.EdumentumBackend.EdumentumBackend.service;

import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {
    
    private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_TmnCode = "VY1Z922S"; // Your TMN Code from VNPay
    private final String vnp_HashSecret = "SEW8GW9OQP3TUMPG30K8A3JH1B7RQY60"; // Your Hash Secret from VNPay

    public String createOrder(long total, String orderInfor, String baseUrl) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = orderInfor;
        String orderType = "other";

        // VNPay expects amount in VND (smallest unit without decimals)
        long amount = total;
        String bankCode = "NCB"; // Default bank code

        String vnp_TxnRef = VNPayService.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", this.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", baseUrl + "/api/v1/student/subscription/payment/vnpay/callback");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        vnp_Params.put("vnp_BankCode", bankCode);

        // Remove any null or empty values
        vnp_Params.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());

        // Build data to hash and query - CRITICAL: Sort by key name
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    // CRITICAL: Hash data must NOT be URL encoded
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue);
                    
                    // Query parameters MUST be URL encoded
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                
                if (i != fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        // CRITICAL: Use the correct hash secret and data
        String vnp_SecureHash = hmacSHA512(this.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = this.vnp_PayUrl + "?" + queryUrl;
        
        // Log for debugging
        System.out.println("VNPay Payment URL: " + paymentUrl);
        System.out.println("Hash Data: " + hashData.toString());
        System.out.println("Secure Hash: " + vnp_SecureHash);
        
        return paymentUrl;
    }

    public int orderReturn(Map<String, String> fields) {
        // Extract the secure hash from the fields
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        
        if (vnp_SecureHash == null) {
            System.out.println("No vnp_SecureHash found in callback");
            return -1; // Invalid signature
        }
        
        // Remove hash fields from the data to be validated
        Map<String, String> dataToHash = new HashMap<>(fields);
        dataToHash.remove("vnp_SecureHash");
        dataToHash.remove("vnp_SecureHashType");
        
        // Create the hash data string (without URL encoding)
        List<String> fieldNames = new ArrayList<>(dataToHash.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = dataToHash.get(fieldName);
            
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append('=');
                sb.append(fieldValue);
                
                if (i != fieldNames.size() - 1) {
                    sb.append('&');
                }
            }
        }
        
        String hashData = sb.toString();
        
        // Generate the hash using our secret
        String signValue = hmacSHA512(this.vnp_HashSecret, hashData);
        
        // Log for debugging
        System.out.println("Callback Hash Data: " + hashData);
        System.out.println("Expected Signature: " + vnp_SecureHash);
        System.out.println("Generated Signature: " + signValue);
        
        // Compare the hashes (case insensitive)
        if (signValue.equalsIgnoreCase(vnp_SecureHash)) {
            if ("00".equals(fields.get("vnp_TransactionStatus"))) {
                return 1; // Success
            } else {
                return 0; // Failed
            }
        } else {
            System.out.println("Signature mismatch:");
            System.out.println("Expected: " + vnp_SecureHash);
            System.out.println("Actual: " + signValue);
            System.out.println("HashData: " + hashData);
            return -1; // Invalid signature
        }
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
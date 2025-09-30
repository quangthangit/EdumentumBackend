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
    private final String vnp_TmnCode = "VY1Z922S"; // TMN Code
    private final String vnp_HashSecret = "14QLIZGFXIQLSHJIY7E7NDJEUZ4QU8T8"; // Hash Secret

    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";

    public String createOrder(long total, String orderInfor, String baseUrl, String userId) {
        // Encode userId in the transaction reference
        String vnp_TxnRef = getRandomNumber(8) + "_" + userId;
        String vnp_IpAddr = "127.0.0.1";
        String orderType = "other";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNP_VERSION);
        vnp_Params.put("vnp_Command", VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", this.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", "https://edumentumbackend-production.up.railway.app/api/v1/student/subscription/payment/vnpay/callback");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp và build hash data + query như code mẫu
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    // Sử dụng US_ASCII encoding như code mẫu
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(this.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = this.vnp_PayUrl + "?" + queryUrl;

        // Log debug
        System.out.println("VNPay Payment URL: " + paymentUrl);
        System.out.println("Hash Data (request): " + hashData);
        System.out.println("Secure Hash (request): " + vnp_SecureHash);
        System.out.println("Transaction Ref (with userId): " + vnp_TxnRef);

        return paymentUrl;
    }

    // Overloaded method for backward compatibility
    public String createOrder(long total, String orderInfor, String baseUrl) {
        return createOrder(total, orderInfor, baseUrl, "1");
    }

    public int orderReturn(Map<String, String> fields) {
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            System.out.println("No vnp_SecureHash found in callback");
            return -1;
        }

        // Tạo bản sao để xử lý - không encode ở đây vì đã được encode từ request
        Map<String, String> fieldsToHash = new HashMap<>();

        // Chỉ loại trừ vnp_SecureHash và vnp_SecureHashType
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (!fieldName.equals("vnp_SecureHash") && !fieldName.equals("vnp_SecureHashType")) {
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fieldsToHash.put(fieldName, fieldValue);
                }
            }
        }

        // Sử dụng hashAllFields như code mẫu
        String signValue = hashAllFields(fieldsToHash);

        // Log debug
        System.out.println("Expected Signature (from VNPay): " + vnp_SecureHash);
        System.out.println("Generated Signature (server):   " + signValue);

        if (signValue.equals(vnp_SecureHash)) {
            // Chữ ký ok -> check trạng thái giao dịch
            return "00".equals(fields.get("vnp_TransactionStatus")) ? 1 : 0;
        } else {
            System.out.println("Signature mismatch!");
            return -1;
        }
    }

    public String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    sb.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    sb.append("=");
                    sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(this.vnp_HashSecret, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            // Sử dụng getBytes() thay vì getBytes(StandardCharsets.UTF_8) như code mẫu
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
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

package com.codesupreme.couriersub.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EpointService {

    private final String publicKey;
    private final String privateKey;
    private final String requestUrl;

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestClient rest;
    private final String resultUrl;

    public EpointService(
            @Value("${epoint.publicKey}") String publicKey,
            @Value("${epoint.privateKey}") String privateKey,
            @Value("${epoint.requestUrl:https://epoint.az/api/1/request}") String requestUrl,
            @Value("${epoint.resultUrl}") String resultUrl
    ) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.requestUrl = requestUrl;
        this.resultUrl = resultUrl;

        this.rest = RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** Bizim sistem üçün orderId */
    public String createProviderOrderId() {
        return "ord_" + UUID.randomUUID();
    }

    /** ePoint request edib redirect_url qaytarır */
    public String initPaymentRedirectUrl(String orderId, int amountInt, String successUrl, String errorUrl, String description) {
        try {
            double amount = amountInt / 100.0;

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("public_key", publicKey);
            dataMap.put("amount", String.format("%.2f", amount));
            dataMap.put("currency", "AZN");
            dataMap.put("language", "az");
            dataMap.put("order_id", orderId);
            dataMap.put("description", (description == null || description.isBlank()) ? "Aylıq abunəlik (5 AZN)" : description);
            dataMap.put("result_url", resultUrl);
            dataMap.put("success_redirect_url", successUrl);
            dataMap.put("error_redirect_url", errorUrl);

            String json = mapper.writeValueAsString(dataMap);
            String dataEncoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

            String signature = sign(privateKey, dataEncoded);

            Map<String, String> postBody = new HashMap<>();
            postBody.put("data", dataEncoded);
            postBody.put("signature", signature);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = rest.post()
                    .uri(requestUrl)
                    .body(postBody)
                    .retrieve()
                    .body(Map.class);

            if (resp == null) throw new IllegalArgumentException("ePoint cavabı boşdur");

            Object st = resp.get("status");
            if (st != null && "success".equalsIgnoreCase(String.valueOf(st))) {
                Object redirect = resp.get("redirect_url");
                if (redirect == null) throw new IllegalArgumentException("ePoint redirect_url qaytarmadı");
                return String.valueOf(redirect);
            }

            // error detayı da qaytaraq
            throw new IllegalArgumentException("ePoint init error: " + resp);
        } catch (Exception e) {
            throw new IllegalArgumentException("ePoint init xətası: " + e.getMessage());
        }
    }

    private String sign(String privateKey, String dataEncoded) throws Exception {
        String rawSignature = privateKey + dataEncoded + privateKey;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1 = md.digest(rawSignature.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sha1);
    }

    public boolean verifySignature(String dataEncoded, String signatureFromEpoint) {
        try {
            String expected = sign(privateKey, dataEncoded);
            // base64 signature string müqayisə
            return expected.equals(signatureFromEpoint);
        } catch (Exception e) {
            return false;
        }
    }

    public DecodedResult decodeResult(String dataEncoded) {
        try {
            byte[] decoded = Base64.getDecoder().decode(dataEncoded);
            String json = new String(decoded, StandardCharsets.UTF_8);

            JsonNode node = mapper.readTree(json);

            DecodedResult r = new DecodedResult();
            r.rawJson = json;

            // ⚠️ ePoint-in real result JSON strukturu fərqli ola bilər.
            // Ona görə bir neçə ehtimalı yoxlayırıq:
            r.orderId = firstNonNull(node, "order_id", "orderId", "order");
            r.status  = firstNonNull(node, "status", "payment_status", "result");

            // transaction id müxtəlif adlarla gələ bilər
            r.transactionId = firstNonNull(node, "transaction", "transaction_id", "transactionId", "rrn");

            if (r.orderId == null) throw new IllegalArgumentException("Result-də order_id tapılmadı: " + json);
            if (r.status == null) throw new IllegalArgumentException("Result-də status tapılmadı: " + json);

            return r;
        } catch (Exception e) {
            throw new IllegalArgumentException("Result decode xətası: " + e.getMessage());
        }
    }

    private String firstNonNull(JsonNode node, String... keys) {
        for (String k : keys) {
            JsonNode v = node.get(k);
            if (v != null && !v.isNull()) {
                String s = v.asText();
                if (s != null && !s.isBlank()) return s;
            }
        }
        return null;
    }

    public static class DecodedResult {
        public String orderId;
        public String status;        // SUCCESS / FAILED kimi map edəcəyik
        public String transactionId;
        public String rawJson;
    }
}

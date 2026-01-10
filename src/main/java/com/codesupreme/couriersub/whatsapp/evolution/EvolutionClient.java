package com.codesupreme.couriersub.whatsapp.evolution;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class EvolutionClient {

    private final RestClient rest;
    private final String apiKey;
    private final String instance;
    private final String sendTextPath;

    public EvolutionClient(
            @Value("${evolution.baseUrl}") String baseUrl,
            @Value("${evolution.apiKey:}") String apiKey,
            @Value("${evolution.instance}") String instance,
            @Value("${evolution.sendTextPath}") String sendTextPath
    ) {
        this.apiKey = apiKey;
        this.instance = instance;
        this.sendTextPath = sendTextPath;

        RestClient.Builder b = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // apiKey boş deyilsə göndər
        if (StringUtils.hasText(apiKey)) {
            b.defaultHeader("apikey", apiKey);
        }

        this.rest = b.build();
    }

    public void sendText(String toJid, String text) {
        Map<String, Object> body = Map.of(
                "number", toJid,
                "text", text
        );

        rest.post()
                .uri(sendTextPath, instance)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}

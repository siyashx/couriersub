package com.codesupreme.couriersub.whatsapp.group;

import com.codesupreme.couriersub.common.util.PhoneUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class WhatsAppGroupService {

    private final RestClient rest;
    private final String instance;
    private final String groupJid;

    public WhatsAppGroupService(
            @Value("${evolution.baseUrl}") String baseUrl,
            @Value("${evolution.apiKey:}") String apiKey,
            @Value("${evolution.instance}") String instance,
            @Value("${evolution.groupJid}") String groupJid
    ) {
        this.instance = instance;
        this.groupJid = groupJid;

        RestClient.Builder b = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (StringUtils.hasText(apiKey)) {
            b.defaultHeader("apikey", apiKey);
        }

        this.rest = b.build();
    }

    public void addToGroup(String phone) {
        updateParticipant("add", phone);
    }

    public void removeFromGroup(String phone) {
        updateParticipant("remove", phone);
    }

    private void updateParticipant(String action, String phone) {
        String normalized = PhoneUtil.normalize(phone);

        Map<String, Object> body = Map.of(
                "action", action,
                "participants", List.of(normalized)
        );

        rest.post()
                .uri("/group/updateParticipant/{instance}?groupJid={groupJid}", instance, groupJid)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}

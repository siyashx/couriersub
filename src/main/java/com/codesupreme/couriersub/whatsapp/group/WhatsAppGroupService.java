package com.codesupreme.couriersub.whatsapp.group;

import com.codesupreme.couriersub.common.util.PhoneUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.*;

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

    public boolean isParticipantInGroup(String phone) {
        String normalized = PhoneUtil.normalize(phone); // "99455...."
        String waJid = normalized + "@s.whatsapp.net";

        Map<String, Object> resp = fetchParticipantsRaw();
        if (resp == null || resp.isEmpty()) return false;

        Set<String> ids = extractParticipantIds(resp);

        // 1) normal jid match
        if (ids.stream().anyMatch(x -> x.equalsIgnoreCase(waJid))) return true;

        // 2) some APIs may return plain "994..." or "055..."
        return ids.stream().anyMatch(x -> x.equalsIgnoreCase(normalized));
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchParticipantsRaw() {
        Object obj = rest.get()
                .uri("/group/participants/{instance}?groupJid={groupJid}", instance, groupJid)
                .retrieve()
                .body(Object.class);

        if (obj instanceof Map<?, ?> m) return (Map<String, Object>) m;
        if (obj instanceof List<?> list) return Map.of("participants", list);

        return Map.of();
    }

    /**
     * Supports response shapes:
     * - {"participants":[ "994..@s.whatsapp.net", ... ]}
     * - {"response":{"participants":[...]}}
     * - {"data":{"participants":[...]}}
     * - {"participants":[{"id":".."},{"participantAlt":"994..@s.whatsapp.net"}]}
     */
    @SuppressWarnings("unchecked")
    private Set<String> extractParticipantIds(Map<String, Object> resp) {
        Object root = unwrap(resp);

        List<Object> participants = null;

        if (root instanceof Map<?, ?> map) {
            Object p = ((Map<String, Object>) map).get("participants");
            if (p instanceof List<?> list) participants = (List<Object>) list;
        } else if (root instanceof List<?> list) {
            participants = (List<Object>) list;
        }

        if (participants == null) return Set.of();

        Set<String> out = new HashSet<>();
        for (Object item : participants) {
            if (item == null) continue;

            if (item instanceof String s) {
                out.add(s.trim());
                continue;
            }

            if (item instanceof Map<?, ?> m0) {
                Map<String, Object> m = (Map<String, Object>) m0;

                addIfString(out, m.get("id"));
                addIfString(out, m.get("jid"));
                addIfString(out, m.get("participant"));
                addIfString(out, m.get("participantAlt")); // lid mode üçün çox vacib
                addIfString(out, m.get("wid"));

                Object key = m.get("key");
                if (key instanceof Map<?, ?> km) {
                    addIfString(out, ((Map<String, Object>) km).get("participantAlt"));
                    addIfString(out, ((Map<String, Object>) km).get("participant"));
                }
            }
        }
        return out;
    }

    private Object unwrap(Map<String, Object> resp) {
        Object r = resp.get("response");
        if (r != null) return r;
        Object d = resp.get("data");
        if (d != null) return d;
        return resp;
    }

    private void addIfString(Set<String> out, Object v) {
        if (v instanceof String s && !s.isBlank()) out.add(s.trim());
    }
}

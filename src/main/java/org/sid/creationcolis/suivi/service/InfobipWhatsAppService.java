package org.sid.creationcolis.suivi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class InfobipWhatsAppService {

    @Value("${infobip.api.key}")
    private String apiKey;

    @Value("${infobip.whatsapp.from}")
    private String from;

    @Value("${infobip.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public InfobipWhatsAppService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendWhatsAppMessage(String to, String message) {
        String url = baseUrl + "/whatsapp/1/message/text";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "App " + apiKey);
        headers.set("Content-Type", "application/json");

        String payload = String.format(
                "{\"from\":\"%s\",\"to\":\"whatsapp:%s\",\"content\":{\"text\":\"%s\"}}",
                from, to, message
        );

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send WhatsApp message: " + response.getBody());
        }
    }
}

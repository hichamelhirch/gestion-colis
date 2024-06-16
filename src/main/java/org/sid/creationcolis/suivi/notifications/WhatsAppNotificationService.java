package org.sid.creationcolis.suivi.notifications;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;



@Service
public class WhatsAppNotificationService {

    @Value("${infobip.base.url}")
    private String infobipApiUrl;

    @Value("${infobip.api.key}")
    private String infobipApiKey;

    @Value("${infobip.whatsapp.from}")
    private String from;

    public void sendWhatsAppMessage(String to, String message) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = String.format("{\"messages\":[{\"from\":\"%s\",\"to\":\"%s\",\"messageId\":\"4be039cc-876e-4b2a-bc07-8eb9a7574124\",\"content\":{\"templateName\":\"message_test\",\"templateData\":{\"body\":{\"placeholders\":[\"%s\"]}},\"language\":\"en\"}}]}",
                from, to, message);
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(infobipApiUrl + "/whatsapp/1/message/template")
                .method("POST", body)
                .addHeader("Authorization", "App " + infobipApiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println(response.body().string());
        }
    }
}

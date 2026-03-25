package com.smartqueue.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAssistantService {

    @Value("${huggingface.api.key:PLACEHOLDER}")
    private String apiKey;

    @Value("${huggingface.model.url:https://router.huggingface.co/v1/chat/completions}")
    private String modelUrl;

    @Value("${huggingface.model.name:meta-llama/Llama-3.1-8B-Instruct}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAiResponse(String userMessage) {
        if ("PLACEHOLDER".equals(apiKey)) {
            return "Hello! I am your MediQueue Hospital Assistant. Please configure my Hugging Face API key to enable my full medical assistance capabilities. For now, I'm providing this automated response. How can I help you today?";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.7);

            List<Map<String, String>> messages = new ArrayList<>();

            // System Prompt
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "You are a professional medical assistant for the MediQueue Hospital Patient Portal. " +
                    "Help patients with consultations, OPD timings, and hospital navigation. " +
                    "Be professional, empathetic, and efficient.");
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(modelUrl, entity, Map.class);

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            return "Connection error: I'm currently unable to reach the AI service. Details: " + e.getMessage();
        }

        return "I'm not sure how to respond to that, but I'm here for you!";
    }
}

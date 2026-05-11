package lk.ijse.springbootbackend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Service
public class OllamaService {

    private final RestClient restClient;

    @Value("${ollama.api.url}")
    private String ollamaUrl;

    @Value("${ollama.model.name}")
    private String modelName;

    public OllamaService() {
        this.restClient = RestClient.builder().build();
    }

    public String getInterviewResponse(String systemPrompt, String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", modelName,
                "prompt", String.format("%s\n\nUser Input: %s", systemPrompt, userMessage),
                "stream", false
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri(ollamaUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return Optional.ofNullable(response)
                    .map(res -> (String) res.get("response"))
                    .map(this::cleanDeepSeekResponse)
                    .orElse("AI produced an empty response.");

        } catch (Exception e) {
            System.err.println("Ollama Service Error: " + e.getMessage());
            return "Interview Service is currently unavailable. Please ensure your local AI engine is running.";
        }
    }
    
    private String cleanDeepSeekResponse(String rawResponse) {
        return rawResponse.replaceAll("(?s)<think>.*?</think>", "").trim();
    }
}
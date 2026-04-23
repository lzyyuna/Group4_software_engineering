package com.group4.tarecruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group4.tarecruitment.model.Admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class AIWorkloadService {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL = "deepseek-chat";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIWorkloadService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeWorkload(List<Admin> workloadData, double threshold) throws Exception {
        String prompt = buildPrompt(workloadData, threshold);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", MODEL);
        requestBody.put("max_tokens", 600);

        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content",
            "You are a TA workload analyst. Be brief and direct. Use plain text only, no markdown.");

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        String requestJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorNode = objectMapper.readTree(response.body());
            String errorMsg = errorNode.path("error").path("message").asText(response.body());
            throw new RuntimeException("API error (" + response.statusCode() + "): " + errorMsg);
        }

        JsonNode responseJson = objectMapper.readTree(response.body());
        return responseJson.path("choices").get(0).path("message").path("content").asText();
    }

    private String buildPrompt(List<Admin> workloadData, double threshold) {
        long overloadedCount = workloadData.stream().filter(a -> a.getExcessAmount() > 0).count();
        double totalWorkload = workloadData.stream().mapToDouble(Admin::getTotalWorkload).sum();
        double avgWorkload = workloadData.isEmpty() ? 0 : totalWorkload / workloadData.size();

        StringBuilder sb = new StringBuilder();
        sb.append("Threshold: ").append(threshold).append("h | TAs: ").append(workloadData.size())
          .append(" | Overloaded: ").append(overloadedCount)
          .append(String.format(" | Avg: %.1fh%n%n", avgWorkload));

        for (Admin a : workloadData) {
            sb.append(a.getTaName())
              .append(" | ").append(a.getCourseName())
              .append(" | Total: ").append(a.getTotalWorkload()).append("h");
            if (a.getExcessAmount() > 0) {
                sb.append(String.format(" [OVER +%.1fh]", a.getExcessAmount()));
            }
            sb.append("\n");
        }

        sb.append("\nReply using EXACTLY this format (plain text, no markdown):\n");
        sb.append("ASSESSMENT: [1-2 sentences on overall distribution health]\n");
        sb.append("AT-RISK:\n- [TA name: brief reason] (list up to 3)\n");
        sb.append("ACTIONS:\n- [concrete step] (list up to 3 steps)\n");
        sb.append("VERDICT: [ACCEPTABLE / NEEDS ATTENTION / CRITICAL]");

        return sb.toString();
    }

    /**
     * Resolves the DeepSeek API key using three sources in priority order:
     *   1. DEEPSEEK_API_KEY environment variable
     *   2. config/api_keys.properties file (property: deepseek.api.key)
     *   3. config/api_keys.properties.example file (fallback, property: deepseek.api.key)
     */
    public static String getApiKeyFromEnv() {
        String key = System.getenv("DEEPSEEK_API_KEY");
        if (key != null && !key.isBlank()) return key;

        // Try the real properties file first
        key = readKeyFromFile("config/api_keys.properties");
        if (key != null) return key;

        // Fall back to the .example file
        key = readKeyFromFile("config/api_keys.properties.example");
        if (key != null) return key;

        return null;
    }

    private static String readKeyFromFile(String path) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
            String key = props.getProperty("deepseek.api.key", "").trim();
            if (!key.isBlank()) return key;
        } catch (IOException ignored) {}
        return null;
    }
}
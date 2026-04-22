package com.group4.tarecruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group4.tarecruitment.model.Admin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

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
        requestBody.put("max_tokens", 1024);

        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a university TA workload analysis assistant.");

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
        sb.append("Analyze the following TA workload data and provide actionable recommendations.\n\n");

        sb.append("=== CONFIGURATION ===\n");
        sb.append("Weekly workload threshold: ").append(threshold).append(" hours\n");
        sb.append("Total TAs: ").append(workloadData.size()).append("\n");
        sb.append("Overloaded TAs: ").append(overloadedCount).append("\n");
        sb.append(String.format("Average workload: %.1f hours%n%n", avgWorkload));

        sb.append("=== TA WORKLOAD DATA ===\n");
        for (Admin a : workloadData) {
            sb.append("- ").append(a.getTaName())
              .append(" (").append(a.getTaId()).append(")")
              .append(" | Course: ").append(a.getCourseName())
              .append(" | Dept: ").append(a.getDepartment())
              .append(" | Weekly: ").append(a.getWeeklyWorkload()).append("h")
              .append(" | Total: ").append(a.getTotalWorkload()).append("h");
            if (a.getExcessAmount() > 0) {
                sb.append(String.format(" | OVERLOADED +%.1fh", a.getExcessAmount()));
            }
            sb.append("\n");
        }

        sb.append("\n=== ANALYSIS REQUESTED ===\n");
        sb.append("Please provide:\n");
        sb.append("1. Overall Assessment: Is the workload distribution healthy?\n");
        sb.append("2. At-Risk TAs: List TAs at risk of burnout with specific concerns.\n");
        sb.append("3. Recommendations: Concrete steps for workload redistribution.\n");
        sb.append("4. Verdict: ACCEPTABLE / NEEDS ATTENTION / CRITICAL\n\n");
        sb.append("Keep the response clear and concise. Use plain text without markdown.");

        return sb.toString();
    }

    public static String getApiKeyFromEnv() {
        return System.getenv("DEEPSEEK_API_KEY");
    }
}
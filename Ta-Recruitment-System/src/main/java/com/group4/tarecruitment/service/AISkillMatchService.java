package com.group4.tarecruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.SkillMatchResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AISkillMatchService {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL = "deepseek-chat";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AISkillMatchService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeSkillMatch(Applicant applicant, Job job, SkillMatchResult result) throws Exception {
        String prompt = buildPrompt(applicant, job, result);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", MODEL);
        requestBody.put("max_tokens", 800);

        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a university TA skill-matching assistant. " +
                        "You must explain the fit between an applicant and a TA job based only on the structured data provided. " +
                        "Do not invent any skills or experiences. Use plain English without markdown.");

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

    private String buildPrompt(Applicant applicant, Job job, SkillMatchResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("Analyze the fit between this TA applicant and this job.\n\n");

        sb.append("=== APPLICANT ===\n");
        sb.append("TA ID: ").append(applicant != null ? applicant.getTaId() : "").append("\n");
        sb.append("Name: ").append(applicant != null ? safe(applicant.getName()) : "").append("\n");
        sb.append("Skill Tags: ").append(applicant != null ? safe(applicant.getSkillTags()) : "").append("\n");
        sb.append("Courses: ").append(applicant != null ? safe(applicant.getCourses()) : "").append("\n\n");

        sb.append("=== JOB ===\n");
        sb.append("Job ID: ").append(job != null ? job.getJobId() : "").append("\n");
        sb.append("Course Name: ").append(job != null ? safe(job.getCourseName()) : "").append("\n");
        sb.append("Position Type: ").append(job != null ? safe(job.getPositionType()) : "").append("\n");
        sb.append("Skill Requirements: ").append(job != null ? safe(job.getSkillRequirements()) : "").append("\n");
        sb.append("Job Content: ").append(job != null ? safe(job.getJobContent()) : "").append("\n\n");

        sb.append("=== STRUCTURED MATCH RESULT ===\n");
        sb.append("Match Score: ").append(String.format("%.1f", result.getMatchScore())).append("%\n");
        sb.append("Recommendation Level: ").append(safe(result.getRecommendationLevel())).append("\n");
        sb.append("Matched Skills: ").append(result.getMatchedSkills()).append("\n");
        sb.append("Missing Skills: ").append(result.getMissingSkills()).append("\n\n");

        sb.append("=== TASK ===\n");
        sb.append("Write a short explanation in 3 parts:\n");
        sb.append("1. Why the applicant is or is not a good fit.\n");
        sb.append("2. Which skills already match.\n");
        sb.append("3. Which skills are missing and whether the applicant should apply.\n");
        sb.append("Keep it concise.");

        return sb.toString();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    public static String getApiKeyFromEnv() {
        return System.getenv("DEEPSEEK_API_KEY");
    }
}
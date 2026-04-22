package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.SkillMatchResult;
import com.group4.tarecruitment.service.AISkillMatchService;
import com.group4.tarecruitment.service.JobService;
import com.group4.tarecruitment.service.SkillMatchService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class JobDetailView {
    private final Stage stage;
    private final Applicant applicant;
    private final Job job;
    private final JobService jobService = new JobService();
    private final SkillMatchService skillMatchService = new SkillMatchService();

    public JobDetailView(Stage stage, Applicant applicant, Job job) {
        this.stage = stage;
        this.applicant = applicant;
        this.job = job;
    }

    public Parent createContent() {
        Label title = new Label("Job Details");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label courseTitle = new Label(job.getCourseName());
        courseTitle.setFont(new Font(14));
        courseTitle.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        String labelStyle = "-fx-font-size: 13px; -fx-text-fill: #2c3e50;";
        infoBox.getChildren().addAll(
                createRow("Job ID: ", job.getJobId(), labelStyle),
                createRow("Course Name: ", job.getCourseName(), labelStyle),
                createRow("Position Type: ", job.getPositionType(), labelStyle),
                createRow("Weekly Workload: ", job.getWeeklyWorkload() + " hours", labelStyle),
                createRow("MO in Charge: ", job.getMoName(), labelStyle),
                createRow("Release Time: ", job.getReleaseTime(), labelStyle),
                createRow("Deadline: ", job.getDeadline(), labelStyle),
                createRow("Skill Requirements: ", job.getSkillRequirements(), labelStyle),
                createRow("Job Content: ", job.getJobContent(), labelStyle)
        );

        SkillMatchResult matchResult = skillMatchService.match(applicant, job);

        VBox matchBox = new VBox(8);
        matchBox.setPadding(new Insets(15));
        matchBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label matchTitle = new Label("Your Skill Match Analysis");
        matchTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label scoreLabel = new Label("Match Score: " + String.format("%.1f", matchResult.getMatchScore()) + "%");
        scoreLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label matchedLabel = new Label("Matched Skills: " + formatList(matchResult.getMatchedSkills()));
        matchedLabel.setStyle(labelStyle);

        Label missingLabel = new Label("Missing Skills: " + formatList(matchResult.getMissingSkills()));
        missingLabel.setStyle(labelStyle);

        Label levelLabel = new Label("Recommendation: " + matchResult.getRecommendationLevel());
        levelLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e44ad; -fx-font-weight: bold;");

        Button aiAnalyzeBtn = new Button("AI Analyze My Fit");
        aiAnalyzeBtn.setStyle("-fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; "
                + "-fx-font-weight: bold; -fx-background-color: #9b59b6; -fx-text-fill: white;");

        TextArea aiExplanationArea = new TextArea();
        aiExplanationArea.setPromptText("AI explanation will appear here...");
        aiExplanationArea.setWrapText(true);
        aiExplanationArea.setEditable(false);
        aiExplanationArea.setPrefRowCount(6);

        matchBox.getChildren().addAll(
                matchTitle,
                scoreLabel,
                matchedLabel,
                missingLabel,
                levelLabel,
                aiAnalyzeBtn,
                aiExplanationArea
        );

        Button applyBtn = new Button("Apply for This Position");
        Button backBtn = new Button("Back to Job List");
        Button backToHomeBtn = new Button("Back to TA Home");

        String btnStyle = "-fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-font-weight: bold;";
        applyBtn.setStyle(btnStyle + "-fx-background-color: #27ae60; -fx-text-fill: white;");
        backBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        backToHomeBtn.setStyle(btnStyle + "-fx-background-color: #95a5a6; -fx-text-fill: white;");

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        applyBtn.setOnAction(e -> {
            try {
                String appId = jobService.submitApplication(applicant.getTaId(), job.getJobId());
                if (appId == null) {
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;");
                    resultLabel.setText("❌ You have already applied for this position.");
                } else {
                    resultLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: bold;");
                    resultLabel.setText("✅ Application submitted successfully. Application ID: " + appId);
                    applyBtn.setDisable(true);
                }
            } catch (Exception ex) {
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;");
                resultLabel.setText("❌ Application failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        aiAnalyzeBtn.setOnAction(e -> runAiAnalysis(matchResult, aiExplanationArea));

        backBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
            stage.setTitle("Available TA Positions");
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        HBox btnBox = new HBox(15, applyBtn, backBtn, backToHomeBtn);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(12, title, courseTitle, infoBox, matchBox, btnBox, resultLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f6fa;");
        root.setAlignment(Pos.TOP_LEFT);

        return root;
    }

    private void runAiAnalysis(SkillMatchResult matchResult, TextArea aiExplanationArea) {
        String apiKey = AISkillMatchService.getApiKeyFromEnv();

        if (apiKey == null || apiKey.isBlank()) {
            TextInputDialog keyDialog = new TextInputDialog();
            keyDialog.setTitle("API Key Required");
            keyDialog.setHeaderText("Enter your DeepSeek API Key");
            keyDialog.setContentText("API Key:");
            var result = keyDialog.showAndWait();
            if (result.isEmpty() || result.get().isBlank()) {
                return;
            }
            apiKey = result.get().trim();
        }

        final String finalApiKey = apiKey;

        Stage loadingStage = new Stage();
        loadingStage.initOwner(stage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("AI Skill Match Analysis");

        ProgressIndicator spinner = new ProgressIndicator();
        Label loadingLabel = new Label("Analyzing your fit with AI...");
        VBox loadingBox = new VBox(15, spinner, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(30));
        loadingStage.setScene(new Scene(loadingBox, 320, 160));
        loadingStage.show();

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                AISkillMatchService aiService = new AISkillMatchService(finalApiKey);
                return aiService.analyzeSkillMatch(applicant, job, matchResult);
            }
        };

        task.setOnSucceeded(ev -> {
            loadingStage.close();
            aiExplanationArea.setText(task.getValue());
        });

        task.setOnFailed(ev -> {
            loadingStage.close();
            Throwable ex = task.getException();
            aiExplanationArea.setText("AI analysis failed: "
                    + (ex != null && ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String formatList(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return "None";
        }
        return String.join(", ", list);
    }

    private HBox createRow(String label, String value, String style) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);

        Label l1 = new Label(label);
        l1.setStyle(style + "-fx-font-weight: bold;");

        Label l2 = new Label(value == null ? "" : value);
        l2.setStyle(style);

        row.getChildren().addAll(l1, l2);
        return row;
    }
}
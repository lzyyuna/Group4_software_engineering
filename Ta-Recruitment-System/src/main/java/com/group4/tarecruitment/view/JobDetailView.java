package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class JobDetailView {
    private final Stage stage;
    private final Applicant applicant;
    private final Job job;
    private final JobService jobService = new JobService();

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
        infoBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

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

        VBox btnBox = new VBox(8, applyBtn, backBtn, backToHomeBtn);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(12, title, courseTitle, infoBox, btnBox, resultLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f6fa;");
        root.setAlignment(Pos.TOP_LEFT);

        return root;
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
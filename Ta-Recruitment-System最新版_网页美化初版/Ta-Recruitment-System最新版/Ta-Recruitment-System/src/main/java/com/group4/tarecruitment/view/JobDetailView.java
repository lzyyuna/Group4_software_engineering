package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
        Label title = new Label(job.getCourseName());
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Review the position information below before submitting your application.");
        subtitle.getStyleClass().add("page-subtitle");

        VBox infoBox = new VBox(10,
                detailLabel("Position ID", job.getJobId()),
                detailLabel("Position Type", job.getPositionType()),
                detailLabel("Weekly Workload", job.getWeeklyWorkload() + " hours"),
                detailLabel("Module Organizer", job.getMoName()),
                detailLabel("Release Time", job.getReleaseTime()),
                detailLabel("Deadline", job.getDeadline()),
                detailLabel("Skill Requirements", job.getSkillRequirements()),
                detailLabel("Job Content", job.getJobContent())
        );
        infoBox.getStyleClass().add("info-card");

        Button applyBtn = new Button("Apply for This Position");
        applyBtn.getStyleClass().add("success-button");
        Button backBtn = new Button("Back to Position List");
        backBtn.getStyleClass().add("secondary-button");

        Label resultLabel = new Label();
        resultLabel.getStyleClass().add("muted-label");
        resultLabel.setWrapText(true);

        applyBtn.setOnAction(e -> {
            try {
                String appId = jobService.submitApplication(applicant.getTaId(), job.getJobId());
                resultLabel.getStyleClass().removeAll("status-success", "status-error", "muted-label");
                if (appId == null) {
                    resultLabel.getStyleClass().add("status-error");
                    resultLabel.setText("You have already applied for this position.");
                } else {
                    resultLabel.getStyleClass().add("status-success");
                    resultLabel.setText("Application submitted successfully. Application ID: " + appId);
                    applyBtn.setDisable(true);
                }
            } catch (Exception ex) {
                resultLabel.getStyleClass().add("status-error");
                resultLabel.setText("Application failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> stage.getScene().setRoot(new JobListView(stage, applicant).createContent()));

        VBox card = new VBox(18, title, subtitle, infoBox, applyBtn, backBtn, resultLabel);
        card.getStyleClass().add("card");
        card.setMaxWidth(860);

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(32));
        return root;
    }

    private VBox detailLabel(String label, String value) {
        Label name = new Label(label);
        name.getStyleClass().add("form-label");
        Label content = new Label(value == null ? "" : value);
        content.getStyleClass().add("value-label");
        content.setWrapText(true);
        return new VBox(4, name, content);
    }
}

package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.ProfileController;
import com.group4.tarecruitment.model.Applicant;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileDetailView {
    private final Applicant applicant;
    private final Stage stage;

    public ProfileDetailView(Applicant applicant, Stage stage) {
        this.applicant = applicant;
        this.stage = stage;
    }

    public Parent getView() {
        ProfileController controller = new ProfileController();

        Label title = new Label("Profile Details");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Review your saved information, manage your resume, and continue to available positions.");
        subtitle.getStyleClass().add("page-subtitle");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(12);
        infoGrid.setVgap(14);
        infoGrid.add(rowLabel("TA ID"), 0, 0);
        infoGrid.add(valueLabel(applicant.getTaId()), 1, 0);
        infoGrid.add(rowLabel("Student ID"), 0, 1);
        infoGrid.add(valueLabel(applicant.getStudentId()), 1, 1);
        infoGrid.add(rowLabel("Name"), 0, 2);
        infoGrid.add(valueLabel(applicant.getName()), 1, 2);
        infoGrid.add(rowLabel("Email"), 0, 3);
        infoGrid.add(valueLabel(applicant.getEmail()), 1, 3);
        infoGrid.add(rowLabel("Courses"), 0, 4);
        infoGrid.add(valueLabel(applicant.getCourses()), 1, 4);
        infoGrid.add(rowLabel("Skill Tags"), 0, 5);
        infoGrid.add(valueLabel(applicant.getSkillTags()), 1, 5);
        infoGrid.add(rowLabel("Contact"), 0, 6);
        infoGrid.add(valueLabel(applicant.getContact()), 1, 6);
        GridPane.setHgrow(infoGrid, Priority.ALWAYS);

        VBox infoCard = new VBox(16, new Label("Applicant Information"), infoGrid);
        infoCard.getStyleClass().add("info-card");
        infoCard.getChildren().get(0).getStyleClass().add("section-title");

        Label resumeTitle = new Label("Resume Management");
        resumeTitle.getStyleClass().add("section-title");

        Label resumeDesc = new Label("Supported formats: txt / pdf / doc / docx, maximum size 10MB.");
        resumeDesc.getStyleClass().add("page-subtitle");

        Button uploadBtn = new Button("Upload Resume");
        uploadBtn.getStyleClass().add("primary-button");

        Button replaceBtn = new Button("Replace Resume");
        replaceBtn.getStyleClass().add("secondary-button");

        Label resumeStatus = new Label("Status: Not uploaded");
        resumeStatus.getStyleClass().add("muted-label");
        Label resumePathLabel = new Label("File: None");
        resumePathLabel.getStyleClass().add("muted-label");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        HBox btnBox = new HBox(10, uploadBtn, replaceBtn);

        uploadBtn.setOnAction(e -> controller.uploadResume(applicant, resumeStatus, resumePathLabel, progressBar));
        replaceBtn.setOnAction(e -> controller.uploadResume(applicant, resumeStatus, resumePathLabel, progressBar));

        VBox resumeCard = new VBox(14, resumeTitle, resumeDesc, btnBox, progressBar, resumeStatus, resumePathLabel);
        resumeCard.getStyleClass().add("resume-card");

        Button jobListBtn = new Button("View Available Positions");
        jobListBtn.getStyleClass().add("success-button");
        jobListBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
            stage.setTitle("Available Positions - TA Recruitment");
        });

        Button backToLoginBtn = new Button("Back to Login Page");
        backToLoginBtn.getStyleClass().add("secondary-button");
        backToLoginBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.getScene().setRoot(roleSelectView.createContent());
            stage.setTitle("TA Recruitment System");
        });

        HBox buttonBox = new HBox(15, jobListBtn, backToLoginBtn);

        VBox card = new VBox(18, title, subtitle, infoCard, new Separator(), resumeCard, buttonBox);
        card.getStyleClass().add("card");
        card.setMaxWidth(900);

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(36));
        return root;
    }

    private Label rowLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private Label valueLabel(String text) {
        Label label = new Label(text == null ? "" : text);
        label.getStyleClass().add("value-label");
        label.setWrapText(true);
        return label;
    }
}

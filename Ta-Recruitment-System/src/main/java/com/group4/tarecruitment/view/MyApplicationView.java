package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import com.group4.tarecruitment.util.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * Builds the TA application history page.
 */
public class MyApplicationView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();
    private static final int PAGE_SIZE = 6;
    private int currentPage = 1;

    public MyApplicationView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("My Applications");
        title.getStyleClass().add("page-title");

        Button refreshBtn = new Button("Refresh Status");
        Button backToListBtn = new Button("Back to Job List");
        Button backToHomeBtn = new Button("Back to TA Home");

        refreshBtn.getStyleClass().add("btn-primary");
        backToListBtn.getStyleClass().add("btn-muted");
        backToHomeBtn.getStyleClass().add("btn-success");

        HBox topBar = new HBox(10, refreshBtn, backToListBtn, backToHomeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("toolbar");

        VBox appListBox = new VBox(12);
        appListBox.getStyleClass().add("list-container");

        HBox pageBox = new HBox(10);
        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");
        Label pageLabel = new Label("Page 1 / 1");
        pageBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        pageBox.setAlignment(Pos.CENTER);

        loadApplications(appListBox, pageLabel);

        refreshBtn.setOnAction(e -> loadApplications(appListBox, pageLabel));

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadApplications(appListBox, pageLabel);
            }
        });

        nextBtn.setOnAction(e -> {
            try {
                int totalPages = getTotalPages();
                if (currentPage < totalPages) {
                    currentPage++;
                    loadApplications(appListBox, pageLabel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backToListBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
            stage.setTitle("Available TA Positions");
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        VBox root = new VBox(16, title, topBar, appListBox, pageBox);
        root.getStyleClass().add("app-page");
        root.setPadding(new Insets(28));
        return root;
    }

    private int getTotalPages() throws Exception {
        List<Application> apps = jobService.getMyApplications(applicant.getTaId());
        if (apps.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) apps.size() / PAGE_SIZE);
    }

    private void loadApplications(VBox appListBox, Label pageLabel) {
        appListBox.getChildren().clear();
        try {
            List<Application> apps = jobService.getMyApplications(applicant.getTaId());
            int totalPages = apps.isEmpty() ? 1 : (int) Math.ceil((double) apps.size() / PAGE_SIZE);

            if (currentPage > totalPages) currentPage = totalPages;
            if (currentPage < 1) currentPage = 1;

            if (apps.isEmpty()) {
                Label emptyLabel = new Label("No application records found.");
                emptyLabel.getStyleClass().add("empty-text");
                appListBox.getChildren().add(emptyLabel);
                pageLabel.setText("Page 1 / 1");
                return;
            }

            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, apps.size());

            for (Application app : apps.subList(start, end)) {
                Job job = jobService.getJobById(app.getJobId());

                Label courseLabel = new Label((job != null ? job.getCourseName() : "Unknown Position"));
                courseLabel.getStyleClass().add("section-title");

                Label timeLabel = new Label("Applied: " + app.getApplicationTime());
                timeLabel.getStyleClass().add("muted-text");

                Label statusLabel = new Label(app.getStatus());
                statusLabel.getStyleClass().addAll("badge", statusBadgeClass(app.getStatus()));

                Button detailBtn = new Button("View Details");
                detailBtn.getStyleClass().add("btn-info");
                detailBtn.setOnAction(e -> showAppDetail(app, job));

                HBox spacer = new HBox();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(14, courseLabel, timeLabel, spacer, statusLabel, detailBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add("list-item-card");

                appListBox.getChildren().add(row);
            }

            pageLabel.setText(String.format("Page %d / %d", currentPage, totalPages));
        } catch (Exception e) {
            Label err = new Label("Load failed: " + e.getMessage());
            err.getStyleClass().add("status-error");
            appListBox.getChildren().add(err);
            pageLabel.setText("Page 1 / 1");
            e.printStackTrace();
        }
    }

    private String statusBadgeClass(String status) {
        if ("Approved".equalsIgnoreCase(status)) return "badge-success";
        if ("Rejected".equalsIgnoreCase(status)) return "badge-danger";
        if ("Pending".equalsIgnoreCase(status))  return "badge-warning";
        return "badge-neutral";
    }

    private void showAppDetail(Application app, Job job) {
        Stage detailStage = new Stage();
        detailStage.initOwner(stage);
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Application Details");

        String reviewComment = app.getReviewComment() == null || app.getReviewComment().isEmpty()
                ? "None"
                : app.getReviewComment();

        Label title = new Label(job != null ? job.getCourseName() : "Unknown Position");
        title.getStyleClass().add("page-title");

        Label statusBadge = new Label(app.getStatus());
        statusBadge.getStyleClass().addAll("badge", statusBadgeClass(app.getStatus()));

        HBox header = new HBox(12, title, statusBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox applicationCard = new VBox(8);
        applicationCard.getStyleClass().add("surface-card");
        applicationCard.getChildren().addAll(
                createDetailRow("Application ID:", app.getApplicationId()),
                createDetailRow("Application Time:", app.getApplicationTime()),
                createDetailRow("Status:", app.getStatus()),
                createDetailRow("Review Comment:", reviewComment)
        );

        VBox jobCard = new VBox(8);
        jobCard.getStyleClass().add("surface-card");
        jobCard.getChildren().addAll(
                createDetailRow("Position:", job != null ? job.getCourseName() : "Unknown Position"),
                createDetailRow("Type:", job != null ? job.getPositionType() : "N/A"),
                createDetailRow("Weekly Workload:", job != null ? job.getWeeklyWorkload() + " hours" : "N/A"),
                createDetailRow("MO:", job != null ? job.getMoName() : "N/A"),
                createDetailRow("Deadline:", job != null ? job.getDeadline() : "N/A"),
                createDetailRow("Skill Requirements:", job != null ? job.getSkillRequirements() : "N/A"),
                createDetailRow("Job Content:", job != null ? job.getJobContent() : "N/A")
        );

        Label appSectionTitle = new Label("Application");
        appSectionTitle.getStyleClass().add("section-title");
        Label jobSectionTitle = new Label("Position Information");
        jobSectionTitle.getStyleClass().add("section-title");

        VBox content = new VBox(12, header, appSectionTitle, applicationCard, jobSectionTitle, jobCard);
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("btn-muted");
        closeBtn.setOnAction(e -> detailStage.close());

        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 20, 12, 20));

        VBox root = new VBox(scrollPane, footer);
        root.getStyleClass().add("app-page");

        Scene scene = ThemeManager.createScene(root, 620, 520);
        detailStage.setScene(scene);
        detailStage.setMinWidth(480);
        detailStage.setMinHeight(360);
        detailStage.showAndWait();
    }

    private HBox createDetailRow(String labelText, String valueText) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("profile-info-label");

        Label value = new Label(valueText == null || valueText.isBlank() ? "N/A" : valueText);
        value.setWrapText(true);
        HBox.setHgrow(value, Priority.ALWAYS);

        row.getChildren().addAll(label, value);
        return row;
    }
}

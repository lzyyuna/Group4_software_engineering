package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MyApplicationView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();

    public MyApplicationView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("My Applications");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Review submitted applications and check their latest status.");
        subtitle.getStyleClass().add("page-subtitle");

        Button refreshBtn = new Button("Refresh Status");
        refreshBtn.getStyleClass().add("secondary-button");
        Button backBtn = new Button("Back to Position List");
        backBtn.getStyleClass().add("button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, refreshBtn, spacer, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox appListBox = new VBox(12);
        ScrollPane scrollPane = new ScrollPane(appListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(500);

        loadApplications(appListBox);

        refreshBtn.setOnAction(e -> loadApplications(appListBox));
        backBtn.setOnAction(e -> stage.getScene().setRoot(new JobListView(stage, applicant).createContent()));

        VBox card = new VBox(18, title, subtitle, topBar, scrollPane);
        card.getStyleClass().add("list-card");

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(32));
        return root;
    }

    private void loadApplications(VBox appListBox) {
        appListBox.getChildren().clear();
        try {
            List<Application> apps = jobService.getMyApplications(applicant.getTaId());
            if (apps.isEmpty()) {
                Label emptyLabel = new Label("No application records yet.");
                emptyLabel.getStyleClass().add("muted-label");
                appListBox.getChildren().add(emptyLabel);
                return;
            }

            for (Application app : apps) {
                Job job = jobService.getJobById(app.getJobId());
                String jobName = job != null ? job.getCourseName() : "Unknown Position";

                Label title = new Label(jobName);
                title.getStyleClass().add("job-title");

                Label meta = new Label("Application Time: " + app.getApplicationTime() + "    Status: " + app.getStatus());
                meta.getStyleClass().add("muted-label");
                meta.setWrapText(true);

                Button detailBtn = new Button("View Details");
                detailBtn.getStyleClass().add("primary-button");
                detailBtn.setOnAction(e -> showAppDetail(app, job));

                VBox appItem = new VBox(10, title, meta, detailBtn);
                appItem.getStyleClass().add("application-card");
                appListBox.getChildren().add(appItem);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Loading failed: " + e.getMessage());
            errorLabel.getStyleClass().add("status-error");
            appListBox.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private void showAppDetail(Application app, Job job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Details");
        alert.setHeaderText(null);
        String content = String.format(
                "Application ID: %s\nPosition: %s\nApplication Time: %s\nStatus: %s\nReview Comment: %s",
                app.getApplicationId(),
                job != null ? job.getCourseName() : "Unknown Position",
                app.getApplicationTime(),
                app.getStatus(),
                app.getReviewComment().isEmpty() ? "None" : app.getReviewComment()
        );
        alert.setContentText(content);
        alert.showAndWait();
    }
}

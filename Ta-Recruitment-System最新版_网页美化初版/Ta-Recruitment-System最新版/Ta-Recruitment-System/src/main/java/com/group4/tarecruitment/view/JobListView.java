package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class JobListView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    public JobListView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("Available TA Positions");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Browse currently active positions, view details, and submit your application.");
        subtitle.getStyleClass().add("page-subtitle");

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.getStyleClass().add("secondary-button");
        Button myAppsBtn = new Button("My Applications");
        myAppsBtn.getStyleClass().add("primary-button");
        Button backToProfileBtn = new Button("Back to Profile");
        backToProfileBtn.getStyleClass().add("button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, refreshBtn, myAppsBtn, spacer, backToProfileBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox jobListBox = new VBox(12);
        ScrollPane scrollPane = new ScrollPane(jobListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(480);

        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");
        Label pageLabel = new Label("Page 1");
        pageLabel.getStyleClass().add("muted-label");
        HBox pageBox = new HBox(12, prevBtn, pageLabel, nextBtn);
        pageBox.setAlignment(Pos.CENTER);

        loadJobs(jobListBox, pageLabel, prevBtn, nextBtn);

        refreshBtn.setOnAction(e -> loadJobs(jobListBox, pageLabel, prevBtn, nextBtn));
        myAppsBtn.setOnAction(e -> stage.getScene().setRoot(new MyApplicationView(stage, applicant).createContent()));
        backToProfileBtn.setOnAction(e -> {
            ProfileDetailView profileView = new ProfileDetailView(applicant, stage);
            stage.getScene().setRoot(profileView.getView());
            stage.setTitle("Profile Details & Resume Upload");
        });

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadJobs(jobListBox, pageLabel, prevBtn, nextBtn);
            }
        });
        nextBtn.setOnAction(e -> {
            try {
                int totalPages = Math.max(1, (int) Math.ceil((double) jobService.getActiveJobs().size() / PAGE_SIZE));
                if (currentPage < totalPages) {
                    currentPage++;
                    loadJobs(jobListBox, pageLabel, prevBtn, nextBtn);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox card = new VBox(18, title, subtitle, topBar, new Separator(), scrollPane, pageBox);
        card.getStyleClass().add("list-card");

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(32));
        return root;
    }

    private void loadJobs(VBox jobListBox, Label pageLabel, Button prevBtn, Button nextBtn) {
        jobListBox.getChildren().clear();
        try {
            List<Job> activeJobs = jobService.getActiveJobs();
            int totalPages = Math.max(1, (int) Math.ceil((double) activeJobs.size() / PAGE_SIZE));
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            int start = Math.max(0, (currentPage - 1) * PAGE_SIZE);
            int end = Math.min(start + PAGE_SIZE, activeJobs.size());
            List<Job> pageJobs = activeJobs.subList(start, end);

            if (pageJobs.isEmpty()) {
                Label emptyLabel = new Label("No active positions are available at the moment.");
                emptyLabel.getStyleClass().add("muted-label");
                jobListBox.getChildren().add(emptyLabel);
            } else {
                for (Job job : pageJobs) {
                    VBox contentBox = new VBox(10);

                    Label jobTitle = new Label(job.getCourseName());
                    jobTitle.getStyleClass().add("job-title");

                    HBox metaRow = new HBox(8,
                            createPill("ID: " + job.getJobId()),
                            createPill(job.getPositionType()),
                            createPill(job.getWeeklyWorkload() + "h/week"),
                            createPill("MO: " + job.getMoName())
                    );

                    Label detailLine = new Label(
                            "Release: " + job.getReleaseTime() + "    Deadline: " + job.getDeadline() +
                                    "\nSkills: " + job.getSkillRequirements()
                    );
                    detailLine.getStyleClass().add("muted-label");
                    detailLine.setWrapText(true);

                    Button detailBtn = new Button("View Details");
                    detailBtn.getStyleClass().add("primary-button");
                    detailBtn.setOnAction(e -> stage.getScene().setRoot(new JobDetailView(stage, applicant, job).createContent()));

                    contentBox.getChildren().addAll(jobTitle, metaRow, detailLine, detailBtn);
                    contentBox.getStyleClass().add("job-card");
                    jobListBox.getChildren().add(contentBox);
                }
            }

            pageLabel.setText(String.format("Page %d / %d", currentPage, totalPages));
            prevBtn.setDisable(currentPage <= 1);
            nextBtn.setDisable(currentPage >= totalPages);
        } catch (Exception e) {
            Label errorLabel = new Label("Loading failed: " + e.getMessage());
            errorLabel.getStyleClass().add("status-error");
            jobListBox.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private Label createPill(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("small-pill");
        return label;
    }
}

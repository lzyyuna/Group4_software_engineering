package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class JobListView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();
    private final int PAGE_SIZE = 10;
    private int currentPage = 1;

    public JobListView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("Available TA Positions");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button refreshBtn = new Button("Refresh List");
        Button myAppsBtn = new Button("My Applications");
        Button backToHomeBtn = new Button("Back to TA Home");

        String btnStyle = "-fx-font-size: 14px; -fx-padding: 7 14; -fx-background-radius: 5; -fx-font-weight: bold;";
        refreshBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        myAppsBtn.setStyle(btnStyle + "-fx-background-color: #9b59b6; -fx-text-fill: white;");
        backToHomeBtn.setStyle(btnStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        HBox topBar = new HBox(10, refreshBtn, myAppsBtn, backToHomeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox jobListBox = new VBox(10);
        jobListBox.setPadding(new Insets(15));
        jobListBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        HBox pageBox = new HBox(10);
        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");
        Label pageLabel = new Label("Page 1");
        pageBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        pageBox.setAlignment(Pos.CENTER);

        loadJobs(jobListBox, pageLabel);

        refreshBtn.setOnAction(e -> loadJobs(jobListBox, pageLabel));
        myAppsBtn.setOnAction(e -> {
            MyApplicationView myAppView = new MyApplicationView(stage, applicant);
            stage.getScene().setRoot(myAppView.createContent());
            stage.setTitle("My Applications");
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadJobs(jobListBox, pageLabel);
            }
        });

        nextBtn.setOnAction(e -> {
            try {
                int totalPages = (int) Math.ceil((double) jobService.getActiveJobs().size() / PAGE_SIZE);
                if (currentPage < totalPages) {
                    currentPage++;
                    loadJobs(jobListBox, pageLabel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(15, title, topBar, jobListBox, new Separator(), pageBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f6fa;");
        return root;
    }

    private void loadJobs(VBox jobListBox, Label pageLabel) {
        jobListBox.getChildren().clear();
        try {
            List<Job> activeJobs = jobService.getActiveJobs();
            int totalPages = (int) Math.ceil((double) activeJobs.size() / PAGE_SIZE);
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, activeJobs.size());
            List<Job> pageJobs = activeJobs.subList(start, end);

            if (pageJobs.isEmpty()) {
                Label emptyLabel = new Label("No available positions.");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
                jobListBox.getChildren().add(emptyLabel);
                return;
            }

            for (Job job : pageJobs) {
                HBox jobItem = new HBox(15);
                jobItem.setAlignment(Pos.CENTER_LEFT);
                jobItem.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");

                Label idLabel = new Label("ID: " + job.getJobId());
                Label courseLabel = new Label("Course: " + job.getCourseName());
                Label typeLabel = new Label("Type: " + job.getPositionType());
                Label workloadLabel = new Label("Weekly Workload: " + job.getWeeklyWorkload() + "h");
                Label moLabel = new Label("MO: " + job.getMoName());

                Button detailBtn = new Button("View Details");
                detailBtn.setStyle("-fx-font-size: 13px; -fx-padding: 6 12; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

                detailBtn.setOnAction(e -> {
                    JobDetailView detailView = new JobDetailView(stage, applicant, job);
                    stage.getScene().setRoot(detailView.createContent());
                    stage.setTitle("Job Details");
                });

                jobItem.getChildren().addAll(idLabel, courseLabel, typeLabel, workloadLabel, moLabel, detailBtn);
                jobListBox.getChildren().add(jobItem);
            }

            pageLabel.setText(String.format("Page %d / %d", currentPage, totalPages));
        } catch (Exception e) {
            jobListBox.getChildren().add(new Label("Load failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}
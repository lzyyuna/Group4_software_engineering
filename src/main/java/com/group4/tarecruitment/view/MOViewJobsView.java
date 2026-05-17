package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.MOService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.group4.tarecruitment.util.ThemeManager;

import java.util.List;

public class MOViewJobsView {

    private final Stage stage;
    private final String moName;
    private final MOService moService = new MOService();
    private VBox jobListBox;

    public MOViewJobsView(Stage stage, String moName) {
        this.stage = stage;
        this.moName = moName;
    }

    public Parent createContent() {
        Label title = new Label("My Posted Positions");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button refreshBtn = new Button("Refresh List");
        Button backBtn = new Button("Back to Home");

        String btnStyle = "-fx-font-size: 14px; -fx-padding: 7 14; -fx-background-radius: 5; -fx-font-weight: bold;";
        refreshBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        backBtn.setStyle(btnStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        HBox topBar = new HBox(10, refreshBtn, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        jobListBox = new VBox(8);
        jobListBox.setPadding(new Insets(10));
        jobListBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        loadJobs();

        refreshBtn.setOnAction(e -> loadJobs());

        backBtn.setOnAction(e -> {
            TeacherView teacherView = new TeacherView(stage, moName);
            stage.setScene(ThemeManager.createScene(teacherView.createContent(), 1000, 700));
        });

        VBox root = new VBox(12, title, topBar, jobListBox);
        root.setPadding(new Insets(20, 25, 18, 25));
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    private void loadJobs() {
        jobListBox.getChildren().clear();
        try {
            List<Job> jobs = moService.getMyPostedJobs(moName);

            if (jobs.isEmpty()) {
                Label emptyLabel = new Label("No Posted Positions");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
                jobListBox.getChildren().add(emptyLabel);
                return;
            }

            for (Job job : jobs) {
                VBox jobItem = new VBox(5);
                jobItem.setPadding(new Insets(8, 10, 8, 10));
                jobItem.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");

                Label courseLabel = new Label(job.getCourseName() == null ? "Untitled Course" : job.getCourseName());
                courseLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                Label basicInfoLabel = new Label(
                        "ID: " + safe(job.getJobId())
                                + "   |   Type: " + safe(job.getPositionType())
                                + "   |   Weekly Workload: " + job.getWeeklyWorkload() + "h"
                                + "   |   Released: " + safe(job.getReleaseTime())
                );
                basicInfoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

                Label statusLabel = new Label("Status: " + safe(job.getStatus()));
                statusLabel.setStyle(getStatusBadgeStyle(job.getStatus()));

                Button detailBtn = new Button("View Details");
                Button editBtn = new Button("Edit");
                Button closeBtn = new Button("Close Position");

                String actionBtnStyle = "-fx-font-size: 13px; -fx-padding: 6 12; -fx-background-radius: 5;";
                detailBtn.setStyle(actionBtnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
                editBtn.setStyle(actionBtnStyle + "-fx-background-color: #f39c12; -fx-text-fill: white;");
                closeBtn.setStyle(actionBtnStyle + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

                if (!"Recruiting".equals(job.getStatus())) {
                    editBtn.setDisable(true);
                    closeBtn.setDisable(true);
                    editBtn.setStyle(editBtn.getStyle() + "; -fx-opacity: 0.5;");
                    closeBtn.setStyle(closeBtn.getStyle() + "; -fx-opacity: 0.5;");
                }

                detailBtn.setOnAction(e -> showJobDetail(job));

                editBtn.setOnAction(e -> {
                    MOEditJobView editView = new MOEditJobView(stage, moName, job);
                    stage.setScene(ThemeManager.createScene(editView.createContent(), 1000, 700));
                });

                closeBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Close");
                    confirm.setHeaderText("Close Position");
                    confirm.setContentText("Are you sure you want to close this position?\n" +
                            "Position ID: " + job.getJobId() + "\n" +
                            "Course: " + job.getCourseName());

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                boolean success = moService.closeJob(job.getJobId(), moName);
                                if (success) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Success");
                                    alert.setContentText("Position closed successfully!");
                                    alert.showAndWait();
                                    loadJobs();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                });

                HBox bottomBar = new HBox(10);
                bottomBar.setAlignment(Pos.CENTER_LEFT);
                HBox spacer = new HBox();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                bottomBar.getChildren().addAll(statusLabel, spacer, detailBtn, editBtn, closeBtn);

                jobItem.getChildren().addAll(
                        courseLabel,
                        basicInfoLabel,
                        bottomBar
                );

                jobListBox.getChildren().add(jobItem);
            }
        } catch (Exception e) {
            jobListBox.getChildren().add(new Label("Load failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String getStatusBadgeStyle(String status) {
        String backgroundColor;
        String textColor;

        switch (status) {
            case "Recruiting":
                backgroundColor = "#e8f5e9";
                textColor = "#1b5e20";
                break;
            case "Closed":
                backgroundColor = "#fdecea";
                textColor = "#c62828";
                break;
            default:
                backgroundColor = "#eceff1";
                textColor = "#455a64";
                break;
        }

        return "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-background-color: " + backgroundColor + ";"
                + "-fx-background-radius: 4;"
                + "-fx-padding: 4 10;";
    }

    private void showJobDetail(Job job) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Position Detail");
        dialog.setHeaderText(null);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f5f6fa;");

        // Header with icon and title
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-background-radius: 8; -fx-padding: 16;");
        
        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 28px;");
        
        VBox titleBox = new VBox(4);
        Label titleLabel = new Label(job.getCourseName());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label idLabel = new Label("ID: " + job.getJobId());
        idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8);");
        titleBox.getChildren().addAll(titleLabel, idLabel);
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusBadge = new Label(job.getStatus());
        statusBadge.setStyle(getStatusBadgeStyle(job.getStatus()) + " -fx-padding: 6 14;");
        
        headerBox.getChildren().addAll(iconLabel, titleBox, spacer, statusBadge);

        // Main info grid
        VBox infoSection = new VBox(12);
        infoSection.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");
        
        Label sectionTitle = new Label("📊 Position Information");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(12);
        
        addInfoRow(infoGrid, 0, "Position Type:", job.getPositionType(), "#3498db");
        addInfoRow(infoGrid, 1, "Weekly Workload:", job.getWeeklyWorkload() + " hours/week", "#e74c3c");
        addInfoRow(infoGrid, 2, "MO Name:", job.getMoName(), "#27ae60");
        addInfoRow(infoGrid, 3, "MO Email:", job.getMoEmail(), "#9b59b6");
        addInfoRow(infoGrid, 4, "Release Time:", job.getReleaseTime(), "#f39c12");
        addInfoRow(infoGrid, 5, "Deadline:", job.getDeadline() != null ? job.getDeadline() : "Not specified", "#e67e22");
        
        infoSection.getChildren().addAll(sectionTitle, new Separator(), infoGrid);

        // Requirements section
        VBox reqSection = new VBox(12);
        reqSection.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");
        
        Label reqTitle = new Label("🎯 Requirements & Content");
        reqTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox skillBox = createInfoBox("Skill Requirements:", job.getSkillRequirements());
        VBox contentBox = createInfoBox("Job Content:", job.getJobContent());
        
        reqSection.getChildren().addAll(reqTitle, new Separator(), skillBox, contentBox);

        content.getChildren().addAll(headerBox, infoSection, reqSection);

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(550);
        dialog.getDialogPane().setPrefHeight(600);

        dialog.showAndWait();
    }
    
    private void addInfoRow(GridPane grid, int row, String label, String value, String color) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        
        Label valueNode = new Label(value != null ? value : "N/A");
        valueNode.setStyle("-fx-font-size: 14px; -fx-text-fill: " + color + "; -fx-font-weight: 500;");
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    private VBox createInfoBox(String title, String content) {
        VBox box = new VBox(6);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        Label contentLabel = new Label(content != null && !content.isEmpty() ? content : "Not specified");
        contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(480);
        contentLabel.setWrapText(true);
        
        box.getChildren().addAll(titleLabel, contentLabel);
        return box;
    }
}

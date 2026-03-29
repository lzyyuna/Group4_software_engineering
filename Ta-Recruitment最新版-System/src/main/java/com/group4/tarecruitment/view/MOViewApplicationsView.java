package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.MOService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class MOViewApplicationsView {

    private final Stage stage;
    private final String moName;
    private final MOService moService = new MOService();
    private final ApplicantService applicantService = new ApplicantService();

    private TableView<Application> appTable;
    private ObservableList<Application> appData;
    private ComboBox<Job> jobComboBox;
    private ComboBox<String> statusFilter;

    public MOViewApplicationsView(Stage stage, String moName) {
        this.stage = stage;
        this.moName = moName;
    }

    public Parent createContent() {
        Label title = new Label("Review TA Applications");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 职位选择
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER);

        Label jobLabel = new Label("Select Position:");
        jobComboBox = new ComboBox<>();
        jobComboBox.setPrefWidth(300);
        jobComboBox.setPromptText("Select a position");

        Label statusLabel = new Label("Filter by Status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");

        Button loadBtn = new Button("Load Applications");
        loadBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        filterBox.getChildren().addAll(jobLabel, jobComboBox, statusLabel, statusFilter, loadBtn);

        // 加载职位列表
        loadJobs();

        // 创建申请表格
        appTable = new TableView<>();
        appTable.setPrefHeight(350);

        TableColumn<Application, String> appIdCol = new TableColumn<>("Application ID");
        appIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApplicationId()));
        appIdCol.setPrefWidth(120);

        TableColumn<Application, String> taIdCol = new TableColumn<>("TA ID");
        taIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTaId()));
        taIdCol.setPrefWidth(100);

        TableColumn<Application, String> taNameCol = new TableColumn<>("TA Name");
        taNameCol.setCellValueFactory(data -> {
            try {
                var applicant = applicantService.getApplicantById(data.getValue().getTaId());
                return new SimpleStringProperty(applicant != null ? applicant.getName() : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        taNameCol.setPrefWidth(120);

        TableColumn<Application, String> timeCol = new TableColumn<>("Application Time");
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApplicationTime()));
        timeCol.setPrefWidth(150);

        TableColumn<Application, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        TableColumn<Application, String> commentCol = new TableColumn<>("Review Comment");
        commentCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getReviewComment() != null ? data.getValue().getReviewComment() : ""));
        commentCol.setPrefWidth(150);

        appTable.getColumns().addAll(appIdCol, taIdCol, taNameCol, timeCol, statusCol, commentCol);

        // 按钮
        Button viewProfileBtn = new Button("View TA Profile");
        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");
        Button refreshBtn = new Button("Refresh");
        Button backBtn = new Button("Back");

        viewProfileBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, viewProfileBtn, approveBtn, rejectBtn, refreshBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // 状态标签
        Label statusMsgLabel = new Label();
        statusMsgLabel.setStyle("-fx-text-fill: #e74c3c;");

        // 按钮事件
        loadBtn.setOnAction(e -> loadApplications());

        refreshBtn.setOnAction(e -> {
            if (jobComboBox.getValue() != null) {
                loadApplications();
            }
        });

        viewProfileBtn.setOnAction(e -> {
            Application selected = appTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showTAProfile(selected.getTaId());
            } else {
                statusMsgLabel.setText("Please select an application first!");
            }
        });

        approveBtn.setOnAction(e -> {
            Application selected = appTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                reviewApplication(selected, "Approved", statusMsgLabel);
            } else {
                statusMsgLabel.setText("Please select an application first!");
            }
        });

        rejectBtn.setOnAction(e -> {
            Application selected = appTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                reviewApplication(selected, "Rejected", statusMsgLabel);
            } else {
                statusMsgLabel.setText("Please select an application first!");
            }
        });

        backBtn.setOnAction(e -> {
            TeacherView teacherView = new TeacherView(stage, moName);
            stage.setScene(new Scene(teacherView.createContent(), 800, 600));
        });

        VBox root = new VBox(15, title, filterBox, appTable, buttonBox, statusMsgLabel);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    private void loadJobs() {
        try {
            List<Job> jobs = moService.getMyPostedJobs(moName);
            jobComboBox.getItems().clear();
            jobComboBox.getItems().addAll(jobs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadApplications() {
        Job selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            return;
        }

        try {
            List<Application> apps = moService.getJobApplications(selectedJob.getJobId(), moName);

            // 应用状态过滤
            String filter = statusFilter.getValue();
            if (!"All".equals(filter)) {
                apps = apps.stream()
                        .filter(a -> a.getStatus().equals(filter))
                        .toList();
            }

            appData = FXCollections.observableArrayList(apps);
            appTable.setItems(appData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTAProfile(String taId) {
        try {
            var applicant = applicantService.getApplicantById(taId);
            if (applicant != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("TA Profile");
                alert.setHeaderText("TA ID: " + taId);
                alert.setContentText(
                        "Name: " + applicant.getName() + "\n" +
                        "Email: " + applicant.getEmail() + "\n" +
                        "Phone: " + (applicant.getPhone() != null ? applicant.getPhone() : "N/A") + "\n" +
                        "Skills: " + (applicant.getSkills() != null ? applicant.getSkills() : "N/A") + "\n" +
                        "CV Path: " + (applicant.getCvPath() != null ? applicant.getCvPath() : "N/A")
                );
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Found");
                alert.setContentText("TA profile not found!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reviewApplication(Application app, String result, Label statusLabel) {
        // 只有Pending状态的申请可以审核
        if (!"Pending".equals(app.getStatus())) {
            statusLabel.setText("Error: Only 'Pending' applications can be reviewed!");
            return;
        }

        // 创建评论输入对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Review Application");
        dialog.setHeaderText(result + " Application: " + app.getApplicationId());
        dialog.setContentText("Review Comment (optional, max 50 chars):");

        dialog.showAndWait().ifPresent(comment -> {
            if (comment.length() > 50) {
                statusLabel.setText("Error: Comment must be 50 characters or less!");
                return;
            }

            try {
                boolean success = moService.reviewApplication(app.getApplicationId(), moName, result, comment);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("Application " + result.toLowerCase() + " successfully!");
                    alert.showAndWait();
                    loadApplications();
                } else {
                    statusLabel.setText("Error: Failed to review application!");
                }
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

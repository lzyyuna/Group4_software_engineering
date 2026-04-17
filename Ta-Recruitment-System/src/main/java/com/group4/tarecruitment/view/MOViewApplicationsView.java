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
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.group4.tarecruitment.util.ThemeManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        title.getStyleClass().add("mo-applications-title");

        // 职位选择
        HBox filterBox = new HBox(15);
        filterBox.getStyleClass().add("filter-box");

        Label jobLabel = new Label("Select Position:");
        jobComboBox = new ComboBox<>();
        jobComboBox.setPrefWidth(300);
        jobComboBox.setPromptText("Select a position");

        Label statusLabel = new Label("Filter by Status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");

        Button loadBtn = new Button("Load Applications");
        loadBtn.getStyleClass().add("refresh-btn");

        filterBox.getChildren().addAll(jobLabel, jobComboBox, statusLabel, statusFilter, loadBtn);

        // 加载职位列表
        loadJobs();

        // 创建申请表格
        appTable = new TableView<>();
        appTable.getStyleClass().add("applications-table");

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

        viewProfileBtn.getStyleClass().add("view-profile-btn");
        approveBtn.getStyleClass().add("approve-btn");
        rejectBtn.getStyleClass().add("reject-btn");
        refreshBtn.getStyleClass().add("refresh-btn");
        backBtn.getStyleClass().add("back-btn");

        HBox buttonBox = new HBox(15, viewProfileBtn, approveBtn, rejectBtn, refreshBtn, backBtn);
        buttonBox.getStyleClass().add("button-box");

        // 状态标签
        Label statusMsgLabel = new Label();
        statusMsgLabel.getStyleClass().add("status-message");

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
            stage.setScene(ThemeManager.createScene(teacherView.createContent(), 1000, 700));
        });

        VBox root = new VBox(15, title, filterBox, appTable, buttonBox, statusMsgLabel);
        root.getStyleClass().add("mo-applications-page");

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
                // 创建自定义对话框
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("TA Profile");
                dialog.setHeaderText("TA ID: " + taId);

                // 创建内容区域
                VBox content = new VBox();
                content.getStyleClass().add("profile-content");

                // 创建个人信息卡片
                VBox profileSection = new VBox(16);
                profileSection.getStyleClass().add("profile-section");

                // 创建信息行，使用HBox布局使标签更加整齐
                createStyledInfoRow(profileSection, "Name:", applicant.getName() != null ? applicant.getName() : "N/A");
                createStyledInfoRow(profileSection, "Email:", applicant.getEmail() != null ? applicant.getEmail() : "N/A");
                createStyledInfoRow(profileSection, "Phone:", applicant.getPhone() != null ? applicant.getPhone() : "N/A");
                createStyledInfoRow(profileSection, "Skills:", applicant.getSkills() != null ? applicant.getSkills() : "N/A");

                // 创建简历信息卡片
                VBox cvSection = new VBox(16);
                cvSection.getStyleClass().add("cv-section");
                
                // 简历信息和下载按钮
                HBox cvBox = new HBox(12);
                cvBox.getStyleClass().add("cv-box");
                
                Label cvPathLabel = new Label(applicant.getCvPath() != null ? applicant.getCvPath() : "N/A");
                cvPathLabel.getStyleClass().add("cv-path-label");
                
                Button downloadBtn = new Button("Download CV");
                downloadBtn.getStyleClass().add("download-btn");

                // 只有当简历路径存在时才启用下载按钮
                if (applicant.getCvPath() == null || applicant.getCvPath().isEmpty()) {
                    downloadBtn.setDisable(true);
                }

                downloadBtn.setOnAction(e -> downloadCV(applicant.getCvPath()));
                cvBox.getChildren().addAll(cvPathLabel, downloadBtn);
                cvSection.getChildren().add(cvBox);

                // 添加所有部分到内容区域
                content.getChildren().addAll(profileSection, cvSection);

                // 添加关闭按钮
                ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().add(closeButtonType);

                // 为对话框添加样式
                dialog.getDialogPane().getStyleClass().add("ta-profile-dialog");
                dialog.getDialogPane().setContent(content);
                
                // 确保对话框加载CSS样式
                String css = getClass().getResource("/styles/app-theme.css").toExternalForm();
                dialog.getDialogPane().getStylesheets().add(css);
                
                // 设置对话框大小
                dialog.getDialogPane().setPrefWidth(550);
                dialog.getDialogPane().setPrefHeight(400);
                
                dialog.showAndWait();
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
    
    private void createStyledInfoRow(VBox parent, String labelText, String value) {
        HBox row = new HBox(16);
        row.getStyleClass().add("profile-info-row");
        
        Label label = new Label(labelText);
        label.getStyleClass().add("profile-info-label");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("profile-info-value");
        
        row.getChildren().addAll(label, valueLabel);
        parent.getChildren().add(row);
    }

    private void downloadCV(String cvPath) {
        if (cvPath == null || cvPath.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No CV");
            alert.setContentText("No CV file available for download!");
            alert.showAndWait();
            return;
        }

        try {
            // 提取文件名
            String fileName = cvPath;
            if (cvPath.contains("\\")) {
                fileName = cvPath.substring(cvPath.lastIndexOf("\\") + 1);
            } else if (cvPath.contains("/")) {
                fileName = cvPath.substring(cvPath.lastIndexOf("/") + 1);
            }
            
            // 尝试多个可能的相对路径
            String[] possiblePaths = {
                "data/resumes/" + fileName,
                "../data/resumes/" + fileName,
                "TA-Recruitment-System/data/resumes/" + fileName,
                "../TA-Recruitment-System/data/resumes/" + fileName,
                "../../data/resumes/" + fileName,
                "../../TA-Recruitment-System/data/resumes/" + fileName,
                "resumes/" + fileName,
                "../resumes/" + fileName
            };

            File cvFile = null;
            String foundPath = "";

            for (String path : possiblePaths) {
                File tempFile = new File(path);
                if (tempFile.exists()) {
                    cvFile = tempFile;
                    foundPath = tempFile.getAbsolutePath();
                    break;
                }
            }

            if (cvFile == null || !cvFile.exists()) {
                // 尝试从类路径加载
                try {
                    java.net.URL resourceUrl = getClass().getResource("/resumes/" + fileName);
                    if (resourceUrl != null) {
                        cvFile = new File(resourceUrl.toURI());
                        foundPath = cvFile.getAbsolutePath();
                    }
                } catch (Exception e) {
                    // 类路径加载失败，继续处理
                }
            }

            if (cvFile == null || !cvFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Not Found");
                alert.setContentText("CV file not found. Please ensure the file exists in the data/resumes directory.");
                alert.showAndWait();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CV File");
            fileChooser.setInitialFileName(cvFile.getName());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
            );

            File saveFile = fileChooser.showSaveDialog(stage);

            if (saveFile != null) {
                Files.copy(
                        cvFile.toPath(),
                        saveFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Download Successful");
                alert.setContentText("CV file downloaded successfully to:\n" + saveFile.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Download Failed");
            alert.setContentText("Failed to download CV file: " + e.getMessage());
            alert.showAndWait();
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

package com.group4.tarecruitment.view;

import com.group4.tarecruitment.util.ThemeManager;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
            stage.setScene(ThemeManager.createScene(teacherView.createContent(), 800, 600));
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
                // 创建自定义对话框
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("TA Profile");
                dialog.setHeaderText("TA ID: " + taId);

                // 创建内容区域
                VBox content = new VBox(10);
                content.setPadding(new Insets(20));

                Label nameLabel = new Label("Name: " + applicant.getName());
                Label emailLabel = new Label("Email: " + applicant.getEmail());
                Label phoneLabel = new Label("Phone: " + (applicant.getPhone() != null ? applicant.getPhone() : "N/A"));
                Label skillsLabel = new Label("Skills: " + (applicant.getSkills() != null ? applicant.getSkills() : "N/A"));

                // 简历信息和下载按钮
                HBox cvBox = new HBox(10);
                Label cvLabel = new Label("CV Path: " + (applicant.getCvPath() != null ? applicant.getCvPath() : "N/A"));
                Button downloadBtn = new Button("Download CV");

                // 只有当简历路径存在时才启用下载按钮
                if (applicant.getCvPath() == null || applicant.getCvPath().isEmpty()) {
                    downloadBtn.setDisable(true);
                }

                downloadBtn.setOnAction(e -> downloadCV(applicant.getCvPath()));
                cvBox.getChildren().addAll(cvLabel, downloadBtn);

                content.getChildren().addAll(nameLabel, emailLabel, phoneLabel, skillsLabel, cvBox);

                // 添加关闭按钮
                ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().add(closeButtonType);

                dialog.getDialogPane().setContent(content);
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

    private void downloadCV(String cvPath) {
        if (cvPath == null || cvPath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No CV");
            alert.setContentText("No CV file available for download!");
            alert.showAndWait();
            return;
        }

        try {
            // 修复路径：手动重建正确的路径
            // 从错误信息看，路径中的反斜杠完全丢失了
            // 正确的路径应该包含反斜杠
            String correctPath = "C:\\Users\\25476\\Desktop\\Group4_software_engineering-main\\Ta-Recruitment-System\\data\\resumes\\" + 
                               cvPath.substring(cvPath.lastIndexOf("_") - 6); // 提取文件名部分
            
            // 获取文件对象
            File cvFile = new File(correctPath);

            if (!cvFile.exists()) {
                // 尝试另一种方式：使用正斜杠
                String altPath = correctPath.replace("\\", "/");
                File altCvFile = new File(altPath);
                
                if (!altCvFile.exists()) {
                    // 尝试从当前工作目录构建路径
                    String currentDir = System.getProperty("user.dir");
                    String relativePath = currentDir + "\\data\\resumes\\" + 
                                        cvPath.substring(cvPath.lastIndexOf("_") - 6);
                    File relativeCvFile = new File(relativePath);
                    
                    if (!relativeCvFile.exists()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("File Not Found");
                        alert.setContentText("CV file not found:\n" + 
                                            "Original: " + cvPath + "\n" +
                                            "Correct path: " + correctPath + "\n" +
                                            "Alt path: " + altPath + "\n" +
                                            "Relative path: " + relativePath + "\n" +
                                            "Current dir: " + currentDir);
                        alert.showAndWait();
                        return;
                    }
                    cvFile = relativeCvFile;
                } else {
                    cvFile = altCvFile;
                }
            }

            // 创建文件选择器来保存文件
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CV File");
            fileChooser.setInitialFileName(cvFile.getName());

            // 设置文件过滤器（可选）
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "All Files (*.*)", "*.*");
            fileChooser.getExtensionFilters().add(extFilter);

            // 显示保存对话框
            File saveFile = fileChooser.showSaveDialog(stage);

            if (saveFile != null) {
                // 复制文件到指定位置
                Files.copy(
                        cvFile.toPath(),
                        saveFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
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

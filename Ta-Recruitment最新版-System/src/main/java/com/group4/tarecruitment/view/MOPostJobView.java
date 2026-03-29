package com.group4.tarecruitment.view;

import com.group4.tarecruitment.service.MOService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MOPostJobView {

    private final Stage stage;
    private final String moName;
    private final String moEmail;
    private final MOService moService = new MOService();

    public MOPostJobView(Stage stage, String moName, String moEmail) {
        this.stage = stage;
        this.moName = moName;
        this.moEmail = moEmail;
    }

    public Parent createContent() {
        Label title = new Label("Post TA Recruitment Position");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 表单字段
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        Label courseNameLabel = new Label("Course Name*:");
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Enter course name");

        Label positionTypeLabel = new Label("Position Type*:");
        ComboBox<String> positionTypeCombo = new ComboBox<>();
        positionTypeCombo.getItems().addAll("Module TA", "Invigilation TA");
        positionTypeCombo.setPromptText("Select position type");

        Label workloadLabel = new Label("Weekly Workload* (hours/week):");
        TextField workloadField = new TextField();
        workloadField.setPromptText("Enter weekly workload");

        Label moNameLabel = new Label("MO Name*:");
        TextField moNameField = new TextField(moName);
        moNameField.setEditable(false);

        Label moEmailLabel = new Label("MO Email*:");
        TextField moEmailField = new TextField(moEmail);
        moEmailField.setEditable(false);

        Label skillLabel = new Label("Skill Requirements:");
        TextArea skillArea = new TextArea();
        skillArea.setPromptText("Enter skill requirements");
        skillArea.setPrefRowCount(3);

        Label contentLabel = new Label("Job Content:");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter job content/description");
        contentArea.setPrefRowCount(3);

        Label deadlineLabel = new Label("Application Deadline:");
        TextField deadlineField = new TextField();
        deadlineField.setPromptText("YYYY-MM-DD");

        // 添加到表单
        form.add(courseNameLabel, 0, 0);
        form.add(courseNameField, 1, 0);
        form.add(positionTypeLabel, 0, 1);
        form.add(positionTypeCombo, 1, 1);
        form.add(workloadLabel, 0, 2);
        form.add(workloadField, 1, 2);
        form.add(moNameLabel, 0, 3);
        form.add(moNameField, 1, 3);
        form.add(moEmailLabel, 0, 4);
        form.add(moEmailField, 1, 4);
        form.add(skillLabel, 0, 5);
        form.add(skillArea, 1, 5);
        form.add(contentLabel, 0, 6);
        form.add(contentArea, 1, 6);
        form.add(deadlineLabel, 0, 7);
        form.add(deadlineField, 1, 7);

        // 按钮
        Button postBtn = new Button("Post Position");
        Button backBtn = new Button("Back");

        postBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox buttonBox = new HBox(15, postBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // 状态标签
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");

        // 按钮事件
        postBtn.setOnAction(e -> {
            try {
                // 验证必填字段
                String courseName = courseNameField.getText().trim();
                String positionType = positionTypeCombo.getValue();
                String workloadStr = workloadField.getText().trim();

                if (courseName.isEmpty()) {
                    statusLabel.setText("Error: Course Name is required!");
                    return;
                }
                if (positionType == null || positionType.isEmpty()) {
                    statusLabel.setText("Error: Position Type is required!");
                    return;
                }
                if (workloadStr.isEmpty()) {
                    statusLabel.setText("Error: Weekly Workload is required!");
                    return;
                }

                int workload;
                try {
                    workload = Integer.parseInt(workloadStr);
                    if (workload <= 0) {
                        statusLabel.setText("Error: Workload must be a positive number!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Error: Workload must be a valid number!");
                    return;
                }

                String jobId = moService.postJob(
                        courseName,
                        positionType,
                        workload,
                        moName,
                        moEmail,
                        skillArea.getText().trim(),
                        contentArea.getText().trim(),
                        deadlineField.getText().trim()
                );

                if (jobId != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Position posted successfully!\nJob ID: " + jobId);
                    alert.showAndWait();

                    // 清空表单
                    courseNameField.clear();
                    positionTypeCombo.setValue(null);
                    workloadField.clear();
                    skillArea.clear();
                    contentArea.clear();
                    deadlineField.clear();
                    statusLabel.setText("");
                } else {
                    statusLabel.setText("Error: Failed to post position. Please check your input.");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> {
            TeacherView teacherView = new TeacherView(stage, moName);
            stage.setScene(new Scene(teacherView.createContent(), 800, 600));
        });

        VBox root = new VBox(20, title, form, buttonBox, statusLabel);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }
}

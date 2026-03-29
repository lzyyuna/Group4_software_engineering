package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Job;
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

public class MOViewJobsView {

    private final Stage stage;
    private final String moName;
    private final MOService moService = new MOService();
    private TableView<Job> jobTable;
    private ObservableList<Job> jobData;

    public MOViewJobsView(Stage stage, String moName) {
        this.stage = stage;
        this.moName = moName;
    }

    public Parent createContent() {
        Label title = new Label("My Posted Positions");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 创建表格
        jobTable = new TableView<>();
        jobTable.setPrefHeight(400);

        // 定义列
        TableColumn<Job, String> idCol = new TableColumn<>("Position ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJobId()));
        idCol.setPrefWidth(120);

        TableColumn<Job, String> courseCol = new TableColumn<>("Course Name");
        courseCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseName()));
        courseCol.setPrefWidth(150);

        TableColumn<Job, String> typeCol = new TableColumn<>("Position Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPositionType()));
        typeCol.setPrefWidth(120);

        TableColumn<Job, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        TableColumn<Job, String> timeCol = new TableColumn<>("Release Time");
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReleaseTime()));
        timeCol.setPrefWidth(150);

        TableColumn<Job, String> workloadCol = new TableColumn<>("Workload (h/w)");
        workloadCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getWeeklyWorkload())));
        workloadCol.setPrefWidth(100);

        jobTable.getColumns().addAll(idCol, courseCol, typeCol, statusCol, timeCol, workloadCol);

        // 加载数据
        loadJobs();

        // 双击查看详情
        jobTable.setRowFactory(tv -> {
            TableRow<Job> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Job job = row.getItem();
                    showJobDetail(job);
                }
            });
            return row;
        });

        // 按钮
        Button refreshBtn = new Button("Refresh");
        Button detailBtn = new Button("View Detail");
        Button editBtn = new Button("Edit");
        Button closeBtn = new Button("Close Position");
        Button backBtn = new Button("Back");

        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        detailBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, refreshBtn, detailBtn, editBtn, closeBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // 状态标签
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");

        // 按钮事件
        refreshBtn.setOnAction(e -> loadJobs());

        detailBtn.setOnAction(e -> {
            Job selected = jobTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showJobDetail(selected);
            } else {
                statusLabel.setText("Please select a position first!");
            }
        });

        editBtn.setOnAction(e -> {
            Job selected = jobTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (!"Recruiting".equals(selected.getStatus())) {
                    statusLabel.setText("Error: Only 'Recruiting' positions can be edited!");
                    return;
                }
                MOEditJobView editView = new MOEditJobView(stage, moName, selected);
                stage.setScene(new Scene(editView.createContent(), 800, 600));
            } else {
                statusLabel.setText("Please select a position first!");
            }
        });

        closeBtn.setOnAction(e -> {
            Job selected = jobTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (!"Recruiting".equals(selected.getStatus())) {
                    statusLabel.setText("Error: Only 'Recruiting' positions can be closed!");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Close");
                confirm.setHeaderText("Close Position");
                confirm.setContentText("Are you sure you want to close this position?\n" +
                        "Position ID: " + selected.getJobId() + "\n" +
                        "Course: " + selected.getCourseName());

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            boolean success = moService.closeJob(selected.getJobId(), moName);
                            if (success) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setContentText("Position closed successfully!");
                                alert.showAndWait();
                                loadJobs();
                            } else {
                                statusLabel.setText("Error: Failed to close position!");
                            }
                        } catch (Exception ex) {
                            statusLabel.setText("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                });
            } else {
                statusLabel.setText("Please select a position first!");
            }
        });

        backBtn.setOnAction(e -> {
            TeacherView teacherView = new TeacherView(stage, moName);
            stage.setScene(new Scene(teacherView.createContent(), 800, 600));
        });

        VBox root = new VBox(15, title, jobTable, buttonBox, statusLabel);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    private void loadJobs() {
        try {
            List<Job> jobs = moService.getMyPostedJobs(moName);
            jobData = FXCollections.observableArrayList(jobs);
            jobTable.setItems(jobData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showJobDetail(Job job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Position Detail");
        alert.setHeaderText("Job ID: " + job.getJobId());
        alert.setContentText(
                "Course Name: " + job.getCourseName() + "\n" +
                "Position Type: " + job.getPositionType() + "\n" +
                "Weekly Workload: " + job.getWeeklyWorkload() + " hours/week\n" +
                "MO Name: " + job.getMoName() + "\n" +
                "MO Email: " + job.getMoEmail() + "\n" +
                "Status: " + job.getStatus() + "\n" +
                "Release Time: " + job.getReleaseTime() + "\n" +
                "Deadline: " + (job.getDeadline() != null ? job.getDeadline() : "N/A") + "\n" +
                "Skill Requirements: " + (job.getSkillRequirements() != null ? job.getSkillRequirements() : "N/A") + "\n" +
                "Job Content: " + (job.getJobContent() != null ? job.getJobContent() : "N/A")
        );
        alert.showAndWait();
    }
}

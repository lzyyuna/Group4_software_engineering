package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        Label title = new Label("我的申请记录");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold;");

        Button refreshBtn = new Button("刷新状态");
        Button backBtn = new Button("返回岗位列表");
        HBox topBar = new HBox(10, refreshBtn, backBtn);

        VBox appListBox = new VBox(10);
        appListBox.setPadding(new Insets(10));
        appListBox.setStyle("-fx-background-color: #f8f9fa;");

        loadApplications(appListBox);

        refreshBtn.setOnAction(e -> loadApplications(appListBox));
        backBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
        });

        VBox root = new VBox(15, title, topBar, appListBox);
        root.setPadding(new Insets(20));
        return root;
    }

    private void loadApplications(VBox appListBox) {
        appListBox.getChildren().clear();
        try {
            List<Application> apps = jobService.getMyApplications(applicant.getTaId());
            if (apps.isEmpty()) {
                appListBox.getChildren().add(new Label("暂无申请记录"));
                return;
            }

            for (Application app : apps) {
                Job job = jobService.getJobById(app.getJobId());
                HBox appItem = new HBox(15);
                appItem.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 5;");
                appItem.getChildren().addAll(
                        new Label("课程: " + (job != null ? job.getCourseName() : "未知岗位")),
                        new Label("申请时间: " + app.getApplicationTime()),
                        new Label("状态: " + app.getStatus())
                );
                Button detailBtn = new Button("查看详情");
                detailBtn.setOnAction(e -> showAppDetail(app, job));
                appItem.getChildren().add(detailBtn);
                appListBox.getChildren().add(appItem);
            }
        } catch (Exception e) {
            appListBox.getChildren().add(new Label("加载失败: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void showAppDetail(Application app, Job job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("申请详情");
        alert.setHeaderText(null);
        String content = String.format(
                "申请ID: %s\n岗位: %s\n申请时间: %s\n状态: %s\n评审意见: %s",
                app.getApplicationId(),
                job != null ? job.getCourseName() : "未知岗位",
                app.getApplicationTime(),
                app.getStatus(),
                app.getReviewComment().isEmpty() ? "无" : app.getReviewComment()
        );
        alert.setContentText(content);
        alert.showAndWait();
    }
}
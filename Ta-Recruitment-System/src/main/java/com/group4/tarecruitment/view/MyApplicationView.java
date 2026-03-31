package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
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

public class MyApplicationView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();

    public MyApplicationView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        // 标题美化
        Label title = new Label("我的申请记录");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 按钮统一美化
        Button refreshBtn = new Button("刷新状态");
        Button backToListBtn = new Button("返回岗位列表");
        Button backToHomeBtn = new Button("返回TA首页");

        String btnStyle = "-fx-font-size: 14px; -fx-padding: 7 14; -fx-background-radius: 5; -fx-font-weight: bold;";
        refreshBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        backToListBtn.setStyle(btnStyle + "-fx-background-color: #95a5a6; -fx-text-fill: white;");
        backToHomeBtn.setStyle(btnStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        HBox topBar = new HBox(10, refreshBtn, backToListBtn, backToHomeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // 列表容器美化
        VBox appListBox = new VBox(10);
        appListBox.setPadding(new Insets(15));
        appListBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        loadApplications(appListBox);

        refreshBtn.setOnAction(e -> loadApplications(appListBox));

        backToListBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        VBox root = new VBox(15, title, topBar, appListBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f6fa;");
        return root;
    }

    private void loadApplications(VBox appListBox) {
        appListBox.getChildren().clear();
        try {
            List<Application> apps = jobService.getMyApplications(applicant.getTaId());
            if (apps.isEmpty()) {
                Label emptyLabel = new Label("暂无申请记录");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
                appListBox.getChildren().add(emptyLabel);
                return;
            }

            for (Application app : apps) {
                Job job = jobService.getJobById(app.getJobId());

                HBox appItem = new HBox(15);
                appItem.setAlignment(Pos.CENTER_LEFT);
                appItem.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");

                Label courseLabel = new Label("课程: " + (job != null ? job.getCourseName() : "未知岗位"));
                Label timeLabel = new Label("申请时间: " + app.getApplicationTime());
                Label statusLabel = new Label("状态: " + app.getStatus());

                // 状态颜色
                if (app.getStatus().equals("待审核")) {
                    statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                } else if (app.getStatus().equals("已通过")) {
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                } else if (app.getStatus().equals("已拒绝")) {
                    statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }

                Button detailBtn = new Button("查看详情");
                detailBtn.setStyle("-fx-font-size: 13px; -fx-padding: 6 12; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

                detailBtn.setOnAction(e -> showAppDetail(app, job));
                appItem.getChildren().addAll(courseLabel, timeLabel, statusLabel, detailBtn);
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
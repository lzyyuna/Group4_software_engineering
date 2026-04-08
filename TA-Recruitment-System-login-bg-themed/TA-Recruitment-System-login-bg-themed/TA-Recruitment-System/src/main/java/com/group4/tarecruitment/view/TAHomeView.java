package com.group4.tarecruitment.view;

import com.group4.tarecruitment.util.ThemeManager;
import com.group4.tarecruitment.model.Applicant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TAHomeView {

    private final Stage stage;
    private final Applicant applicant;

    public TAHomeView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("TA Dashboard");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ✅ 关键修复：显示登录用户名（和MO页面完全一致）
        // 方案1：显示登录用户名（推荐，和TeacherView的Welcome, huang对齐）
        Label welcomeLabel = new Label("Welcome, " + applicant.getUsername());
        // 方案2：显示真实姓名（如果需要，替换为 applicant.getName()）
        // Label welcomeLabel = new Label("Welcome, " + applicant.getName());

        welcomeLabel.setFont(new Font(14));
        welcomeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button profileBtn = new Button("个人详情");
        Button resumeBtn = new Button("简历上传");
        Button jobListBtn = new Button("查看可申请岗位");
        Button backBtn = new Button("返回角色选择");

        profileBtn.setPrefWidth(250);
        resumeBtn.setPrefWidth(250);
        jobListBtn.setPrefWidth(250);
        backBtn.setPrefWidth(250);

        profileBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        resumeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        jobListBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        // 个人详情 → 跳转到ProfileDetailView
        profileBtn.setOnAction(e -> {
            ProfileDetailView profileView = new ProfileDetailView(applicant, stage);
            stage.getScene().setRoot(profileView.getView());
            stage.setTitle("个人详情 & 简历上传");
        });

        // 简历上传 → 跳转到ProfileDetailView
        resumeBtn.setOnAction(e -> {
            ResumeUploadView resumeView = new ResumeUploadView(applicant, stage);
            stage.getScene().setRoot(resumeView.createContent());
            stage.setTitle("简历上传");
        });

        // 查看可申请岗位 → 跳转到JobListView
        jobListBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
            stage.setTitle("可申请TA岗位列表");
        });

        // 返回角色选择 → 跳转到RoleSelectView
        backBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.setScene(ThemeManager.createScene(roleSelectView.createContent(), 800, 600));
            stage.setTitle("TA Recruitment System");
        });

        VBox root = new VBox(15, title, welcomeLabel, profileBtn, resumeBtn, jobListBtn, backBtn);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }
}
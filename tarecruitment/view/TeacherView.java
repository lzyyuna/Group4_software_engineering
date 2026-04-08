package com.group4.tarecruitment.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TeacherView {

    private final Stage stage;
    private final String moUsername;

    public TeacherView(Stage stage, String moUsername) {
        this.stage = stage;
        this.moUsername = moUsername;
    }

    public Parent createContent() {
        Label title = new Label("Teacher Dashboard");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label welcomeLabel = new Label("Welcome, " + moUsername);
        welcomeLabel.setFont(new Font(14));
        welcomeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button publishBtn = new Button("Post TA Position");
        Button viewJobsBtn = new Button("View My Posted Positions");
        Button reviewBtn = new Button("Review Applications");
        Button backBtn = new Button("Back to Role Selection");

        publishBtn.setPrefWidth(250);
        viewJobsBtn.setPrefWidth(250);
        reviewBtn.setPrefWidth(250);
        backBtn.setPrefWidth(250);

        publishBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        viewJobsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        reviewBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        // 按钮事件
        publishBtn.setOnAction(e -> {
            // MO-001: 发布职位
            MOPostJobView postJobView = new MOPostJobView(stage, moUsername, moUsername + "@bupt.edu");
            stage.setScene(new Scene(postJobView.createContent(), 800, 600));
        });

        viewJobsBtn.setOnAction(e -> {
            // MO-002: 查看已发布职位（包含编辑和关闭功能）
            MOViewJobsView viewJobsView = new MOViewJobsView(stage, moUsername);
            stage.setScene(new Scene(viewJobsView.createContent(), 800, 600));
        });

        reviewBtn.setOnAction(e -> {
            // MO-005 & MO-006: 查看和审核申请
            MOViewApplicationsView viewAppsView = new MOViewApplicationsView(stage, moUsername);
            stage.setScene(new Scene(viewAppsView.createContent(), 800, 600));
        });

        backBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.setScene(new Scene(roleSelectView.createContent(), 800, 600));
        });

        VBox root = new VBox(15, title, welcomeLabel, publishBtn, viewJobsBtn, reviewBtn, backBtn);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }
}

package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.ProfileController;
import com.group4.tarecruitment.model.Applicant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;

public class ResumeUploadView {
    private final Applicant applicant;
    private final Stage stage;
    private final ProfileController controller = new ProfileController();

    public ResumeUploadView(Applicant applicant, Stage stage) {
        this.applicant = applicant;
        this.stage = stage;
    }

    public Parent createContent() {
        // 标题美化
        Label title = new Label("📎 简历上传");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subTitle = new Label("支持 txt/pdf/doc/docx 格式，最大 10MB");
        subTitle.setFont(new Font(14));
        subTitle.setStyle("-fx-text-fill: #7f8c8d;");

        // 按钮区域（上传/替换）
        Button uploadBtn = new Button("上传简历");
        Button replaceBtn = new Button("替换简历");

        // 按钮样式美化
        String btnStyle = "-fx-font-size: 16px; " +
                "-fx-padding: 12px 30px; " +
                "-fx-background-radius: 8; " +
                "-fx-font-weight: bold;";
        uploadBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        replaceBtn.setStyle(btnStyle + "-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox btnBox = new HBox(20, uploadBtn, replaceBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(20, 0, 20, 0));

        // 状态信息区域
        VBox statusBox = new VBox(15);
        statusBox.setPadding(new Insets(20));
        statusBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

        // ===================== 【修复：进入页面自动加载已保存的简历】 =====================
        Label statusLabel = new Label();
        Label pathLabel = new Label();

        if (applicant.getResumePath() != null && !applicant.getResumePath().isBlank()) {
            // 已经上传过
            statusLabel.setText("✅ 状态：已上传");
            statusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            pathLabel.setText("文件：" + new File(applicant.getResumePath()).getName());
            pathLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        } else {
            // 未上传
            statusLabel.setText("⏳ 状态：未上传");
            statusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
            pathLabel.setText("文件：无");
            pathLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        }

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(300);

        statusBox.getChildren().addAll(statusLabel, pathLabel, progressBar);

        // 绑定上传逻辑
        uploadBtn.setOnAction(e -> {
            controller.uploadResume(applicant, statusLabel, pathLabel, progressBar);
        });
        replaceBtn.setOnAction(e -> {
            controller.uploadResume(applicant, statusLabel, pathLabel, progressBar);
        });

        // 返回TA首页按钮
        Button backToHomeBtn = new Button("返回TA首页");
        backToHomeBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );
        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        HBox backBtnBox = new HBox(backToHomeBtn);
        backBtnBox.setAlignment(Pos.CENTER);
        backBtnBox.setPadding(new Insets(25, 0, 0, 0));

        // 根布局
        VBox root = new VBox(20, title, subTitle, btnBox, statusBox, backBtnBox);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }
}
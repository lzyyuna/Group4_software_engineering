package com.group4.tarecruitment.view;

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

public class JobDetailView {
    private final Stage stage;
    private final Applicant applicant;
    private final Job job;
    private final JobService jobService = new JobService();

    public JobDetailView(Stage stage, Applicant applicant, Job job) {
        this.stage = stage;
        this.applicant = applicant;
        this.job = job;
    }

    public Parent createContent() {
        // 标题缩小，更紧凑
        Label title = new Label("岗位详情");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label courseTitle = new Label(job.getCourseName());
        courseTitle.setFont(new Font(14));
        courseTitle.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        // 缩小信息卡片：减小内边距、行间距、字体
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        String labelStyle = "-fx-font-size: 13px; -fx-text-fill: #2c3e50;";
        infoBox.getChildren().addAll(
                createRow("岗位ID: ", job.getJobId(), labelStyle),
                createRow("课程名称: ", job.getCourseName(), labelStyle),
                createRow("岗位类型: ", job.getPositionType(), labelStyle),
                createRow("周工作量: ", job.getWeeklyWorkload() + " 小时", labelStyle),
                createRow("负责MO: ", job.getMoName(), labelStyle),
                createRow("发布时间: ", job.getReleaseTime(), labelStyle),
                createRow("截止日期: ", job.getDeadline(), labelStyle),
                createRow("技能要求: ", job.getSkillRequirements(), labelStyle),
                createRow("工作内容: ", job.getJobContent(), labelStyle)
        );

        // 缩小按钮尺寸，更紧凑
        Button applyBtn = new Button("申请该岗位");
        Button backBtn = new Button("返回岗位列表");
        Button backToHomeBtn = new Button("返回TA首页");

        String btnStyle = "-fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-font-weight: bold;";
        applyBtn.setStyle(btnStyle + "-fx-background-color: #27ae60; -fx-text-fill: white;");
        backBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        backToHomeBtn.setStyle(btnStyle + "-fx-background-color: #95a5a6; -fx-text-fill: white;");

        // 提示语放在按钮下方，确保完全可见
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        // 按钮事件（完全保留原逻辑）
        applyBtn.setOnAction(e -> {
            try {
                String appId = jobService.submitApplication(applicant.getTaId(), job.getJobId());
                if (appId == null) {
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;");
                    resultLabel.setText("❌ 你已经申请过该岗位！");
                } else {
                    resultLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: bold;");
                    resultLabel.setText("✅ 申请成功！申请ID: " + appId);
                    applyBtn.setDisable(true);
                }
            } catch (Exception ex) {
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;");
                resultLabel.setText("❌ 申请失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        // 按钮组合，缩小间距
        VBox btnBox = new VBox(8, applyBtn, backBtn, backToHomeBtn);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // 根布局：减小整体内边距，确保提示语在可视区域
        VBox root = new VBox(12, title, courseTitle, infoBox, btnBox, resultLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f6fa;");
        root.setAlignment(Pos.TOP_LEFT);

        return root;
    }

    // 生成紧凑的信息行
    private HBox createRow(String label, String value, String style) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);

        Label l1 = new Label(label);
        l1.setStyle(style + "-fx-font-weight: bold;");

        Label l2 = new Label(value);
        l2.setStyle(style);

        row.getChildren().addAll(l1, l2);
        return row;
    }
}
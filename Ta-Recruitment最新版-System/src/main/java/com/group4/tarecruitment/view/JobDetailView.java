package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.JobService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
        Label title = new Label("岗位详情：" + job.getCourseName());
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold;");

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #f8f9fa;");
        infoBox.getChildren().addAll(
                new Label("岗位ID: " + job.getJobId()),
                new Label("课程名称: " + job.getCourseName()),
                new Label("岗位类型: " + job.getPositionType()),
                new Label("周工作量: " + job.getWeeklyWorkload() + "小时"),
                new Label("负责MO: " + job.getMoName()),
                new Label("发布时间: " + job.getReleaseTime()),
                new Label("截止日期: " + job.getDeadline()),
                new Label("技能要求: " + job.getSkillRequirements()),
                new Label("工作内容: " + job.getJobContent())
        );

        Button applyBtn = new Button("申请该岗位");
        Button backBtn = new Button("返回岗位列表");
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-text-fill: red;");

        applyBtn.setOnAction(e -> {
            try {
                String appId = jobService.submitApplication(applicant.getTaId(), job.getJobId());
                if (appId == null) {
                    resultLabel.setText("❌ 你已经申请过该岗位！");
                } else {
                    resultLabel.setStyle("-fx-text-fill: green;");
                    resultLabel.setText("✅ 申请成功！申请ID: " + appId);
                    applyBtn.setDisable(true);
                }
            } catch (Exception ex) {
                resultLabel.setText("❌ 申请失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> {
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
        });

        VBox root = new VBox(15, title, infoBox, applyBtn, backBtn, resultLabel);
        root.setPadding(new Insets(20));
        return root;
    }
}
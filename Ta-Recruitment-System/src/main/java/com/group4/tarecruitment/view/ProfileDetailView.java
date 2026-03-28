package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.ProfileController;
import com.group4.tarecruitment.model.Applicant;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ProfileDetailView {
    private final Applicant applicant;
    private final Stage stage;  // 新增：用于页面跳转

    // 构造器已修改，必须传入 stage
    public ProfileDetailView(Applicant applicant, Stage stage) {
        this.applicant = applicant;
        this.stage = stage;
    }

    public Parent getView() {
        ProfileController controller = new ProfileController();

        Label title = new Label("✅ 档案创建成功 | 个人详情 & 简历上传");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight:bold; -fx-text-fill: #2ecc71;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(12);
        infoGrid.setPadding(new Insets(10));

        infoGrid.add(new Label("TA ID："), 0, 0);
        infoGrid.add(new Label(applicant.getTaId()), 1, 0);

        infoGrid.add(new Label("学号："), 0, 1);
        infoGrid.add(new Label(applicant.getStudentId()), 1, 1);

        infoGrid.add(new Label("姓名："), 0, 2);
        infoGrid.add(new Label(applicant.getName()), 1, 2);

        infoGrid.add(new Label("邮箱："), 0, 3);
        infoGrid.add(new Label(applicant.getEmail()), 1, 3);

        infoGrid.add(new Label("可授课程："), 0, 4);
        infoGrid.add(new Label(applicant.getCourses()), 1, 4);

        infoGrid.add(new Label("技能标签："), 0, 5);
        infoGrid.add(new Label(applicant.getSkillTags()), 1, 5);

        infoGrid.add(new Label("联系电话："), 0, 6);
        infoGrid.add(new Label(applicant.getContact()), 1, 6);

        // 简历区域 ==========================================
        Label resumeTitle = new Label("📎 简历上传（支持 txt/pdf/doc/docx，最大10MB）");
        resumeTitle.setStyle("-fx-font-weight:bold;");

        Button uploadBtn = new Button("上传简历");
        Button replaceBtn = new Button("替换简历");
        Label resumeStatus = new Label("状态：未上传");
        Label resumePathLabel = new Label("文件：无");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setVisible(false);

        HBox btnBox = new HBox(10, uploadBtn, replaceBtn);
        btnBox.setPadding(new Insets(5, 0, 5, 0));

        uploadBtn.setOnAction(e -> {
            controller.uploadResume(applicant, resumeStatus, resumePathLabel, progressBar);
        });
        replaceBtn.setOnAction(e -> {
            controller.uploadResume(applicant, resumeStatus, resumePathLabel, progressBar);
        });

        VBox resumeBox = new VBox(8, resumeTitle, btnBox, progressBar, resumeStatus, resumePathLabel);
        resumeBox.setStyle("-fx-padding:15; -fx-background-color:#f8f9fa;");

        // ===================== 【新增】查看可申请岗位按钮 =====================
        Button jobListBtn = new Button("查看可申请岗位");
        jobListBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px 15px;");
        jobListBtn.setOnAction(e -> {
            // 跳转到岗位列表页面
            JobListView jobListView = new JobListView(stage, applicant);
            stage.getScene().setRoot(jobListView.createContent());
            stage.setTitle("Available Positions - TA Recruitment");
        });

        // ===================== 【新增】返回登录注册页面按钮 =====================
        Button backToLoginBtn = new Button("返回登录注册页面");
        backToLoginBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px 15px;");
        backToLoginBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.getScene().setRoot(roleSelectView.createContent());
            stage.setTitle("TA Recruitment System");
        });

        // 把两个按钮放在同一行，更美观
        HBox buttonBox = new HBox(15, jobListBtn, backToLoginBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // 把按钮加入界面
        VBox root = new VBox(15, title, infoGrid, new Separator(), resumeBox, buttonBox);
        root.setPadding(new Insets(20));
        return root;
    }
}
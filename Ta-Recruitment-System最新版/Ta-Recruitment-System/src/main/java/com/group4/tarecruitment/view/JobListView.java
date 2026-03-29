package com.group4.tarecruitment.view;

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

public class JobListView {
    private final Stage stage;
    private final Applicant applicant;
    private final JobService jobService = new JobService();
    private final int PAGE_SIZE = 10;
    private int currentPage = 1;

    public JobListView(Stage stage, Applicant applicant) {
        this.stage = stage;
        this.applicant = applicant;
    }

    public Parent createContent() {
        Label title = new Label("可申请 TA 岗位列表");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold;");

        // 顶部按钮栏：刷新列表 + 我的申请记录 + 返回档案
        Button refreshBtn = new Button("刷新列表");
        Button myAppsBtn = new Button("我的申请记录");
        // 【新增】返回个人档案按钮
        Button backToProfileBtn = new Button("返回个人档案");
        HBox topBar = new HBox(10, refreshBtn, myAppsBtn, backToProfileBtn);

        // 岗位列表区域
        VBox jobListBox = new VBox(10);
        jobListBox.setPadding(new Insets(10));
        jobListBox.setStyle("-fx-background-color: #f8f9fa;");

        // 分页按钮
        HBox pageBox = new HBox(10);
        Button prevBtn = new Button("上一页");
        Button nextBtn = new Button("下一页");
        Label pageLabel = new Label("第 1 页");
        pageBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        pageBox.setStyle("-fx-alignment: center;");

        // 加载数据
        loadJobs(jobListBox, pageLabel);

        refreshBtn.setOnAction(e -> loadJobs(jobListBox, pageLabel));
        myAppsBtn.setOnAction(e -> {
            MyApplicationView myAppView = new MyApplicationView(stage, applicant);
            stage.getScene().setRoot(myAppView.createContent());
        });
        // 【新增】返回档案页面逻辑
        backToProfileBtn.setOnAction(e -> {
            ProfileDetailView profileView = new ProfileDetailView(applicant, stage);
            stage.getScene().setRoot(profileView.getView());
            stage.setTitle("Profile Details & Resume Upload");
        });

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadJobs(jobListBox, pageLabel);
            }
        });
        nextBtn.setOnAction(e -> {
            try {
                int totalPages = (int) Math.ceil((double) jobService.getActiveJobs().size() / PAGE_SIZE);
                if (currentPage < totalPages) {
                    currentPage++;
                    loadJobs(jobListBox, pageLabel);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        VBox root = new VBox(15, title, topBar, jobListBox, new Separator(), pageBox);
        root.setPadding(new Insets(20));
        return root;
    }

    private void loadJobs(VBox jobListBox, Label pageLabel) {
        jobListBox.getChildren().clear();
        try {
            List<Job> activeJobs = jobService.getActiveJobs();
            int totalPages = (int) Math.ceil((double) activeJobs.size() / PAGE_SIZE);
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, activeJobs.size());
            List<Job> pageJobs = activeJobs.subList(start, end);

            if (pageJobs.isEmpty()) {
                jobListBox.getChildren().add(new Label("暂无可申请岗位"));
                return;
            }

            for (Job job : pageJobs) {
                HBox jobItem = new HBox(15);
                jobItem.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 5;");
                jobItem.getChildren().addAll(
                        new Label("ID: " + job.getJobId()),
                        new Label("课程: " + job.getCourseName()),
                        new Label("类型: " + job.getPositionType()),
                        new Label("周工作量: " + job.getWeeklyWorkload() + "h"),
                        new Label("MO: " + job.getMoName())
                );
                Button detailBtn = new Button("查看详情");
                detailBtn.setOnAction(e -> {
                    JobDetailView detailView = new JobDetailView(stage, applicant, job);
                    stage.getScene().setRoot(detailView.createContent());
                });
                jobItem.getChildren().add(detailBtn);
                jobListBox.getChildren().add(jobItem);
            }
            pageLabel.setText(String.format("第 %d / %d 页", currentPage, totalPages));
        } catch (Exception e) {
            jobListBox.getChildren().add(new Label("加载失败: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}
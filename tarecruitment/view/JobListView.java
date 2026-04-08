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
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 按钮统一美化
        Button refreshBtn = new Button("刷新列表");
        Button myAppsBtn = new Button("我的申请记录");
        Button backToHomeBtn = new Button("返回TA首页");

        String btnStyle = "-fx-font-size: 14px; -fx-padding: 7 14; -fx-background-radius: 5; -fx-font-weight: bold;";
        refreshBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        myAppsBtn.setStyle(btnStyle + "-fx-background-color: #9b59b6; -fx-text-fill: white;");
        backToHomeBtn.setStyle(btnStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        HBox topBar = new HBox(10, refreshBtn, myAppsBtn, backToHomeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // 列表容器美化
        VBox jobListBox = new VBox(10);
        jobListBox.setPadding(new Insets(15));
        jobListBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        // 分页按钮
        HBox pageBox = new HBox(10);
        Button prevBtn = new Button("上一页");
        Button nextBtn = new Button("下一页");
        Label pageLabel = new Label("第 1 页");
        pageBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        pageBox.setAlignment(Pos.CENTER);

        // 加载数据
        loadJobs(jobListBox, pageLabel);

        refreshBtn.setOnAction(e -> loadJobs(jobListBox, pageLabel));
        myAppsBtn.setOnAction(e -> {
            MyApplicationView myAppView = new MyApplicationView(stage, applicant);
            stage.getScene().setRoot(myAppView.createContent());
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
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
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f6fa;");
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
                Label emptyLabel = new Label("暂无可申请岗位");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
                jobListBox.getChildren().add(emptyLabel);
                return;
            }

            for (Job job : pageJobs) {
                HBox jobItem = new HBox(15);
                jobItem.setAlignment(Pos.CENTER_LEFT);
                jobItem.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");

                Label idLabel = new Label("ID: " + job.getJobId());
                Label courseLabel = new Label("课程: " + job.getCourseName());
                Label typeLabel = new Label("类型: " + job.getPositionType());
                Label workloadLabel = new Label("周工作量: " + job.getWeeklyWorkload() + "h");
                Label moLabel = new Label("MO: " + job.getMoName());

                Button detailBtn = new Button("查看详情");
                detailBtn.setStyle("-fx-font-size: 13px; -fx-padding: 6 12; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

                detailBtn.setOnAction(e -> {
                    JobDetailView detailView = new JobDetailView(stage, applicant, job);
                    stage.getScene().setRoot(detailView.createContent());
                });

                jobItem.getChildren().addAll(idLabel, courseLabel, typeLabel, workloadLabel, moLabel, detailBtn);
                jobListBox.getChildren().add(jobItem);
            }
            pageLabel.setText(String.format("第 %d / %d 页", currentPage, totalPages));
        } catch (Exception e) {
            jobListBox.getChildren().add(new Label("加载失败: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}
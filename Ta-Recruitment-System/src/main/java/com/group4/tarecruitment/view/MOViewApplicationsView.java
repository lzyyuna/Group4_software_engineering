package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.SkillMatchResult;
import com.group4.tarecruitment.service.AISkillMatchService;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.MOService;
import com.group4.tarecruitment.service.SkillMatchService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.group4.tarecruitment.util.ThemeManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MOViewApplicationsView {

    private final Stage stage;
    private final String moName;
    private final MOService moService = new MOService();
    private final ApplicantService applicantService = new ApplicantService();
    private final SkillMatchService skillMatchService = new SkillMatchService();
    
    private final AISkillMatchService aiSkillMatchService;
    private final String apiKey;

    private VBox appListBox;
    private ObservableList<Application> appData;
    private ComboBox<Job> jobComboBox;
    private ComboBox<String> statusFilter;
    private ComboBox<String> sortByCombo;
    private CheckBox strongMatchCb;

    private final int PAGE_SIZE = 3;
    private int currentPage = 1;

    public MOViewApplicationsView(Stage stage, String moName) {
        this.stage = stage;
        this.moName = moName;
        this.apiKey = AISkillMatchService.getApiKeyFromEnv();
        this.aiSkillMatchService = (apiKey != null) ? new AISkillMatchService(apiKey) : null;
    }

    public Parent createContent() {
        Label title = new Label("Review TA Applications");
        title.getStyleClass().add("mo-applications-title");
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button refreshBtn = new Button("Refresh List");
        Button backBtn = new Button("Back to Home");
        
        String btnStyle = "-fx-font-size: 14px; -fx-padding: 7 14; -fx-background-radius: 5; -fx-font-weight: bold;";
        refreshBtn.setStyle(btnStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        backBtn.setStyle(btnStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        HBox topBar = new HBox(10, refreshBtn, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // ===================== Filter Bar =====================
        Label jobLabel = new Label("Select Position:");
        jobComboBox = new ComboBox<>();
        jobComboBox.setPrefWidth(200);
        jobComboBox.setPromptText("Select a position");

        Label statusLabel = new Label("Status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");
        statusFilter.setPrefWidth(120);

        Label sortLabel = new Label("Sort by:");
        sortByCombo = new ComboBox<>();
        sortByCombo.getItems().addAll("Match Score (Desc)", "Application Time (Desc)", "Application Time (Asc)");
        sortByCombo.setValue("Match Score (Desc)");
        sortByCombo.setPrefWidth(180);

        strongMatchCb = new CheckBox("Only show strong matches");

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle(btnStyle + "-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox filterSpacer = new HBox();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        HBox filterBar = new HBox(15, jobLabel, jobComboBox, statusLabel, statusFilter, sortLabel, sortByCombo, strongMatchCb, filterSpacer, resetBtn);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10, 0, 10, 0));

        // 创建申请列表容器（卡片式布局）
        appListBox = new VBox(12);
        appListBox.setPadding(new Insets(10));
        appListBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        // 分页控件
        HBox pageBox = new HBox(10);
        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");
        Label pageLabel = new Label("Page 1");
        pageBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        pageBox.setAlignment(Pos.CENTER);

        // 加载职位列表
        loadJobs();

        // 按钮事件
        refreshBtn.setOnAction(e -> {
            currentPage = 1;
            loadApplications(pageLabel);
        });

        resetBtn.setOnAction(e -> {
            statusFilter.setValue("All");
            sortByCombo.setValue("Match Score (Desc)");
            strongMatchCb.setSelected(false);
            currentPage = 1;
            loadApplications(pageLabel);
        });

        backBtn.setOnAction(e -> {
            TeacherView teacherView = new TeacherView(stage, moName);
            stage.setScene(ThemeManager.createScene(teacherView.createContent(), 1000, 700));
        });

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadApplications(pageLabel);
            }
        });

        nextBtn.setOnAction(e -> {
            try {
                int totalPages = getTotalPages();
                if (currentPage < totalPages) {
                    currentPage++;
                    loadApplications(pageLabel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 职位选择变化时刷新
        jobComboBox.setOnAction(e -> {
            currentPage = 1;
            loadApplications(pageLabel);
        });

        statusFilter.setOnAction(e -> {
            currentPage = 1;
            loadApplications(pageLabel);
        });

        sortByCombo.setOnAction(e -> {
            currentPage = 1;
            loadApplications(pageLabel);
        });

        strongMatchCb.setOnAction(e -> {
            currentPage = 1;
            loadApplications(pageLabel);
        });

        VBox root = new VBox(12, title, topBar, filterBar, appListBox, new Separator(), pageBox);
        root.setPadding(new Insets(20, 25, 18, 25));
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    private void loadJobs() {
        try {
            List<Job> jobs = moService.getMyPostedJobs(moName);
            jobComboBox.getItems().clear();
            jobComboBox.getItems().addAll(jobs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getTotalPages() throws Exception {
        List<Application> filteredApps = getFilteredApplications();
        if (filteredApps.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) filteredApps.size() / PAGE_SIZE);
    }

    private List<Application> getFilteredApplications() throws Exception {
        Job selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            return new ArrayList<>();
        }

        List<Application> apps = moService.getJobApplications(selectedJob.getJobId(), moName);

        // 状态过滤
        String filter = statusFilter.getValue();
        if (!"All".equals(filter)) {
            apps = apps.stream()
                    .filter(a -> a.getStatus().equals(filter))
                    .toList();
        }

        // 强匹配过滤
        if (strongMatchCb.isSelected()) {
            apps = apps.stream().filter(app -> {
                try {
                    Applicant applicant = applicantService.getApplicantById(app.getTaId());
                    SkillMatchResult match = skillMatchService.match(applicant, selectedJob);
                    return "Strong Match".equals(match.getRecommendationLevel());
                } catch (Exception e) {
                    return false;
                }
            }).toList();
        }

        // 排序
        String sortBy = sortByCombo.getValue();
        if ("Match Score (Desc)".equals(sortBy)) {
            apps = new ArrayList<>(apps);
            apps.sort((a1, a2) -> {
                try {
                    Applicant app1 = applicantService.getApplicantById(a1.getTaId());
                    Applicant app2 = applicantService.getApplicantById(a2.getTaId());
                    Job job = selectedJob;
                    double s1 = skillMatchService.match(app1, job).getMatchScore();
                    double s2 = skillMatchService.match(app2, job).getMatchScore();
                    return Double.compare(s2, s1);
                } catch (Exception e) {
                    return 0;
                }
            });
        } else if ("Application Time (Desc)".equals(sortBy)) {
            apps = new ArrayList<>(apps);
            apps.sort((a1, a2) -> a2.getApplicationTime().compareTo(a1.getApplicationTime()));
        } else if ("Application Time (Asc)".equals(sortBy)) {
            apps = new ArrayList<>(apps);
            apps.sort((a1, a2) -> a1.getApplicationTime().compareTo(a2.getApplicationTime()));
        }

        return apps;
    }

    private void loadApplications(Label pageLabel) {
        appListBox.getChildren().clear();
        
        Job selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            Label selectJobLabel = new Label("Please select a position first");
            selectJobLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
            appListBox.getChildren().add(selectJobLabel);
            pageLabel.setText("Page 1 / 1");
            return;
        }

        try {
            List<Application> filteredApps = getFilteredApplications();
            int totalPages = filteredApps.isEmpty() ? 1 : (int) Math.ceil((double) filteredApps.size() / PAGE_SIZE);

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            if (currentPage < 1) {
                currentPage = 1;
            }

            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, filteredApps.size());

            List<Application> pageApps = filteredApps.isEmpty() ? new ArrayList<>() : filteredApps.subList(start, end);

            if (pageApps.isEmpty()) {
                Label emptyLabel = new Label("No Matched Applications");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7f8c8d;");
                appListBox.getChildren().add(emptyLabel);
                pageLabel.setText("Page 1 / 1");
                return;
            }

            for (Application app : pageApps) {
                Applicant applicant = applicantService.getApplicantById(app.getTaId());
                SkillMatchResult match = skillMatchService.match(applicant, selectedJob);

                // 创建卡片
                VBox card = new VBox(8);
                card.setPadding(new Insets(12));
                card.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");

                // 头部信息
                HBox headerRow = new HBox();
                headerRow.setAlignment(Pos.CENTER_LEFT);
                
                Label taIdLabel = new Label("TA ID: " + safe(app.getTaId()));
                taIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                
                Label nameLabel = new Label("Name: " + safe(applicant != null ? applicant.getName() : "Unknown"));
                nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                
                HBox headerSpacer = new HBox();
                HBox.setHgrow(headerSpacer, Priority.ALWAYS);
                
                Label statusBadge = new Label(safe(app.getStatus()));
                statusBadge.setStyle(getStatusBadgeStyle(app.getStatus()));
                
                headerRow.getChildren().addAll(taIdLabel, nameLabel, headerSpacer, statusBadge);

                // 基本信息
                HBox infoRow = new HBox(20);
                infoRow.setAlignment(Pos.CENTER_LEFT);
                
                Label emailLabel = new Label("Email: " + safe(applicant != null ? applicant.getEmail() : "N/A"));
                emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
                
                Label phoneLabel = new Label("Phone: " + safe(applicant != null ? applicant.getPhone() : "N/A"));
                phoneLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
                
                Label timeLabel = new Label("Applied: " + safe(app.getApplicationTime()));
                timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
                
                infoRow.getChildren().addAll(emailLabel, phoneLabel, timeLabel);

                // 技能匹配信息
                VBox matchSection = new VBox(4);
                matchSection.setPadding(new Insets(8, 0, 8, 0));
                matchSection.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1 0;");
                
                HBox matchRow = new HBox(20);
                matchRow.setAlignment(Pos.CENTER_LEFT);
                
                Label matchScoreLabel = new Label("Match Score: " + String.format("%.1f", match.getMatchScore()) + "%");
                matchScoreLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                
                Label recommendationLabel = new Label(safe(match.getRecommendationLevel()));
                recommendationLabel.setStyle(getRecommendationBadgeStyle(match.getRecommendationLevel()));
                
                matchRow.getChildren().addAll(matchScoreLabel, recommendationLabel);
                
                Label matchedSkillsLabel = new Label("Matched Skills: " + formatList(match.getMatchedSkills()));
                matchedSkillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
                
                Label missingSkillsLabel = new Label("Missing Skills: " + formatList(match.getMissingSkills()));
                missingSkillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                
                matchSection.getChildren().addAll(matchRow, matchedSkillsLabel, missingSkillsLabel);

                // 评论信息
                Label commentLabel = new Label();
                if (app.getReviewComment() != null && !app.getReviewComment().isEmpty()) {
                    commentLabel.setText("Review Comment: " + app.getReviewComment());
                    commentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e; -fx-font-style: italic;");
                }

                // 操作按钮
                HBox actionRow = new HBox(10);
                actionRow.setAlignment(Pos.CENTER_LEFT);
                
                Button viewProfileBtn = new Button("View Profile");
                viewProfileBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 12; -fx-background-color: #3498db; "
                        + "-fx-text-fill: white; -fx-background-radius: 5;");
                
                Button aiAnalysisBtn = new Button("AI Analysis");
                aiAnalysisBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 12; -fx-background-color: #9b59b6; "
                        + "-fx-text-fill: white; -fx-background-radius: 5;");
                
                if (aiSkillMatchService == null) {
                    aiAnalysisBtn.setDisable(true);
                    aiAnalysisBtn.setStyle(aiAnalysisBtn.getStyle() + "; -fx-opacity: 0.5;");
                }
                
                Button approveBtn = new Button("Approve");
                approveBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 12; -fx-background-color: #27ae60; "
                        + "-fx-text-fill: white; -fx-background-radius: 5;");
                
                Button rejectBtn = new Button("Reject");
                rejectBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 12; -fx-background-color: #e74c3c; "
                        + "-fx-text-fill: white; -fx-background-radius: 5;");
                
                if (!"Pending".equals(app.getStatus())) {
                    approveBtn.setDisable(true);
                    rejectBtn.setDisable(true);
                    approveBtn.setStyle(approveBtn.getStyle() + "; -fx-opacity: 0.5;");
                    rejectBtn.setStyle(rejectBtn.getStyle() + "; -fx-opacity: 0.5;");
                }
                
                HBox actionSpacer = new HBox();
                HBox.setHgrow(actionSpacer, Priority.ALWAYS);
                
                actionRow.getChildren().addAll(viewProfileBtn, aiAnalysisBtn, actionSpacer, approveBtn, rejectBtn);

                // 添加到卡片
                card.getChildren().addAll(headerRow, infoRow, matchSection);
                if (commentLabel.getText() != null && !commentLabel.getText().isEmpty()) {
                    card.getChildren().add(commentLabel);
                }
                card.getChildren().add(actionRow);

                appListBox.getChildren().add(card);

                // 按钮事件
                viewProfileBtn.setOnAction(e -> showTAProfile(app.getTaId()));
                
                aiAnalysisBtn.setOnAction(e -> showAIAnalysis(applicant, selectedJob, match));
                
                approveBtn.setOnAction(e -> reviewApplication(app, "Approved"));
                
                rejectBtn.setOnAction(e -> reviewApplication(app, "Rejected"));
            }

            pageLabel.setText(String.format("Page %d / %d", currentPage, totalPages));
        } catch (Exception e) {
            appListBox.getChildren().add(new Label("Load failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private String getStatusBadgeStyle(String status) {
        String bgColor;
        String textColor;
        
        switch (status) {
            case "Approved":
                bgColor = "#e8f5e9";
                textColor = "#1b5e20";
                break;
            case "Rejected":
                bgColor = "#fdecea";
                textColor = "#c62828";
                break;
            case "Pending":
            default:
                bgColor = "#fff8e1";
                textColor = "#e65100";
                break;
        }
        
        return "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + textColor + "; "
                + "-fx-background-color: " + bgColor + "; -fx-background-radius: 4; -fx-padding: 3 8;";
    }

    private String getRecommendationBadgeStyle(String recommendationLevel) {
        String level = recommendationLevel == null ? "" : recommendationLevel;
        String backgroundColor;
        String textColor;

        switch (level) {
            case "Strong Match":
                backgroundColor = "#e8f5e9";
                textColor = "#1b5e20";
                break;
            case "Moderate Match":
                backgroundColor = "#fff8e1";
                textColor = "#e65100";
                break;
            case "Weak Match":
                backgroundColor = "#fdecea";
                textColor = "#c62828";
                break;
            default:
                backgroundColor = "#eceff1";
                textColor = "#455a64";
                break;
        }

        return "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-background-color: " + backgroundColor + ";"
                + "-fx-background-radius: 4;"
                + "-fx-padding: 4 10;";
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "None";
        }
        return String.join(", ", list);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showTAProfile(String taId) {
        try {
            var applicant = applicantService.getApplicantById(taId);
            if (applicant != null) {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("TA Profile");
                dialog.setHeaderText("TA ID: " + taId);

                VBox content = new VBox();
                content.getStyleClass().add("profile-content");

                VBox profileSection = new VBox(16);
                profileSection.getStyleClass().add("profile-section");

                createStyledInfoRow(profileSection, "Name:", applicant.getName() != null ? applicant.getName() : "N/A");
                createStyledInfoRow(profileSection, "Email:", applicant.getEmail() != null ? applicant.getEmail() : "N/A");
                createStyledInfoRow(profileSection, "Phone:", applicant.getPhone() != null ? applicant.getPhone() : "N/A");
                createStyledInfoRow(profileSection, "Skills:", applicant.getSkills() != null ? applicant.getSkills() : "N/A");
                createStyledInfoRow(profileSection, "Skill Tags:", applicant.getSkillTags() != null ? applicant.getSkillTags() : "N/A");
                createStyledInfoRow(profileSection, "Courses:", applicant.getCourses() != null ? applicant.getCourses() : "N/A");

                VBox cvSection = new VBox(16);
                cvSection.getStyleClass().add("cv-section");
                
                HBox cvBox = new HBox(12);
                cvBox.getStyleClass().add("cv-box");
                
                Label cvPathLabel = new Label(applicant.getCvPath() != null ? applicant.getCvPath() : "N/A");
                cvPathLabel.getStyleClass().add("cv-path-label");
                
                Button downloadBtn = new Button("Download CV");
                downloadBtn.getStyleClass().add("download-btn");

                if (applicant.getCvPath() == null || applicant.getCvPath().isEmpty()) {
                    downloadBtn.setDisable(true);
                }

                downloadBtn.setOnAction(e -> downloadCV(applicant.getCvPath()));
                cvBox.getChildren().addAll(cvPathLabel, downloadBtn);
                cvSection.getChildren().add(cvBox);

                content.getChildren().addAll(profileSection, cvSection);

                ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().add(closeButtonType);

                dialog.getDialogPane().getStyleClass().add("ta-profile-dialog");
                dialog.getDialogPane().setContent(content);
                
                String css = getClass().getResource("/styles/app-theme.css").toExternalForm();
                dialog.getDialogPane().getStylesheets().add(css);
                
                dialog.getDialogPane().setPrefWidth(550);
                dialog.getDialogPane().setPrefHeight(450);
                
                dialog.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Found");
                alert.setContentText("TA profile not found!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAIAnalysis(Applicant applicant, Job job, SkillMatchResult match) {
        if (aiSkillMatchService == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AI Analysis Unavailable");
            alert.setContentText("AI analysis is not available because no API key was found. Please configure the DeepSeek API key.");
            alert.showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("AI Skill Match Analysis");
        dialog.setHeaderText(null);

        VBox mainContent = new VBox(16);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #f5f6fa;");

        // Header with gradient background
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #11998e, #38ef7d); -fx-background-radius: 8; -fx-padding: 16;");
        
        Label iconLabel = new Label("🤖");
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("AI Skill Match Analysis");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitleLabel = new Label(safe(applicant.getName()) + " → " + safe(job.getCourseName()));
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.9);");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        headerBox.getChildren().addAll(iconLabel, titleBox);

        // Match Score Card
        VBox scoreCard = new VBox(12);
        scoreCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");
        scoreCard.setAlignment(Pos.CENTER);
        
        Label scoreTitle = new Label("📊 Match Overview");
        scoreTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox scoreRow = new HBox(30);
        scoreRow.setAlignment(Pos.CENTER);
        
        VBox scoreBox = new VBox(4);
        scoreBox.setAlignment(Pos.CENTER);
        Label scoreValue = new Label(String.format("%.1f%%", match.getMatchScore()));
        scoreValue.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + getScoreColor(match.getMatchScore()) + ";");
        Label scoreLabel = new Label("Match Score");
        scoreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        scoreBox.getChildren().addAll(scoreValue, scoreLabel);
        
        VBox levelBox = new VBox(4);
        levelBox.setAlignment(Pos.CENTER);
        Label levelValue = new Label(safe(match.getRecommendationLevel()));
        levelValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + getLevelColor(match.getRecommendationLevel()) + "; -fx-padding: 8 16; -fx-background-color: " + getLevelBgColor(match.getRecommendationLevel()) + "; -fx-background-radius: 20;");
        Label levelLabel = new Label("Recommendation");
        levelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        levelBox.getChildren().addAll(levelValue, levelLabel);
        
        scoreRow.getChildren().addAll(scoreBox, levelBox);
        
        // Skills display
        HBox skillsRow = new HBox(20);
        skillsRow.setAlignment(Pos.CENTER);
        
        VBox matchedBox = new VBox(6);
        matchedBox.setAlignment(Pos.CENTER);
        Label matchedTitle = new Label("✅ Matched Skills");
        matchedTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        Label matchedValue = new Label(formatList(match.getMatchedSkills()));
        matchedValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        matchedBox.getChildren().addAll(matchedTitle, matchedValue);
        
        VBox missingBox = new VBox(6);
        missingBox.setAlignment(Pos.CENTER);
        Label missingTitle = new Label("❌ Missing Skills");
        missingTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        Label missingValue = new Label(formatList(match.getMissingSkills()));
        missingValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        missingBox.getChildren().addAll(missingTitle, missingValue);
        
        skillsRow.getChildren().addAll(matchedBox, missingBox);
        
        scoreCard.getChildren().addAll(scoreTitle, new Separator(), scoreRow, skillsRow);

        // AI Analysis Result Card
        VBox analysisCard = new VBox(12);
        analysisCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);");
        
        Label analysisTitle = new Label("💡 AI Analysis");
        analysisTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TextArea analysisArea = new TextArea();
        analysisArea.setEditable(false);
        analysisArea.setWrapText(true);
        analysisArea.setPrefHeight(200);
        analysisArea.setStyle("-fx-font-size: 13px; -fx-background-color: #f8f9fa; -fx-background-radius: 6; -fx-border-color: #e0e0e0; -fx-border-radius: 6;");
        analysisArea.setText("🔄 Analyzing... Please wait...");
        
        analysisCard.getChildren().addAll(analysisTitle, new Separator(), analysisArea);

        mainContent.getChildren().addAll(headerBox, scoreCard, analysisCard);

        // 异步执行AI分析
        new Thread(() -> {
            try {
                String analysis = aiSkillMatchService.analyzeSkillMatch(applicant, job, match);
                javafx.application.Platform.runLater(() -> {
                    analysisArea.setText(analysis);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    analysisArea.setText("❌ AI analysis failed:\n" + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        dialog.getDialogPane().setContent(mainContent);

        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(650);

        dialog.showAndWait();
    }
    
    private String getScoreColor(double score) {
        if (score >= 80) return "#27ae60";
        if (score >= 50) return "#f39c12";
        return "#e74c3c";
    }
    
    private String getLevelColor(String level) {
        if ("Strong Match".equals(level)) return "#1b5e20";
        if ("Moderate Match".equals(level)) return "#e65100";
        if ("Weak Match".equals(level)) return "#c62828";
        return "#455a64";
    }
    
    private String getLevelBgColor(String level) {
        if ("Strong Match".equals(level)) return "#e8f5e9";
        if ("Moderate Match".equals(level)) return "#fff8e1";
        if ("Weak Match".equals(level)) return "#fdecea";
        return "#eceff1";
    }

    private void createStyledInfoRow(VBox parent, String labelText, String value) {
        HBox row = new HBox(16);
        row.getStyleClass().add("profile-info-row");
        
        Label label = new Label(labelText);
        label.getStyleClass().add("profile-info-label");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("profile-info-value");
        
        row.getChildren().addAll(label, valueLabel);
        parent.getChildren().add(row);
    }

    private void downloadCV(String cvPath) {
        if (cvPath == null || cvPath.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No CV");
            alert.setContentText("No CV file available for download!");
            alert.showAndWait();
            return;
        }

        try {
            String fileName = cvPath;
            if (cvPath.contains("\\")) {
                fileName = cvPath.substring(cvPath.lastIndexOf("\\") + 1);
            } else if (cvPath.contains("/")) {
                fileName = cvPath.substring(cvPath.lastIndexOf("/") + 1);
            }
            
            String[] possiblePaths = {
                "data/resumes/" + fileName,
                "../data/resumes/" + fileName,
                "TA-Recruitment-System/data/resumes/" + fileName,
                "../TA-Recruitment-System/data/resumes/" + fileName,
                "../../data/resumes/" + fileName,
                "../../TA-Recruitment-System/data/resumes/" + fileName,
                "resumes/" + fileName,
                "../resumes/" + fileName
            };

            File cvFile = null;

            for (String path : possiblePaths) {
                File tempFile = new File(path);
                if (tempFile.exists()) {
                    cvFile = tempFile;
                    break;
                }
            }

            if (cvFile == null || !cvFile.exists()) {
                try {
                    java.net.URL resourceUrl = getClass().getResource("/resumes/" + fileName);
                    if (resourceUrl != null) {
                        cvFile = new File(resourceUrl.toURI());
                    }
                } catch (Exception e) {
                }
            }

            if (cvFile == null || !cvFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Not Found");
                alert.setContentText("CV file not found. Please ensure the file exists in the data/resumes directory.");
                alert.showAndWait();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CV File");
            fileChooser.setInitialFileName(cvFile.getName());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
            );

            File saveFile = fileChooser.showSaveDialog(stage);

            if (saveFile != null) {
                Files.copy(
                        cvFile.toPath(),
                        saveFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Download Successful");
                alert.setContentText("CV file downloaded successfully to:\n" + saveFile.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Download Failed");
            alert.setContentText("Failed to download CV file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void reviewApplication(Application app, String result) {
        if (!"Pending".equals(app.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Only 'Pending' applications can be reviewed!");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Review Application");
        dialog.setHeaderText(result + " Application: " + app.getApplicationId());
        dialog.setContentText("Review Comment (optional, max 50 chars):");

        dialog.showAndWait().ifPresent(comment -> {
            if (comment.length() > 50) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Comment must be 50 characters or less!");
                alert.showAndWait();
                return;
            }

            try {
                boolean success = moService.reviewApplication(app.getApplicationId(), moName, result, comment);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("Application " + result.toLowerCase() + " successfully!");
                    alert.showAndWait();
                    currentPage = 1;
                    loadApplications(new Label());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to review application!");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });
    }
}
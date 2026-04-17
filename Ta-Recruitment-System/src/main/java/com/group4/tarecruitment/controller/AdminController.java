package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.JobService;
import com.group4.tarecruitment.util.ThemeManager;
import com.group4.tarecruitment.view.RoleSelectView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private final Stage stage;
    private final ApplicantService applicantService = new ApplicantService();
    private final JobService jobService = new JobService();

    public AdminController(Stage stage) {
        this.stage = stage;
    }

    public void showAdminPanel() {
        Label title = new Label("Admin Dashboard");
        title.getStyleClass().add("page-title");

        Label welcomeLabel = new Label("Welcome, Admin");
        welcomeLabel.getStyleClass().add("dashboard-welcome");

        Button workloadBtn = new Button("Check TA Workload");
        Button manageBtn = new Button("Manage System");
        Button backBtn = new Button("Back to Role Selection");

        workloadBtn.getStyleClass().add("primary-button");
        manageBtn.getStyleClass().add("secondary-button");
        backBtn.getStyleClass().add("neutral-button");

        workloadBtn.setPrefWidth(260);
        manageBtn.setPrefWidth(260);
        backBtn.setPrefWidth(260);

        workloadBtn.setOnAction(e -> {
            stage.setTitle("TA Workload View");
            stage.setScene(createTaWorkloadScene());
        });

        manageBtn.setOnAction(e -> {
            stage.setTitle("User Management");
            stage.setScene(createUserManagementScene());
        });

        backBtn.setOnAction(e -> goBackToRoleSelect());

        VBox hero = new VBox(8, title, welcomeLabel);
        hero.getStyleClass().add("dashboard-hero");
        hero.setMaxWidth(560);

        VBox actions = new VBox(14, workloadBtn, manageBtn, backBtn);
        actions.getStyleClass().add("dashboard-card");
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, hero, actions);
        root.getStyleClass().add("dashboard-page");
        root.setPadding(new Insets(36));
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(ThemeManager.createScene(root, 1000, 700));
        stage.show();
    }

    private void goBackToRoleSelect() {
        RoleSelectView roleView = new RoleSelectView(stage);
        stage.setScene(ThemeManager.createScene(roleView.createContent(), 1000, 700));
    }

    private List<Admin> computeWorkload() {
        List<Admin> result = new ArrayList<>();
        try {
            List<Applicant> applicants = applicantService.getAllApplicants();
            List<Job> jobs = jobService.getActiveJobs();

            for (Applicant a : applicants) {
                for (Job j : jobs) {
                    if (a.getCourses() != null
                            && j.getCourseName() != null
                            && a.getCourses().contains(j.getCourseName())) {
                        Admin item = new Admin();
                        item.setTaId(a.getTaId());
                        item.setTaName(a.getName());
                        item.setCourseName(j.getCourseName());
                        item.setWeeklyWorkload(j.getWeeklyWorkload());
                        result.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Scene createTaWorkloadScene() {
        com.group4.tarecruitment.view.AdminWorkloadView view = new com.group4.tarecruitment.view.AdminWorkloadView(stage, this::showAdminPanel);
        return ThemeManager.createScene(view.createContent(), 1000, 700);
    }

    private Scene createUserManagementScene() {
        com.group4.tarecruitment.view.AdminUserView view = new com.group4.tarecruitment.view.AdminUserView(stage, this::showAdminPanel);
        return ThemeManager.createScene(view.createContent(), 1000, 700);
    }
}

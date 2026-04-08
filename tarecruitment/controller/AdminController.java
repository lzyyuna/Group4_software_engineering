package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.JobService;
import com.group4.tarecruitment.view.RoleSelectView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label welcomeLabel = new Label("Welcome, Admin");
        welcomeLabel.setFont(new Font(14));
        welcomeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button workloadBtn = new Button("Check TA Workload");
        Button manageBtn = new Button("Manage System");
        Button backBtn = new Button("Back to Role Selection");

        workloadBtn.setPrefWidth(250);
        manageBtn.setPrefWidth(250);
        backBtn.setPrefWidth(250);

        workloadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        manageBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        workloadBtn.setOnAction(e -> {
            System.out.println("[Admin] Clicked Check TA Workload");
            stage.setTitle("TA Workload View");
            stage.setScene(createTaWorkloadScene());
        });

        manageBtn.setOnAction(e -> {
            System.out.println("[Admin] Clicked Manage System");
            stage.setTitle("User Management");
            stage.setScene(createUserManagementScene());
        });

        backBtn.setOnAction(e -> {
            System.out.println("[Admin] Clicked Back to Role Selection");
            goBackToRoleSelect();
        });

        VBox root = new VBox(15, title, welcomeLabel, workloadBtn, manageBtn, backBtn);
        root.setPadding(new Insets(30));
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    private void goBackToRoleSelect() {
        RoleSelectView roleView = new RoleSelectView(stage);
        stage.setScene(new Scene(roleView.createContent(), 800, 600));
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
        com.group4.tarecruitment.view.AdminWorkloadView view = new com.group4.tarecruitment.view.AdminWorkloadView(stage, () -> {
            showAdminPanel();
        });
        return new Scene(view.createContent(), 800, 600);
    }

    private Scene createUserManagementScene() {
        com.group4.tarecruitment.view.AdminUserView view = new com.group4.tarecruitment.view.AdminUserView(stage, () -> {
            showAdminPanel();
        });
        return new Scene(view.createContent(), 800, 600);
    }
}
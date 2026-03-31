package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RoleSelectView {
    private final Stage stage;
    private final AuthService authService = new AuthService();

    public RoleSelectView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("TA Recruitment System");
        title.setFont(new Font(24));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Login or register with username, password, and role");
        subtitle.setFont(new Font(16));
        subtitle.setStyle("-fx-text-fill: #34495e;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(300);

        Button loginTaBtn = new Button("Login as TA");
        Button loginMoBtn = new Button("Login as MO");
        Button loginAdminBtn = new Button("Login as Admin");
        Button regTaBtn = new Button("Register as TA");
        Button regMoBtn = new Button("Register as MO");
        Button regAdminBtn = new Button("Register as Admin");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // 登录按钮事件
        loginTaBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "TA"));
        loginMoBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "MO"));
        loginAdminBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "Admin"));

        // 注册按钮事件
        regTaBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "TA"));
        regMoBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "MO"));
        regAdminBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "Admin"));

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #ecf0f1;");
        root.getChildren().addAll(
                title,
                subtitle,
                usernameField,
                passwordField,
                loginTaBtn,
                loginMoBtn,
                loginAdminBtn,
                regTaBtn,
                regMoBtn,
                regAdminBtn,
                messageLabel
        );

        return root;
    }

    // 登录逻辑（完全保留你原来的验证逻辑）
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel, String role) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authService.login(username, password, role);
        if (!success) {
            messageLabel.setText("Invalid " + role + " username or password.");
            return;
        }

        messageLabel.setText("");

        if (role.equals("TA")) {
            try {
                ApplicantService applicantService = new ApplicantService();
                Applicant existingApplicant = applicantService.getApplicantByUsername(username);

                if (existingApplicant != null) {
                    // =============== 【已修改】有档案 → 跳转到 TA Dashboard ===============
                    TAHomeView taHomeView = new TAHomeView(stage, existingApplicant);
                    stage.getScene().setRoot(taHomeView.createContent());
                    stage.setTitle("TA Dashboard");
                } else {
                    // =============== 无档案 → 跳转到创建档案页（不变） ===============
                    HelloView helloView = new HelloView(stage);
                    helloView.setLoginUsername(username);
                    stage.getScene().setRoot(helloView.createContent());
                    stage.setTitle("Create Profile");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Error loading profile: " + ex.getMessage());
            }
        } else if (role.equals("MO")) {
            TeacherView teacherView = new TeacherView(stage, username);
            stage.getScene().setRoot(teacherView.createContent());
        } else if (role.equals("Admin")) {
            AdminView adminView = new AdminView(stage);
            stage.getScene().setRoot(adminView.createContent());
        }
    }

    // 注册逻辑（完全保留你原来的验证逻辑）
    private void handleRegister(TextField usernameField, PasswordField passwordField, Label messageLabel, String role) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authService.register(username, password, role);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Registration successful! Please login.");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Registration failed: Username already exists or invalid input.");
        }
    }
}
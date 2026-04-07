package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.AuthService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RoleSelectView {
    private final Stage stage;
    private final AuthService authService = new AuthService();
    private static final String[] ROLES = {"TA", "MO", "Admin"};

    private enum AuthMode {
        LOGIN,
        REGISTER
    }

    public RoleSelectView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("TA Recruitment System");
        title.setFont(new Font(26));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("注册、登录");
        subtitle.setFont(new Font(15));
        subtitle.setStyle("-fx-text-fill: #4b5563;");

        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(createHomePane(contentPane));

        VBox root = new VBox(18, title, subtitle, contentPane);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(28, 22, 28, 22));
        root.setStyle("-fx-background-color: #f5f7fb;");

        return root;
    }

    private Parent createHomePane(StackPane contentPane) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);");

        Label tip = new Label("请选择入口");
        tip.setFont(new Font(16));
        tip.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold;");

        Button registerBtn = new Button("注册");
        Button loginBtn = new Button("登录");

        registerBtn.setPrefWidth(260);
        loginBtn.setPrefWidth(260);

        String btnBase = "-fx-font-size: 14px; -fx-padding: 12 18; -fx-background-radius: 10; -fx-font-weight: bold; -fx-text-fill: white;";
        registerBtn.setStyle(btnBase + "-fx-background-color: #9b59b6;");
        loginBtn.setStyle(btnBase + "-fx-background-color: #3498db;");

        Label note = new Label("提示：请选择角色后进行注册或登录");
        note.setFont(new Font(12));
        note.setStyle("-fx-text-fill: #6b7280;");
        note.setWrapText(true);

        registerBtn.setOnAction(e -> contentPane.getChildren().setAll(createAuthPane(contentPane, AuthMode.REGISTER, null, false)));
        loginBtn.setOnAction(e -> contentPane.getChildren().setAll(createAuthPane(contentPane, AuthMode.LOGIN, null, false)));

        VBox actions = new VBox(14, registerBtn, loginBtn);
        actions.setAlignment(Pos.CENTER);

        card.getChildren().addAll(tip, actions, note);
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        return root;
    }

    private Parent createAuthPane(StackPane contentPane, AuthMode mode, String presetRole, boolean goToJobList) {
        Label title = new Label(mode == AuthMode.LOGIN ? "登录" : "注册");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label roleLabel = new Label("身份（角色）");
        roleLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 13px;");

        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(ROLES));
        roleCombo.setPrefWidth(260);
        roleCombo.setPromptText("请选择角色");
        if (presetRole != null) {
            roleCombo.setValue(presetRole);
        }

        Label usernameLabel = new Label("用户名");
        usernameLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 13px;");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(260);
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("密码");
        passwordLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 13px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(260);
        passwordField.setPromptText("Enter password");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px;");

        Button submitBtn = new Button(mode == AuthMode.LOGIN ? "登录" : "注册");
        Button backBtn = new Button("返回首页");

        submitBtn.setPrefWidth(260);
        backBtn.setPrefWidth(260);

        String submitStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;";
        if (mode == AuthMode.REGISTER) {
            submitStyle = "-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;";
        }
        submitBtn.setStyle(submitStyle);
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.getChildren().addAll(
                title,
                roleLabel,
                roleCombo,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                submitBtn,
                backBtn,
                messageLabel
        );

        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 14;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);");

        backBtn.setOnAction(e -> contentPane.getChildren().setAll(createHomePane(contentPane)));

        submitBtn.setOnAction(e -> {
            String role = roleCombo.getValue();
            if (role == null || role.trim().isEmpty()) {
                messageLabel.setText("请选择角色");
                return;
            }

            messageLabel.setText("");
            if (mode == AuthMode.LOGIN) {
                handleLogin(usernameField, passwordField, messageLabel, role, goToJobList);
            } else {
                handleRegister(usernameField, passwordField, messageLabel, role);
            }
        });

        return form;
    }

    // 登录逻辑（保留你原来的验证逻辑；新增：可选进入 TA 岗位列表）
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel, String role, boolean goToJobList) {
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
                    if (goToJobList) {
                        JobListView jobListView = new JobListView(stage, existingApplicant);
                        stage.getScene().setRoot(jobListView.createContent());
                        stage.setTitle("可申请TA岗位列表");
                    } else {
                        // =============== 【已修改】有档案 → 跳转到 TA Dashboard ===============
                        TAHomeView taHomeView = new TAHomeView(stage, existingApplicant);
                        stage.getScene().setRoot(taHomeView.createContent());
                        stage.setTitle("TA Dashboard");
                    }
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
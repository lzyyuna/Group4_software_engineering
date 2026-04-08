package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.AuthService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        StackPane root = new StackPane();
        root.getStyleClass().add("login-page");

        VBox shell = new VBox(36);
        shell.setPadding(new Insets(64, 48, 48, 48));
        shell.setAlignment(Pos.TOP_LEFT);
        shell.setFillWidth(false);

        Label title = new Label("International School Teaching Assistant Recruitment");
        title.getStyleClass().add("login-hero-title");
        title.setWrapText(true);
        title.setMaxWidth(980);

        StackPane contentPane = new StackPane();
        contentPane.setAlignment(Pos.CENTER_LEFT);
        contentPane.getChildren().add(createHomePane(contentPane));

        shell.getChildren().addAll(title, contentPane);
        root.getChildren().add(shell);
        StackPane.setAlignment(shell, Pos.TOP_LEFT);

        return root;
    }

    private Parent createHomePane(StackPane contentPane) {
        VBox card = createBaseCard();

        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(ROLES));
        roleCombo.getStyleClass().add("login-combo");
        roleCombo.setPromptText("TA/MO/Admin");
        roleCombo.setPrefWidth(300);

        Button registerBtn = new Button("Create Account");
        registerBtn.getStyleClass().addAll("login-button", "secondary-action");
        registerBtn.setPrefWidth(140);

        Button loginBtn = new Button("OK");
        loginBtn.getStyleClass().add("login-button");
        loginBtn.setPrefWidth(140);

        Label hint = new Label("No account? Contact admin to create one.");
        hint.getStyleClass().add("login-hint");
        hint.setWrapText(true);
        hint.setMaxWidth(300);

        Label caption = new Label("Please choose your role first, then continue to register or sign in.");
        caption.getStyleClass().add("login-caption");
        caption.setWrapText(true);
        caption.setMaxWidth(300);

        HBox actions = new HBox(14, loginBtn, registerBtn);
        actions.setAlignment(Pos.CENTER);

        loginBtn.setOnAction(e -> contentPane.getChildren().setAll(createAuthPane(contentPane, AuthMode.LOGIN, roleCombo.getValue(), false)));
        registerBtn.setOnAction(e -> contentPane.getChildren().setAll(createAuthPane(contentPane, AuthMode.REGISTER, roleCombo.getValue(), false)));

        card.getChildren().addAll(
                roleCombo,
                caption,
                actions,
                hint
        );

        return card;
    }

    private Parent createAuthPane(StackPane contentPane, AuthMode mode, String presetRole, boolean goToJobList) {
        VBox card = createBaseCard();
        card.setSpacing(12);

        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(ROLES));
        roleCombo.getStyleClass().add("login-combo");
        roleCombo.setPromptText("TA/MO/Admin");
        roleCombo.setPrefWidth(300);
        if (presetRole != null) {
            roleCombo.setValue(presetRole);
        }

        Label accountLabel = new Label("Account");
        accountLabel.getStyleClass().add("login-field-label");

        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("login-input");
        usernameField.setPrefWidth(300);
        usernameField.setPromptText("Enter account (email/student ID)");

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("login-field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("login-input");
        passwordField.setPrefWidth(300);
        passwordField.setPromptText("Enter password");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("login-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        Button submitBtn = new Button(mode == AuthMode.LOGIN ? "OK" : "Register");
        submitBtn.getStyleClass().add("login-button");
        submitBtn.setPrefWidth(140);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().addAll("login-button", "secondary-action");
        backBtn.setPrefWidth(140);

        Label switchHint = new Label(mode == AuthMode.LOGIN
                ? "No account? You can create one first."
                : "Already have an account? Go back and sign in.");
        switchHint.getStyleClass().add("login-hint");
        switchHint.setWrapText(true);
        switchHint.setMaxWidth(300);

        HBox actions = new HBox(14, submitBtn, backBtn);
        actions.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                roleCombo,
                accountLabel,
                usernameField,
                passwordLabel,
                passwordField,
                actions,
                messageLabel,
                switchHint
        );

        backBtn.setOnAction(e -> contentPane.getChildren().setAll(createHomePane(contentPane)));

        submitBtn.setOnAction(e -> {
            String role = roleCombo.getValue();
            if (role == null || role.trim().isEmpty()) {
                messageLabel.getStyleClass().remove("success");
                messageLabel.setText("Please select a role.");
                return;
            }

            messageLabel.getStyleClass().remove("success");
            messageLabel.setText("");
            if (mode == AuthMode.LOGIN) {
                handleLogin(usernameField, passwordField, messageLabel, role, goToJobList);
            } else {
                handleRegister(usernameField, passwordField, messageLabel, role);
            }
        });

        return card;
    }

    private VBox createBaseCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(360);
        card.setMaxWidth(360);
        return card;
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel, String role, boolean goToJobList) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.getStyleClass().remove("success");
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authService.login(username, password, role);
        if (!success) {
            messageLabel.getStyleClass().remove("success");
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
                        stage.setTitle("Available TA Positions");
                    } else {
                        TAHomeView taHomeView = new TAHomeView(stage, existingApplicant);
                        stage.getScene().setRoot(taHomeView.createContent());
                        stage.setTitle("TA Dashboard");
                    }
                } else {
                    HelloView helloView = new HelloView(stage);
                    helloView.setLoginUsername(username);
                    stage.getScene().setRoot(helloView.createContent());
                    stage.setTitle("Create Profile");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.getStyleClass().remove("success");
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

    private void handleRegister(TextField usernameField, PasswordField passwordField, Label messageLabel, String role) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.getStyleClass().remove("success");
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authService.register(username, password, role);
        if (success) {
            if (!messageLabel.getStyleClass().contains("success")) {
                messageLabel.getStyleClass().add("success");
            }
            messageLabel.setText("Registration successful. Please sign in.");
        } else {
            messageLabel.getStyleClass().remove("success");
            messageLabel.setText("Registration failed: Username already exists or invalid input.");
        }
    }
}

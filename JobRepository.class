package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoleSelectView {
    private final Stage stage;
    private final AuthService authService = new AuthService();

    public RoleSelectView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("TA Recruitment System");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Login or register with username, password, and role.");
        subtitle.getStyleClass().add("page-subtitle");

        Label introTitle = new Label("International School Teaching Assistant Recruitment");
        introTitle.getStyleClass().add("section-title");
        introTitle.setWrapText(true);

        Label introText = new Label(
                "Use one shared account portal for TA, MO, and Admin. " +
                "The current version already supports registration, login, profile creation, " +
                "job browsing, application submission, and application tracking."
        );
        introText.getStyleClass().add("page-subtitle");
        introText.setWrapText(true);

        Label chip1 = new Label("TA Profile");
        Label chip2 = new Label("Job Application");
        Label chip3 = new Label("Application Status");
        chip1.getStyleClass().add("chip");
        chip2.getStyleClass().add("chip");
        chip3.getStyleClass().add("chip");
        HBox chipBox = new HBox(10, chip1, chip2, chip3);

        VBox leftCard = new VBox(18, introTitle, introText, chipBox);
        leftCard.getStyleClass().addAll("hero-card", "page-header");
        leftCard.setPrefWidth(420);

        Label formTitle = new Label("Account Access");
        formTitle.getStyleClass().add("section-title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        GridPane fieldGrid = new GridPane();
        fieldGrid.setHgap(12);
        fieldGrid.setVgap(12);
        fieldGrid.add(createFormLabel("Username"), 0, 0);
        fieldGrid.add(usernameField, 1, 0);
        fieldGrid.add(createFormLabel("Password"), 0, 1);
        fieldGrid.add(passwordField, 1, 1);
        GridPane.setHgrow(usernameField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);

        Button loginTaBtn = buildActionButton("Login as TA", "primary-button");
        Button loginMoBtn = buildActionButton("Login as MO", "secondary-button");
        Button loginAdminBtn = buildActionButton("Login as Admin", "secondary-button");
        Button regTaBtn = buildActionButton("Register as TA", "success-button");
        Button regMoBtn = buildActionButton("Register as MO", "button");
        Button regAdminBtn = buildActionButton("Register as Admin", "button");

        loginTaBtn.setMaxWidth(Double.MAX_VALUE);
        loginMoBtn.setMaxWidth(Double.MAX_VALUE);
        loginAdminBtn.setMaxWidth(Double.MAX_VALUE);
        regTaBtn.setMaxWidth(Double.MAX_VALUE);
        regMoBtn.setMaxWidth(Double.MAX_VALUE);
        regAdminBtn.setMaxWidth(Double.MAX_VALUE);

        HBox loginRow = new HBox(10, loginTaBtn, loginMoBtn, loginAdminBtn);
        HBox registerRow = new HBox(10, regTaBtn, regMoBtn, regAdminBtn);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("muted-label");

        loginTaBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "TA"));
        loginMoBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "MO"));
        loginAdminBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "Admin"));

        regTaBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "TA"));
        regMoBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "MO"));
        regAdminBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "Admin"));

        VBox rightCard = new VBox(18, formTitle, fieldGrid, loginRow, registerRow, messageLabel);
        rightCard.getStyleClass().add("form-card");
        rightCard.setPrefWidth(520);

        HBox contentRow = new HBox(24, leftCard, rightCard);
        contentRow.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox shell = new VBox(30, contentRow, spacer);
        shell.getStyleClass().add("page-shell");
        shell.setPadding(new Insets(40));
        shell.setAlignment(Pos.CENTER);

        VBox root = new VBox(shell);
        root.getStyleClass().add("page-root");
        root.setAlignment(Pos.CENTER);

        return root;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private Button buildActionButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void showMessage(Label messageLabel, String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("status-success", "status-error", "muted-label");
        messageLabel.getStyleClass().add(success ? "status-success" : "status-error");
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel, String role) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage(messageLabel, "Username and password cannot be empty.", false);
            return;
        }

        boolean success = authService.login(username, password, role);
        if (!success) {
            showMessage(messageLabel, "Invalid " + role + " username or password.", false);
            return;
        }

        showMessage(messageLabel, "Login successful.", true);

        if (role.equals("TA")) {
            try {
                ApplicantService applicantService = new ApplicantService();
                Applicant existingApplicant = applicantService.getApplicantByUsername(username);

                if (existingApplicant != null) {
                    ProfileDetailView profileView = new ProfileDetailView(existingApplicant, stage);
                    stage.getScene().setRoot(profileView.getView());
                    stage.setTitle("My Profile");
                } else {
                    HelloView helloView = new HelloView(stage);
                    helloView.setLoginUsername(username);
                    stage.getScene().setRoot(helloView.createContent());
                    stage.setTitle("Create Profile");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showMessage(messageLabel, "Error loading profile: " + ex.getMessage(), false);
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
            showMessage(messageLabel, "Username and password cannot be empty.", false);
            return;
        }

        boolean success = authService.register(username, password, role);
        if (success) {
            showMessage(messageLabel, "Registration successful. Please log in.", true);
        } else {
            showMessage(messageLabel, "Registration failed: Username already exists or invalid input.", false);
        }
    }
}

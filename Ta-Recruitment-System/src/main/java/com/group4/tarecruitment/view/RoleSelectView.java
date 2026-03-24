package com.group4.tarecruitment.view;

import com.group4.tarecruitment.service.AuthService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subTitle = new Label("Login or register with username, password, and role");
        subTitle.setStyle("-fx-font-size: 14px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(240);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(240);

        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // µÇÂ¼°´Å¥
        Button taBtn = new Button("Login as TA");
        Button moBtn = new Button("Login as MO");
        Button adminBtn = new Button("Login as Admin");

        // ×¢²á°´Å¥
        Button registerTaBtn = new Button("Register as TA");
        Button registerMoBtn = new Button("Register as MO");
        Button registerAdminBtn = new Button("Register as Admin");

        taBtn.setPrefWidth(240);
        moBtn.setPrefWidth(240);
        adminBtn.setPrefWidth(240);

        registerTaBtn.setPrefWidth(240);
        registerMoBtn.setPrefWidth(240);
        registerAdminBtn.setPrefWidth(240);

        taBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "TA"));
        moBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "MO"));
        adminBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, "Admin"));

        registerTaBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "TA"));
        registerMoBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "MO"));
        registerAdminBtn.setOnAction(e -> handleRegister(usernameField, passwordField, messageLabel, "Admin"));

        VBox root = new VBox(
                12,
                title,
                subTitle,
                usernameField,
                passwordField,
                taBtn,
                moBtn,
                adminBtn,
                registerTaBtn,
                registerMoBtn,
                registerAdminBtn,
                messageLabel
        );

        root.setPadding(new Insets(40));
        root.setStyle("-fx-alignment: center; -fx-background-color: #ecf0f1;");

        return root;
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField,
                             Label messageLabel, String role) {
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

        switch (role) {
            case "TA" -> {
                HelloView helloView = new HelloView(stage);
                stage.setScene(new Scene(helloView.createContent(), 800, 600));
            }
            case "MO" -> {
                TeacherView teacherView = new TeacherView(stage);
                stage.setScene(new Scene(teacherView.createContent(), 800, 600));
            }
            case "Admin" -> {
                AdminView adminView = new AdminView(stage);
                stage.setScene(new Scene(adminView.createContent(), 800, 600));
            }
        }
    }

    private void handleRegister(TextField usernameField, PasswordField passwordField,
                                Label messageLabel, String role) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authService.register(username, password, role);

        if (!success) {
            messageLabel.setText("Register failed. Username may already exist.");
            return;
        }

        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13px;");
        messageLabel.setText("Register success! You can now login as " + role + ".");

        usernameField.clear();
        passwordField.clear();
    }
}
package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloView {
    private final Stage stage;
    private final HelloController controller;
    private String loginUsername; // 【新增】保存当前登录用户名

    public HelloView(Stage stage) {
        this.stage = stage;
        this.controller = new HelloController();
    }

    // 【新增】供登录页面调用，传入登录账号
    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    public Parent createContent() {
        Label title = new Label("Create TA Application Profile");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdField = new TextField();

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label coursesLabel = new Label("Courses Available to Teach:");
        TextField coursesField = new TextField();

        Label skillsLabel = new Label("Skill Tags:");
        CheckBox cbJava = new CheckBox("Java");
        CheckBox cbEnglish = new CheckBox("English");
        CheckBox cbTeaching = new CheckBox("Teaching");
        CheckBox cbPython = new CheckBox("Python");
        CheckBox cbOffice = new CheckBox("Office");

        Label contactLabel = new Label("Contact Number:");
        TextField contactField = new TextField();

        Button submitBtn = new Button("Create Profile");
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: red");

        // ==========================
        // 关键：把登录用户名传给Controller
        // ==========================
        controller.setCurrentLoginUsername(loginUsername);

        submitBtn.setOnAction(e -> {
            controller.createProfile(
                    studentIdField, nameField, emailField,
                    coursesField, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                    contactField, resultLabel, submitBtn
            );
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(coursesLabel, 0, 3);
        grid.add(coursesField, 1, 3);
        grid.add(skillsLabel, 0, 4);
        grid.add(new VBox(5, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice), 1, 4);
        grid.add(contactLabel, 0, 5);
        grid.add(contactField, 1, 5);

        VBox root = new VBox(20, title, grid, submitBtn, resultLabel);
        root.setPadding(new Insets(30));
        return root;
    }
}
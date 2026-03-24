package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HelloView {

    private final Stage stage;

    public HelloView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        HelloController controller = new HelloController();

        Label titleLabel = new Label("TA Recruitment System - Applicant Profile Creation");
        titleLabel.setFont(new Font(18));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label studentIdLabel = new Label("Student ID*:");
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Enter your student ID");
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        Label emailLabel = new Label("Email*:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter a valid email");
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        Label coursesLabel = new Label("Available Courses:");
        TextField coursesField = new TextField();
        coursesField.setPromptText("Separate multiple courses with commas");
        grid.add(coursesLabel, 0, 3);
        grid.add(coursesField, 1, 3);

        Label skillLabel = new Label("Skill Tags*:");
        CheckBox cbJava = new CheckBox("Java");
        CheckBox cbEnglish = new CheckBox("English");
        CheckBox cbTeaching = new CheckBox("Teaching");
        CheckBox cbPython = new CheckBox("Python");
        CheckBox cbOffice = new CheckBox("Office");
        HBox skillBox = new HBox(10, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice);
        grid.add(skillLabel, 0, 4);
        grid.add(skillBox, 1, 4);

        Label contactLabel = new Label("Contact:");
        TextField contactField = new TextField();
        contactField.setPromptText("Phone number");
        grid.add(contactLabel, 0, 5);
        grid.add(contactField, 1, 5);

        Button submitBtn = new Button("Create Profile");
        Button backBtn = new Button("Back to Login");
        HBox btnBox = new HBox(20, submitBtn, backBtn);

        Label resultLabel = new Label("");

        submitBtn.setOnAction(e -> controller.createProfile(
                studentIdField, nameField, emailField, coursesField,
                cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                contactField, resultLabel, submitBtn
        ));

        backBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.setScene(new Scene(roleSelectView.createContent(), 800, 600));
        });

        VBox root = new VBox(10, titleLabel, grid, btnBox, resultLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ecf0f1;");

        return root;
    }
}
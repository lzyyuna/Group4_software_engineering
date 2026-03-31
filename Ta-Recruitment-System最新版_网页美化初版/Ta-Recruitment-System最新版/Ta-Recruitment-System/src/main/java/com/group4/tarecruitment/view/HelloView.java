package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloView {
    private final Stage stage;
    private final HelloController controller;
    private String loginUsername;

    public HelloView(Stage stage) {
        this.stage = stage;
        this.controller = new HelloController();
    }

    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    public Parent createContent() {
        Label title = new Label("Create TA Application Profile");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Complete your basic profile before browsing and applying for positions.");
        subtitle.getStyleClass().add("page-subtitle");

        Label studentIdLabel = createFormLabel("Student ID");
        TextField studentIdField = new TextField();

        Label nameLabel = createFormLabel("Name");
        TextField nameField = new TextField();

        Label emailLabel = createFormLabel("Email");
        TextField emailField = new TextField();

        Label coursesLabel = createFormLabel("Courses Available to Teach");
        TextField coursesField = new TextField();

        Label skillsLabel = createFormLabel("Skill Tags");
        CheckBox cbJava = new CheckBox("Java");
        CheckBox cbEnglish = new CheckBox("English");
        CheckBox cbTeaching = new CheckBox("Teaching");
        CheckBox cbPython = new CheckBox("Python");
        CheckBox cbOffice = new CheckBox("Office");
        HBox skillRow1 = new HBox(12, cbJava, cbEnglish, cbTeaching);
        HBox skillRow2 = new HBox(12, cbPython, cbOffice);
        VBox skillsBox = new VBox(8, skillRow1, skillRow2);

        Label contactLabel = createFormLabel("Contact Number");
        TextField contactField = new TextField();

        Button submitBtn = new Button("Create Profile");
        submitBtn.getStyleClass().add("primary-button");

        Button backBtn = new Button("Back to Login");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> stage.getScene().setRoot(new RoleSelectView(stage).createContent()));

        Label resultLabel = new Label();
        resultLabel.getStyleClass().add("muted-label");
        resultLabel.setWrapText(true);

        controller.setCurrentLoginUsername(loginUsername);

        submitBtn.setOnAction(e -> controller.createProfile(
                studentIdField, nameField, emailField,
                coursesField, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                contactField, resultLabel, submitBtn
        ));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(coursesLabel, 0, 3);
        grid.add(coursesField, 1, 3);
        grid.add(skillsLabel, 0, 4);
        grid.add(skillsBox, 1, 4);
        grid.add(contactLabel, 0, 5);
        grid.add(contactField, 1, 5);
        GridPane.setHgrow(studentIdField, Priority.ALWAYS);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(coursesField, Priority.ALWAYS);
        GridPane.setHgrow(contactField, Priority.ALWAYS);

        HBox actionRow = new HBox(12, submitBtn, backBtn);

        VBox formCard = new VBox(18, title, subtitle, grid, actionRow, resultLabel);
        formCard.getStyleClass().add("form-card");
        formCard.setMaxWidth(820);

        VBox root = new VBox(formCard);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(36));
        return root;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }
}

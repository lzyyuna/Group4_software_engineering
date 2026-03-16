package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class HelloView {

    public Parent createContent() {
        HelloController controller = new HelloController();

        Label titleLabel = new Label("TA Recruitment System");
        Label promptLabel = new Label("Please enter your name:");

        TextField nameField = new TextField();
        nameField.setPromptText("Your name");

        Button submitButton = new Button("Submit");

        Label resultLabel = new Label("Result will appear here");

        submitButton.setOnAction(e -> controller.handleSubmit(nameField, resultLabel));

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                titleLabel,
                promptLabel,
                nameField,
                submitButton,
                resultLabel
        );

        return root;
    }
}
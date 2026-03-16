package com.group4.tarecruitment.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

    public void handleSubmit(TextField nameField, Label resultLabel) {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            resultLabel.setText("Please enter your name first.");
        } else {
            resultLabel.setText("Welcome, " + name + "!");
        }
    }
}
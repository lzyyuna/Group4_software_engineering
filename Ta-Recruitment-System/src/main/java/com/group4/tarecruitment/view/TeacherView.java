package com.group4.tarecruitment.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TeacherView {

    private final Stage stage;

    public TeacherView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("Teacher Dashboard");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button publishBtn = new Button("Post TA Position");
        Button reviewBtn = new Button("View Applicants");
        Button approveBtn = new Button("Review Applications");
        Button backBtn = new Button("Back to Role Selection");

        publishBtn.setPrefWidth(200);
        reviewBtn.setPrefWidth(200);
        approveBtn.setPrefWidth(200);
        backBtn.setPrefWidth(200);

        backBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.setScene(new Scene(roleSelectView.createContent(), 800, 600));
        });

        VBox root = new VBox(15, title, publishBtn, reviewBtn, approveBtn, backBtn);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-alignment: center; -fx-background-color: #f5f6fa;");

        return root;
    }
}
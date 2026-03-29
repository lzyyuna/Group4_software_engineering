package com.group4.tarecruitment.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminView {

    private final Stage stage;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("Admin Dashboard");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button workloadBtn = new Button("Check TA Workload");
        Button manageBtn = new Button("Manage System");
        Button backBtn = new Button("Back to Role Selection");

        workloadBtn.setPrefWidth(220);
        manageBtn.setPrefWidth(220);
        backBtn.setPrefWidth(220);

        backBtn.setOnAction(e -> {
            RoleSelectView roleSelectView = new RoleSelectView(stage);
            stage.setScene(new Scene(roleSelectView.createContent(), 800, 600));
        });

        VBox root = new VBox(15, title, workloadBtn, manageBtn, backBtn);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-alignment: center; -fx-background-color: #f5f6fa;");

        return root;
    }
}
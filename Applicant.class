package com.group4.tarecruitment.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminView {

    private final Stage stage;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public Parent createContent() {
        Label title = new Label("Admin Dashboard");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Use the current admin entry points to inspect workload and manage the system.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        Button workloadBtn = new Button("Check TA Workload");
        Button manageBtn = new Button("Manage System");
        Button backBtn = new Button("Back to Role Selection");

        workloadBtn.getStyleClass().addAll("dashboard-action", "primary-button");
        manageBtn.getStyleClass().addAll("dashboard-action", "secondary-button");
        backBtn.getStyleClass().addAll("dashboard-action", "danger-button");

        backBtn.setOnAction(e -> stage.getScene().setRoot(new RoleSelectView(stage).createContent()));

        VBox card = new VBox(16, title, subtitle, workloadBtn, manageBtn, backBtn);
        card.getStyleClass().add("dashboard-card");
        card.setMaxWidth(420);

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(40));
        return root;
    }
}

package com.group4.tarecruitment.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacherView {

    private final Stage stage;
    private final String moUsername;

    public TeacherView(Stage stage) {
        this(stage, "");
    }

    public TeacherView(Stage stage, String moUsername) {
        this.stage = stage;
        this.moUsername = moUsername == null ? "" : moUsername;
    }

    public Parent createContent() {
        Label title = new Label("MO Dashboard");
        title.getStyleClass().add("page-title");

        String subtitleText = moUsername.isBlank()
                ? "Use the current entry points to post positions, manage your postings, and review applications."
                : "Welcome, " + moUsername + ". Use the current entry points to post positions, manage your postings, and review applications.";
        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        Button publishBtn = new Button("Post TA Position");
        Button viewJobsBtn = new Button("View My Posted Positions");
        Button reviewBtn = new Button("Review Applications");
        Button backBtn = new Button("Back to Role Selection");

        publishBtn.getStyleClass().addAll("dashboard-action", "primary-button");
        viewJobsBtn.getStyleClass().addAll("dashboard-action", "secondary-button");
        reviewBtn.getStyleClass().addAll("dashboard-action", "button");
        backBtn.getStyleClass().addAll("dashboard-action", "danger-button");

        publishBtn.setOnAction(e -> stage.getScene().setRoot(
                new MOPostJobView(stage, moUsername, moUsername + "@bupt.edu").createContent()
        ));
        viewJobsBtn.setOnAction(e -> stage.getScene().setRoot(
                new MOViewJobsView(stage, moUsername).createContent()
        ));
        reviewBtn.setOnAction(e -> stage.getScene().setRoot(
                new MOViewApplicationsView(stage, moUsername).createContent()
        ));
        backBtn.setOnAction(e -> stage.getScene().setRoot(new RoleSelectView(stage).createContent()));

        VBox card = new VBox(16, title, subtitle, publishBtn, viewJobsBtn, reviewBtn, backBtn);
        card.getStyleClass().add("dashboard-card");
        card.setMaxWidth(420);

        VBox root = new VBox(card);
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(40));
        return root;
    }
}

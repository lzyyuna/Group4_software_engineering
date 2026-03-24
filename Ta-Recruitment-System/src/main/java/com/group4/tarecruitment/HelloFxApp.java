package com.group4.tarecruitment;

import com.group4.tarecruitment.view.RoleSelectView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloFxApp extends Application {

    @Override
    public void start(Stage stage) {
        RoleSelectView view = new RoleSelectView(stage);
        Scene scene = new Scene(view.createContent(), 800, 600);

        stage.setTitle("TA Recruitment System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
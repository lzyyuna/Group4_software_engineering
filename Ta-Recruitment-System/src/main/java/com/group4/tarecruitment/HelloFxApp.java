package com.group4.tarecruitment;

import com.group4.tarecruitment.util.ThemeManager;
import com.group4.tarecruitment.view.RoleSelectView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class HelloFxApp extends Application {

    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);

        RoleSelectView view = new RoleSelectView(stage);
        Scene scene = ThemeManager.createScene(view.createContent(), 1000, 700);

        stage.setTitle("TA Recruitment System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        launch();
    }
}

package com.group4.tarecruitment;

import com.group4.tarecruitment.view.HelloView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloFxApp extends Application {

    @Override
    public void start(Stage stage) {
        HelloView view = new HelloView();
        // 窗口放大为800×600，适配多输入项表单
        Scene scene = new Scene(view.createContent(), 800, 600);
        stage.setTitle("TA Recruitment System - TA-001 个人申请档案");
        stage.setScene(scene);
        stage.centerOnScreen(); // 窗口居中
        stage.setResizable(true); // 允许手动放大
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
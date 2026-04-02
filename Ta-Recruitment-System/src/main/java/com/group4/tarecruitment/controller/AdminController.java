package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.view.AdminView;
import com.group4.tarecruitment.view.RoleSelectView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminController {

    private final Stage stage;

    public AdminController(Stage stage) {
        this.stage = stage;
    }

    public void showAdminPanel() {
        AdminView adminView = new AdminView(stage);
        Parent root = adminView.createContent();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

        VBox vBox = (VBox) root;

        Button checkWorkloadBtn = (Button) vBox.getChildren().get(1);
        Button manageSystemBtn = (Button) vBox.getChildren().get(2);

        // 绑定点击事件
        checkWorkloadBtn.setOnAction(e -> {
            System.out.println("✅ Check TA Workload 按钮点击成功");
        });

        manageSystemBtn.setOnAction(e -> {
            System.out.println("✅ Manage System 按钮点击成功");
        });
    }
}

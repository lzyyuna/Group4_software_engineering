package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.view.AdminView;
import com.group4.tarecruitment.view.RoleSelectView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminController {

    private final Stage stage;

    public AdminController(Stage stage) {
        this.stage = stage;
    }

    public void showAdminPanel() {
        AdminView adminView = new AdminView(stage);
        Scene scene = new Scene(adminView.createContent(), 800, 600);
        stage.setScene(scene);
        stage.show();

        // 按钮点击事件
        // 返回角色选择
        adminView.backBtn.setOnAction(e -> {
            RoleSelectView roleView = new RoleSelectView(stage);
            Scene roleScene = new Scene(roleView.createContent(), 800, 600);
            stage.setScene(roleScene);
        });

        // 查看 TA 工作量
        adminView.checkWorkloadBtn.setOnAction(e -> {
            System.out.println("✅ Check TA Workload 按钮点击成功");
        });

        // 系统管理
        adminView.manageSystemBtn.setOnAction(e -> {
            System.out.println("✅ Manage System 按钮点击成功");
        });
    }
}

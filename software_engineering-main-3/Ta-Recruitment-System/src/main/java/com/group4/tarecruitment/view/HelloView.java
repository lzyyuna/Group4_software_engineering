package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class HelloView {

    public Parent createContent() {
        HelloController controller = new HelloController();

        // 标题
        Label titleLabel = new Label("TA Recruitment System - 个人申请档案创建（TA-001）");
        titleLabel.setFont(new Font(18));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 表单布局
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // 1. 学号 *
        Label studentIdLabel = new Label("学号*：");
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("请输入北邮学号");
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);

        // 2. 姓名
        Label nameLabel = new Label("姓名：");
        TextField nameField = new TextField();
        nameField.setPromptText("请输入真实姓名");
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        // 3. 邮箱 *
        Label emailLabel = new Label("邮箱*：");
        TextField emailField = new TextField();
        emailField.setPromptText("请输入有效邮箱（含@）");
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        // 4. 可授课课程
        Label coursesLabel = new Label("可授课课程：");
        TextField coursesField = new TextField();
        coursesField.setPromptText("多个用逗号分隔");
        grid.add(coursesLabel, 0, 3);
        grid.add(coursesField, 1, 3);

        // 5. 技能标签 *
        Label skillLabel = new Label("技能标签*：");
        CheckBox cbJava = new CheckBox("Java");
        CheckBox cbEnglish = new CheckBox("English");
        CheckBox cbTeaching = new CheckBox("Teaching");
        CheckBox cbPython = new CheckBox("Python");
        CheckBox cbOffice = new CheckBox("Office");
        HBox skillBox = new HBox(10, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice);
        grid.add(skillLabel, 0, 4);
        grid.add(skillBox, 1, 4);

        // 6. 联系电话
        Label contactLabel = new Label("联系电话：");
        TextField contactField = new TextField();
        contactField.setPromptText("手机号/座机均可");
        grid.add(contactLabel, 0, 5);
        grid.add(contactField, 1, 5);

        // 7. 密码 *
        Label pwdLabel = new Label("密码*：");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("不少于6位");
        grid.add(pwdLabel, 0, 6);
        grid.add(pwdField, 1, 6);

        // 按钮
        Button submitBtn = new Button("创建档案");
        submitBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 20;");
        Button editBtn = new Button("编辑档案");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 20;");
        HBox btnBox = new HBox(20, submitBtn, editBtn);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // 提示信息
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10 0;");

        // ==========================
        // 绑定事件（已自动传 submitBtn）
        // ==========================
        submitBtn.setOnAction(e -> controller.createProfile(
                studentIdField, nameField, emailField, coursesField,
                cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                contactField, pwdField, resultLabel, submitBtn
        ));

        editBtn.setOnAction(e -> controller.editProfile(
                studentIdField, nameField, emailField, coursesField,
                cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                contactField, pwdField, resultLabel
        ));

        // 整体布局
        VBox root = new VBox(10, titleLabel, grid, btnBox, resultLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ecf0f1;");

        return root;
    }
}
package com.group4.tarecruitment.view;

import com.group4.tarecruitment.controller.HelloController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloView {
    private final Stage stage;
    private final HelloController controller;
    private String loginUsername;

    public HelloView(Stage stage) {
        this.stage = stage;
        this.controller = new HelloController();
    }

    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    public Parent createContent() {
        // 1. 页面整体背景（和岗位详情页一致）
        VBox pageRoot = new VBox();
        pageRoot.setAlignment(Pos.TOP_LEFT);
        pageRoot.setStyle("-fx-background-color: #f5f6fa;");
        pageRoot.setPadding(new Insets(30));

        // 2. 标题
        Label title = new Label("Create TA Application Profile");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 3. 副标题
        Label subtitle = new Label("Complete your basic profile before browsing and applying for positions.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 15 0;");

        // 4. 白色信息卡片（和岗位详情页同款）
        VBox infoCard = new VBox(12);
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 2);"
        );

        // 5. 统一样式
        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;";
        String fieldStyle = "-fx-padding: 8 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-color: #e2e8f0; -fx-border-radius: 5;";
        String errorFieldStyle = "-fx-padding: 8 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-radius: 5; -fx-background-color: #fdf2f2;";

        // 6. 表单组件
        Label studentIdLabel = new Label("Student ID:");
        studentIdLabel.setStyle(labelStyle);
        TextField studentIdField = new TextField();
        studentIdField.setStyle(fieldStyle);
        studentIdField.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle(labelStyle);
        TextField nameField = new TextField();
        nameField.setStyle(fieldStyle);
        nameField.setMaxWidth(Double.MAX_VALUE);

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle(labelStyle);
        TextField emailField = new TextField();
        emailField.setStyle(fieldStyle);
        emailField.setMaxWidth(Double.MAX_VALUE);

        Label coursesLabel = new Label("Courses Available to Teach:");
        coursesLabel.setStyle(labelStyle);
        TextField coursesField = new TextField();
        coursesField.setStyle(fieldStyle);
        coursesField.setMaxWidth(Double.MAX_VALUE);

        Label skillsLabel = new Label("Skill Tags:");
        skillsLabel.setStyle(labelStyle);
        CheckBox cbJava = new CheckBox("Java");
        CheckBox cbEnglish = new CheckBox("English");
        CheckBox cbTeaching = new CheckBox("Teaching");
        CheckBox cbPython = new CheckBox("Python");
        CheckBox cbOffice = new CheckBox("Office");
        String checkStyle = "-fx-font-size: 14px; -fx-text-fill: #2c3e50;";
        cbJava.setStyle(checkStyle);
        cbEnglish.setStyle(checkStyle);
        cbTeaching.setStyle(checkStyle);
        cbPython.setStyle(checkStyle);
        cbOffice.setStyle(checkStyle);

        VBox skillsBox = new VBox(5, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice);

        Label contactLabel = new Label("Contact Number:");
        contactLabel.setStyle(labelStyle);
        TextField contactField = new TextField();
        contactField.setStyle(fieldStyle);
        contactField.setMaxWidth(Double.MAX_VALUE);

        // 7. 表单布局
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(coursesLabel, 0, 3);
        grid.add(coursesField, 1, 3);
        grid.add(skillsLabel, 0, 4);
        grid.add(skillsBox, 1, 4);
        grid.add(contactLabel, 0, 5);
        grid.add(contactField, 1, 5);

        // 输入框填满宽度
        GridPane.setFillWidth(studentIdField, true);
        GridPane.setFillWidth(nameField, true);
        GridPane.setFillWidth(emailField, true);
        GridPane.setFillWidth(coursesField, true);
        GridPane.setFillWidth(contactField, true);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), col2);

        // 8. 按钮（美化）
        Button submitBtn = new Button("Create Profile");
        submitBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10 24px; " + // 稍微加大按钮 padding，更美观
                        "-fx-background-radius: 5; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        // 提示标签
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5 0 0 0;");

        // 9. 业务逻辑 + 表单校验
        controller.setCurrentLoginUsername(loginUsername);

        submitBtn.setOnAction(e -> {
            // ===================== 表单校验开始 =====================
            boolean isFormValid = true;
            resultLabel.setText(""); // 清空之前提示

            // 🔴 1. 校验必填项是否为空
            if (studentIdField.getText().trim().isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ Student ID is required.\n");
                studentIdField.setStyle(errorFieldStyle);
                isFormValid = false;
            } else {
                studentIdField.setStyle(fieldStyle);
            }

            if (nameField.getText().trim().isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ Name is required.\n");
                nameField.setStyle(errorFieldStyle);
                isFormValid = false;
            } else {
                nameField.setStyle(fieldStyle);
            }

            if (emailField.getText().trim().isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ Email is required.\n");
                emailField.setStyle(errorFieldStyle);
                isFormValid = false;
            } else {
                emailField.setStyle(fieldStyle);
            }

            if (contactField.getText().trim().isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ Contact Number is required.\n");
                contactField.setStyle(errorFieldStyle);
                isFormValid = false;
            } else {
                contactField.setStyle(fieldStyle);
            }

            // 🔴 2. 校验 Student ID 格式：必须为 10 位数字
            String studentId = studentIdField.getText().trim();
            if (isFormValid && !studentId.matches("\\d{10}")) {
                resultLabel.setText(resultLabel.getText() + "❌ Student ID must be 10 digits.\n");
                studentIdField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            // 🔴 3. 校验 Contact Number 格式：支持 11位手机号 或 固定电话（如 010-12345678）
            String contact = contactField.getText().trim();
            if (isFormValid) {
                // 正则表达式说明：
                // ^1[3-9]\\d{9}$ : 中国大陆手机号 (11位)
                // ^\\d{3,4}-\\d{7,8}$ : 固定电话 (区号-号码)
                if (!contact.matches("^1[3-9]\\d{9}$") && !contact.matches("^\\d{3,4}-\\d{7,8}$")) {
                    resultLabel.setText(resultLabel.getText() + "❌ Invalid contact number format.\n");
                    contactField.setStyle(errorFieldStyle);
                    isFormValid = false;
                }
            }

            // ===================== 校验通过，才提交 =====================
            if (isFormValid) {
                resultLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-padding: 5 0 0 0;");
                resultLabel.setText("✅ Verifying...");

                controller.createProfile(
                        studentIdField, nameField, emailField,
                        coursesField, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice,
                        contactField, resultLabel, submitBtn
                );
            } else {
                // 有错误，提示文字变红
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-padding: 5 0 0 0;");
            }
        });

        // 10. 组装布局
        infoCard.getChildren().add(grid);
        VBox btnBox = new VBox(10, submitBtn, resultLabel);
        btnBox.setPadding(new Insets(15, 0, 0, 0));
        btnBox.setAlignment(Pos.CENTER_LEFT);

        pageRoot.getChildren().addAll(title, subtitle, infoCard, btnBox);

        return pageRoot;
    }
}
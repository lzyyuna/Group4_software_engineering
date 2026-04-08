package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ProfileDetailView {
    private final Applicant applicant;
    private final Stage stage;

    public ProfileDetailView(Applicant applicant, Stage stage) {
        this.applicant = applicant;
        this.stage = stage;
    }

    public Parent getView() {
        // 标题美化
        Label title = new Label("个人详情");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 信息卡片容器（美化背景+内边距）
        VBox infoCard = new VBox(15);
        infoCard.setPadding(new Insets(30));
        infoCard.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

        // 信息网格（两列对齐，美观排版）
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30);
        infoGrid.setVgap(18);
        infoGrid.setAlignment(Pos.CENTER_LEFT);

        // 标签样式统一
        String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;";
        String valueStyle = "-fx-font-size: 16px; -fx-text-fill: #2c3e50;";

        // 逐行添加信息（完全对应你提供的截图）
        addInfoRow(infoGrid, 0, "TA ID：", applicant.getTaId(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 1, "学号：", applicant.getStudentId(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 2, "姓名：", applicant.getName(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 3, "邮箱：", applicant.getEmail(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 4, "可授课程：", applicant.getCourses(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 5, "技能标签：", applicant.getSkillTags(), labelStyle, valueStyle);
        addInfoRow(infoGrid, 6, "联系电话：", applicant.getContact(), labelStyle, valueStyle);

        infoCard.getChildren().add(infoGrid);

        // 返回TA首页按钮（仅保留这一个按钮）
        Button backToHomeBtn = new Button("返回TA首页");
        backToHomeBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );
        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        // 按钮居中布局
        HBox buttonBox = new HBox(backToHomeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25, 0, 0, 0));

        // 根布局（整体居中+背景色）
        VBox root = new VBox(25, title, infoCard, buttonBox);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    // 工具方法：统一添加信息行，避免重复代码
    private void addInfoRow(GridPane grid, int row, String labelText, String valueText, String labelStyle, String valueStyle) {
        Label label = new Label(labelText);
        label.setStyle(labelStyle);

        Label value = new Label(valueText);
        value.setStyle(valueStyle);

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }
}
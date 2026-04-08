package com.group4.tarecruitment.view;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class ProfileDetailView {
    private final Applicant applicant;
    private final Stage stage;
    private final ApplicantService applicantService = new ApplicantService();

    public ProfileDetailView(Applicant applicant, Stage stage) {
        this.applicant = applicant;
        this.stage = stage;
    }

    public Parent getView() {
        return createDisplayView();
    }

    // ===================== 展示模式 =====================
    private Parent createDisplayView() {
        Label title = new Label("个人详情");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox infoCard = new VBox(15);
        infoCard.setPadding(new Insets(30));
        infoCard.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30);
        infoGrid.setVgap(18);
        infoGrid.setAlignment(Pos.CENTER_LEFT);

        String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;";
        String valueStyle = "-fx-font-size: 16px; -fx-text-fill: #2c3e50;";

        addInfoRow(infoGrid, 0, "TA ID：", safe(applicant.getTaId()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 1, "学号：", safe(applicant.getStudentId()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 2, "姓名：", safe(applicant.getName()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 3, "邮箱：", safe(applicant.getEmail()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 4, "可授课程：", safe(applicant.getCourses()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 5, "技能标签：", safe(applicant.getSkillTags()), labelStyle, valueStyle);
        addInfoRow(infoGrid, 6, "联系电话：", safe(applicant.getContact()), labelStyle, valueStyle);

        infoCard.getChildren().add(infoGrid);

        Button editBtn = new Button("编辑");
        editBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );
        editBtn.setOnAction(e -> {
            stage.getScene().setRoot(createEditView());
            stage.setTitle("编辑个人详情");
        });

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

        HBox buttonBox = new HBox(20, editBtn, backToHomeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25, 0, 0, 0));

        VBox root = new VBox(25, title, infoCard, buttonBox);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        return root;
    }

    // ===================== 编辑模式 =====================
    private Parent createEditView() {
        Label title = new Label("编辑个人详情");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(30));
        formCard.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(16);
        formGrid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(col1, col2);

        String labelStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #34495e;";
        String fieldStyle = "-fx-padding: 8 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-color: #dcdde1; -fx-border-radius: 5;";
        String errorFieldStyle = "-fx-padding: 8 12; -fx-font-size: 14px; -fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-radius: 5; -fx-background-color: #fdf2f2;";

        Label taIdLabel = new Label("TA ID：");
        taIdLabel.setStyle(labelStyle);
        TextField taIdField = new TextField(safe(applicant.getTaId()));
        taIdField.setEditable(false);
        taIdField.setDisable(true);
        taIdField.setStyle(fieldStyle + "-fx-opacity: 0.8;");

        Label studentIdLabel = new Label("学号：");
        studentIdLabel.setStyle(labelStyle);
        TextField studentIdField = new TextField(safe(applicant.getStudentId()));
        studentIdField.setStyle(fieldStyle);

        Label nameLabel = new Label("姓名：");
        nameLabel.setStyle(labelStyle);
        TextField nameField = new TextField(safe(applicant.getName()));
        nameField.setStyle(fieldStyle);

        Label emailLabel = new Label("邮箱：");
        emailLabel.setStyle(labelStyle);
        TextField emailField = new TextField(safe(applicant.getEmail()));
        emailField.setStyle(fieldStyle);

        Label coursesLabel = new Label("可授课程：");
        coursesLabel.setStyle(labelStyle);
        TextField coursesField = new TextField(safe(applicant.getCourses()));
        coursesField.setStyle(fieldStyle);

        Label skillsLabel = new Label("技能标签：");
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

        String oldSkills = safe(applicant.getSkillTags()).toLowerCase();
        cbJava.setSelected(oldSkills.contains("java"));
        cbEnglish.setSelected(oldSkills.contains("english"));
        cbTeaching.setSelected(oldSkills.contains("teaching"));
        cbPython.setSelected(oldSkills.contains("python"));
        cbOffice.setSelected(oldSkills.contains("office"));

        VBox skillsBox = new VBox(6, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice);

        Label contactLabel = new Label("联系电话：");
        contactLabel.setStyle(labelStyle);
        TextField contactField = new TextField(safe(applicant.getContact()));
        contactField.setStyle(fieldStyle);

        formGrid.add(taIdLabel, 0, 0);
        formGrid.add(taIdField, 1, 0);

        formGrid.add(studentIdLabel, 0, 1);
        formGrid.add(studentIdField, 1, 1);

        formGrid.add(nameLabel, 0, 2);
        formGrid.add(nameField, 1, 2);

        formGrid.add(emailLabel, 0, 3);
        formGrid.add(emailField, 1, 3);

        formGrid.add(coursesLabel, 0, 4);
        formGrid.add(coursesField, 1, 4);

        formGrid.add(skillsLabel, 0, 5);
        formGrid.add(skillsBox, 1, 5);

        formGrid.add(contactLabel, 0, 6);
        formGrid.add(contactField, 1, 6);

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8 0 0 0;");

        Button saveBtn = new Button("保存");
        saveBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );

        Button cancelBtn = new Button("取消");
        cancelBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #95a5a6; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );

        Button backToHomeBtn = new Button("返回TA首页");
        backToHomeBtn.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 25px; " +
                        "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );

        saveBtn.setOnAction(e -> {
            boolean isFormValid = true;
            resultLabel.setText("");

            studentIdField.setStyle(fieldStyle);
            nameField.setStyle(fieldStyle);
            emailField.setStyle(fieldStyle);
            contactField.setStyle(fieldStyle);

            String studentId = studentIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String courses = coursesField.getText().trim();
            String contact = contactField.getText().trim();

            if (studentId.isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ 学号不能为空。\n");
                studentIdField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (name.isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ 姓名不能为空。\n");
                nameField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (email.isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ 邮箱不能为空。\n");
                emailField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (contact.isEmpty()) {
                resultLabel.setText(resultLabel.getText() + "❌ 联系电话不能为空。\n");
                contactField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (!studentId.isEmpty() && !studentId.matches("\\d{10}")) {
                resultLabel.setText(resultLabel.getText() + "❌ 学号必须是10位数字。\n");
                studentIdField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (!email.isEmpty() && !email.contains("@")) {
                resultLabel.setText(resultLabel.getText() + "❌ 邮箱格式不正确，必须包含 @ 。\n");
                emailField.setStyle(errorFieldStyle);
                isFormValid = false;
            }

            if (!contact.isEmpty()) {
                if (!contact.matches("^1[3-9]\\d{9}$") && !contact.matches("^\\d{3,4}-\\d{7,8}$")) {
                    resultLabel.setText(resultLabel.getText() + "❌ 联系电话格式不正确。\n");
                    contactField.setStyle(errorFieldStyle);
                    isFormValid = false;
                }
            }

            StringBuilder skillTags = new StringBuilder();
            if (cbJava.isSelected()) skillTags.append("Java,");
            if (cbEnglish.isSelected()) skillTags.append("English,");
            if (cbTeaching.isSelected()) skillTags.append("Teaching,");
            if (cbPython.isSelected()) skillTags.append("Python,");
            if (cbOffice.isSelected()) skillTags.append("Office,");

            if (skillTags.length() == 0) {
                resultLabel.setText(resultLabel.getText() + "❌ 请至少选择一个技能标签。\n");
                isFormValid = false;
            }

            if (!isFormValid) {
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-padding: 8 0 0 0;");
                return;
            }

            String skillStr = skillTags.substring(0, skillTags.length() - 1);

            try {
                applicant.setStudentId(studentId);
                applicant.setName(name);
                applicant.setEmail(email);
                applicant.setCourses(courses);
                applicant.setSkillTags(skillStr);
                applicant.setContact(contact);

                applicantService.updateApplicant(applicant);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("个人信息更新成功！");
                alert.showAndWait();

                stage.getScene().setRoot(createDisplayView());
                stage.setTitle("个人详情 & 简历上传");
            } catch (Exception ex) {
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-padding: 8 0 0 0;");
                resultLabel.setText("❌ 保存失败：" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelBtn.setOnAction(e -> {
            stage.getScene().setRoot(createDisplayView());
            stage.setTitle("个人详情 & 简历上传");
        });

        backToHomeBtn.setOnAction(e -> {
            TAHomeView taHomeView = new TAHomeView(stage, applicant);
            stage.getScene().setRoot(taHomeView.createContent());
            stage.setTitle("TA Dashboard");
        });

        HBox buttonBox = new HBox(20, saveBtn, cancelBtn, backToHomeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        formCard.getChildren().addAll(formGrid, resultLabel);

        VBox root = new VBox(10, title, formCard, buttonBox);
        root.setPadding(new Insets(10, 40, 20, 40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f5f6fa; -fx-background-color: #f5f6fa;");

        return scrollPane;
    }

    // ===================== 工具方法 =====================
    private void addInfoRow(GridPane grid, int row, String labelText, String valueText, String labelStyle, String valueStyle) {
        Label label = new Label(labelText);
        label.setStyle(labelStyle);

        Label value = new Label(valueText == null || valueText.isBlank() ? "未填写" : valueText);
        value.setStyle(valueStyle);

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
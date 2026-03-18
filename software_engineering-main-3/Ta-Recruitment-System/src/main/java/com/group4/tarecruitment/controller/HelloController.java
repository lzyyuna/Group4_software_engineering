package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.util.MD5Util;
import com.group4.tarecruitment.view.ProfileDetailView;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class HelloController {
    private final ApplicantService applicantService = new ApplicantService();

    public void createProfile(TextField studentIdField, TextField nameField, TextField emailField,
                              TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                              CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                              TextField contactField, PasswordField pwdField, Label resultLabel, Button submitBtn) {

        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String courses = coursesField.getText().trim();
        String contact = contactField.getText().trim();
        String password = pwdField.getText().trim();

        StringBuilder skillTags = new StringBuilder();
        if (cbJava.isSelected()) skillTags.append("Java,");
        if (cbEnglish.isSelected()) skillTags.append("English,");
        if (cbTeaching.isSelected()) skillTags.append("Teaching,");
        if (cbPython.isSelected()) skillTags.append("Python,");
        if (cbOffice.isSelected()) skillTags.append("Office,");
        String skillStr = skillTags.length() > 0 ? skillTags.substring(0, skillTags.length() - 1) : "";

        if (studentId.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：学号不能为空！");
            return;
        }
        if (email.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：邮箱不能为空！");
            return;
        }
        if (!email.contains("@")) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：邮箱必须包含 @！");
            return;
        }
        if (skillStr.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：至少选择一个技能标签！");
            return;
        }
        if (password.length() < 6) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：密码至少6位！");
            return;
        }

        try {
            List<Applicant> allApplicants = applicantService.getAllApplicants();
            for (Applicant a : allApplicants) {
                if (a.getStudentId().equals(studentId)) {
                    resultLabel.setStyle("-fx-text-fill: red;");
                    resultLabel.setText("错误：该学号已注册！");
                    return;
                }
            }

            String taId = "TA-" + System.currentTimeMillis();
            String encryptPwd = MD5Util.encrypt(password);

            Applicant applicant = new Applicant(taId, studentId, name, email, courses, skillStr, contact, encryptPwd);
            applicantService.addApplicant(applicant);

            // =======================
            // ✅ 创建成功 → 跳转到详情页（你要的功能）
            // =======================
            ProfileDetailView detailView = new ProfileDetailView(applicant);
            Stage stage = (Stage) submitBtn.getScene().getWindow();
            stage.getScene().setRoot(detailView.getView());
            stage.setTitle("档案详情 & 简历上传");

        } catch (Exception e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("创建失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void editProfile(TextField studentIdField, TextField nameField, TextField emailField,
                            TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                            CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                            TextField contactField, PasswordField pwdField, Label resultLabel) {

        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("错误：请输入学号！");
            return;
        }

        try {
            Applicant applicant = applicantService.getApplicantByStudentId(studentId);
            if (applicant == null) {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText("错误：未找到该档案！");
                return;
            }

            String name = nameField.getText().trim().isEmpty() ? applicant.getName() : nameField.getText().trim();
            String email = emailField.getText().trim().isEmpty() ? applicant.getEmail() : emailField.getText().trim();
            String courses = coursesField.getText().trim().isEmpty() ? applicant.getCourses() : coursesField.getText().trim();
            String contact = contactField.getText().trim().isEmpty() ? applicant.getContact() : contactField.getText().trim();
            String password = pwdField.getText().trim().isEmpty() ? applicant.getPassword() : MD5Util.encrypt(pwdField.getText().trim());

            StringBuilder skillTags = new StringBuilder();
            if (cbJava.isSelected() || cbEnglish.isSelected() || cbTeaching.isSelected() || cbPython.isSelected() || cbOffice.isSelected()) {
                if (cbJava.isSelected()) skillTags.append("Java,");
                if (cbEnglish.isSelected()) skillTags.append("English,");
                if (cbTeaching.isSelected()) skillTags.append("Teaching,");
                if (cbPython.isSelected()) skillTags.append("Python,");
                if (cbOffice.isSelected()) skillTags.append("Office,");
            } else {
                skillTags.append(applicant.getSkillTags());
            }
            String skillStr = skillTags.length() > 0 ? skillTags.substring(0, skillTags.length() - 1) : "";

            if (!email.equals(applicant.getEmail()) && !email.contains("@")) {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText("错误：新邮箱格式错误！");
                return;
            }

            applicant.setName(name);
            applicant.setEmail(email);
            applicant.setCourses(courses);
            applicant.setSkillTags(skillStr);
            applicant.setContact(contact);
            applicant.setPassword(password);

            applicantService.updateApplicant(applicant);

            resultLabel.setStyle("-fx-text-fill: green;");
            resultLabel.setText("档案修改成功！");

            clearFields(studentIdField, nameField, emailField, coursesField, cbJava, cbEnglish, cbTeaching, cbPython, cbOffice, contactField, pwdField);

        } catch (Exception e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("编辑失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields(TextField studentIdField, TextField nameField, TextField emailField,
                             TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                             CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                             TextField contactField, PasswordField pwdField) {
        studentIdField.clear();
        nameField.clear();
        emailField.clear();
        coursesField.clear();
        cbJava.setSelected(false);
        cbEnglish.setSelected(false);
        cbTeaching.setSelected(false);
        cbPython.setSelected(false);
        cbOffice.setSelected(false);
        contactField.clear();
        pwdField.clear();
    }
}
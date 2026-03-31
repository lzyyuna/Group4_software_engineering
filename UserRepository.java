package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import com.group4.tarecruitment.view.ProfileDetailView;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class HelloController {
    private final ApplicantService applicantService = new ApplicantService();
    private String currentLoginUsername;

    public void setCurrentLoginUsername(String username) {
        this.currentLoginUsername = username;
    }

    public void createProfile(TextField studentIdField, TextField nameField, TextField emailField,
                              TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                              CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                              TextField contactField, Label resultLabel, Button submitBtn) {

        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String courses = coursesField.getText().trim();
        String contact = contactField.getText().trim();

        StringBuilder skillTags = new StringBuilder();
        if (cbJava.isSelected()) skillTags.append("Java,");
        if (cbEnglish.isSelected()) skillTags.append("English,");
        if (cbTeaching.isSelected()) skillTags.append("Teaching,");
        if (cbPython.isSelected()) skillTags.append("Python,");
        if (cbOffice.isSelected()) skillTags.append("Office,");
        String skillStr = skillTags.length() > 0 ? skillTags.substring(0, skillTags.length() - 1) : "";

        if (studentId.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Error: Student ID cannot be empty!");
            return;
        }
        if (email.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Error: Email cannot be empty!");
            return;
        }
        if (!email.contains("@")) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Error: Email must contain @ !");
            return;
        }
        if (skillStr.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Error: Please select at least one skill tag!");
            return;
        }

        try {
            // 修复：确保方法名和 ApplicantService 完全一致
            List<Applicant> allApplicants = applicantService.getAllApplicants();
            for (Applicant a : allApplicants) {
                // 修复：确保 Applicant 类有 getStudentId() 方法
                if (a.getStudentId().equals(studentId)) {
                    resultLabel.setStyle("-fx-text-fill: red;");
                    resultLabel.setText("Error: This student ID is already registered!");
                    return;
                }
            }

            String taId = "TA-" + System.currentTimeMillis();

            Applicant applicant = new Applicant(taId, studentId, name, email, courses, skillStr, contact);
            applicant.setUsername(currentLoginUsername);

            applicantService.addApplicant(applicant);

            Stage stage = (Stage) submitBtn.getScene().getWindow();
            ProfileDetailView detailView = new ProfileDetailView(applicant, stage);
            stage.getScene().setRoot(detailView.getView());
            stage.setTitle("Profile Details & Resume Upload");

        } catch (Exception e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void editProfile(TextField studentIdField, TextField nameField, TextField emailField,
                            TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                            CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                            TextField contactField, Label resultLabel) {

        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Error: Please enter student ID!");
            return;
        }

        try {
            Applicant applicant = applicantService.getApplicantByStudentId(studentId);
            if (applicant == null) {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText("Error: Applicant not found!");
                return;
            }

            String name = nameField.getText().trim().isEmpty() ? applicant.getName() : nameField.getText().trim();
            String email = emailField.getText().trim().isEmpty() ? applicant.getEmail() : emailField.getText().trim();
            String courses = coursesField.getText().trim().isEmpty() ? applicant.getCourses() : coursesField.getText().trim();
            String contact = contactField.getText().trim().isEmpty() ? applicant.getContact() : contactField.getText().trim();

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
                resultLabel.setText("Error: Invalid new email format!");
                return;
            }

            applicant.setName(name);
            applicant.setEmail(email);
            applicant.setCourses(courses);
            applicant.setSkillTags(skillStr);
            applicant.setContact(contact);

            applicantService.updateApplicant(applicant);

            resultLabel.setStyle("-fx-text-fill: green;");
            resultLabel.setText("Profile updated successfully!");

            clearFields(studentIdField, nameField, emailField, coursesField,
                    cbJava, cbEnglish, cbTeaching, cbPython, cbOffice, contactField);

        } catch (Exception e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields(TextField studentIdField, TextField nameField, TextField emailField,
                             TextField coursesField, CheckBox cbJava, CheckBox cbEnglish,
                             CheckBox cbTeaching, CheckBox cbPython, CheckBox cbOffice,
                             TextField contactField) {
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
    }
}
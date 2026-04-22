package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.service.ApplicantService;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.Desktop;

public class ProfileController {
    private final ApplicantService service = new ApplicantService();
    private final String RESUME_DIR = "data/resumes/";

    public void uploadResume(Applicant applicant, Label status, Label pathLabel, ProgressBar pb) {
        new File(RESUME_DIR).mkdirs();

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("文档文件", "*.txt", "*.pdf", "*.doc", "*.docx")
        );

        File file = chooser.showOpenDialog(null);
        if (file == null) return;

        // 10MB 限制
        if (file.length() > 10 * 1024 * 1024) {
            status.setText("❌ 超过大小限制（最大10MB）");
            status.setStyle("-fx-text-fill:red;");
            return;
        }

        pb.setVisible(true);
        status.setText("⏳ 上传中...");
        pb.setProgress(0.3);

        new Thread(() -> {
            try {
                String newName = applicant.getStudentId() + "_" + file.getName();
                File target = new File(RESUME_DIR + newName);
                Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

// 保存相对路径，而不是绝对路径
                applicant.setResumePath(RESUME_DIR + newName);
                service.updateApplicant(applicant);

                Platform.runLater(() -> {
                    pb.setProgress(1.0);
                    status.setText("✅ 上传成功");
                    status.setStyle("-fx-text-fill:green;");
                    pathLabel.setText("文件：" + target.getName());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    status.setText("❌ 上传失败");
                    status.setStyle("-fx-text-fill:red;");
                });
            }
        }).start();
    }
    public void viewResume(Applicant applicant, Label statusLabel) {
        try {
            if (applicant == null || applicant.getResumePath() == null || applicant.getResumePath().isBlank()) {
                statusLabel.setText("❌ No resume uploaded yet");
                statusLabel.setStyle("-fx-text-fill:red;");
                return;
            }

            File file = new File(applicant.getResumePath());

            if (!file.exists()) {
                statusLabel.setText("❌ Resume file not found");
                statusLabel.setStyle("-fx-text-fill:red;");
                return;
            }

            if (!Desktop.isDesktopSupported()) {
                statusLabel.setText("❌ Desktop open is not supported on this system");
                statusLabel.setStyle("-fx-text-fill:red;");
                return;
            }

            Desktop.getDesktop().open(file);

            statusLabel.setText("✅ Resume opened");
            statusLabel.setStyle("-fx-text-fill:green;");
        } catch (Exception e) {
            statusLabel.setText("❌ Failed to open resume");
            statusLabel.setStyle("-fx-text-fill:red;");
            e.printStackTrace();
        }
    }
}
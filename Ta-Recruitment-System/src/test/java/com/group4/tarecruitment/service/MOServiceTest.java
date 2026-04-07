package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.repository.ApplicationRepository;
import com.group4.tarecruitment.repository.JobRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MOServiceTest {

    private static final Path JOBS_PATH = Path.of("data", "jobs.csv");
    private static final Path APPS_PATH = Path.of("data", "applications.csv");

    private void cleanJobsFiles() {
        try {
            Files.deleteIfExists(JOBS_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cleanup jobs file", e);
        }
    }

    private void cleanApplicationFiles() {
        try {
            Files.deleteIfExists(APPS_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cleanup applications file", e);
        }
    }

    private Application findById(List<Application> all, String id) {
        for (Application a : all) {
            if (id.equals(a.getApplicationId())) return a;
        }
        return null;
    }

    @Test
    void shouldReviewApplication_approved_whenPendingAndJobBelongsToMo() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";
        String jobId = "JOB-1";
        String appId = "APP-1";

        JobRepository jobRepo = new JobRepository();
        jobRepo.saveAll(List.of(
                new Job(jobId, "math", "Module TA", 10, moName, "mo@bupt.edu",
                        "Recruiting", "2026-03-01 10:00:00", "math grade>90",
                        "help student", "2026-9")
        ));

        ApplicationRepository appRepo = new ApplicationRepository();
        appRepo.saveAll(List.of(
                new Application(appId, "TA-1", jobId, "2026-03-02 12:00:00", "Pending", "")
        ));

        MOService service = new MOService();
        boolean ok = service.reviewApplication(appId, moName, "Approved", "good");
        assertTrue(ok);

        List<Application> after = appRepo.loadAll();
        Application updated = findById(after, appId);
        assertNotNull(updated);
        assertEquals("Approved", updated.getStatus());
        assertEquals("good", updated.getReviewComment());
    }

    @Test
    void shouldReviewApplication_rejected_whenPendingAndJobBelongsToMo() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";
        String jobId = "JOB-2";
        String appId = "APP-2";

        new JobRepository().saveAll(List.of(
                new Job(jobId, "java", "Module TA", 10, moName, "mo@bupt.edu",
                        "Recruiting", "2026-03-03 10:00:00", "java grade>90",
                        "help teacher", "2026-9")
        ));

        new ApplicationRepository().saveAll(List.of(
                new Application(appId, "TA-2", jobId, "2026-03-04 12:00:00", "Pending", "")
        ));

        MOService service = new MOService();
        boolean ok = service.reviewApplication(appId, moName, "Rejected", "not fit");
        assertTrue(ok);

        List<Application> after = new ApplicationRepository().loadAll();
        Application updated = findById(after, appId);
        assertNotNull(updated);
        assertEquals("Rejected", updated.getStatus());
        assertEquals("not fit", updated.getReviewComment());
    }

    @Test
    void shouldFail_whenReviewCommentTooLong_moreThan50() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";
        String jobId = "JOB-3";
        String appId = "APP-3";

        new JobRepository().saveAll(List.of(
                new Job(jobId, "math", "Module TA", 10, moName, "mo@bupt.edu",
                        "Recruiting", "2026-03-05 10:00:00", "math grade>90",
                        "help student", "2026-9")
        ));

        new ApplicationRepository().saveAll(List.of(
                new Application(appId, "TA-3", jobId, "2026-03-06 12:00:00", "Pending", "")
        ));

        String tooLongComment = "a".repeat(51);

        MOService service = new MOService();
        boolean ok = service.reviewApplication(appId, moName, "Approved", tooLongComment);
        assertFalse(ok);

        List<Application> after = new ApplicationRepository().loadAll();
        Application updated = findById(after, appId);
        assertNotNull(updated);
        // 不应修改 status/reviewComment
        assertEquals("Pending", updated.getStatus());
        assertEquals("", updated.getReviewComment());
    }

    @Test
    void shouldFail_whenApplicationIdNotFound() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";

        // 准备一个岗位，但 applications 里没有该 appId
        new JobRepository().saveAll(List.of(
                new Job("JOB-4", "math", "Module TA", 10, moName, "mo@bupt.edu",
                        "Recruiting", "2026-03-07 10:00:00", "math grade>90",
                        "help student", "2026-9")
        ));
        new ApplicationRepository().saveAll(List.of(
                new Application("APP-OTHER", "TA-4", "JOB-4", "2026-03-08 12:00:00", "Pending", "")
        ));

        MOService service = new MOService();
        boolean ok = service.reviewApplication("APP-MISSING", moName, "Approved", "good");
        assertFalse(ok);

        // 原有申请仍保持不变
        List<Application> after = new ApplicationRepository().loadAll();
        Application existing = findById(after, "APP-OTHER");
        assertNotNull(existing);
        assertEquals("Pending", existing.getStatus());
        assertEquals("", existing.getReviewComment());
    }

    @Test
    void shouldFail_whenJobDoesNotBelongToMo() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";
        String otherMoName = "mo2";
        String jobId = "JOB-5";
        String appId = "APP-5";

        new JobRepository().saveAll(List.of(
                new Job(jobId, "java", "Module TA", 10, otherMoName, "other@bupt.edu",
                        "Recruiting", "2026-03-09 10:00:00", "java grade>90",
                        "help teacher", "2026-9")
        ));

        new ApplicationRepository().saveAll(List.of(
                new Application(appId, "TA-5", jobId, "2026-03-10 12:00:00", "Pending", "")
        ));

        MOService service = new MOService();
        boolean ok = service.reviewApplication(appId, moName, "Approved", "good");
        assertFalse(ok);

        List<Application> after = new ApplicationRepository().loadAll();
        Application updated = findById(after, appId);
        assertNotNull(updated);
        assertEquals("Pending", updated.getStatus());
        assertEquals("", updated.getReviewComment());
    }

    @Test
    void shouldFail_whenApplicationStatusNotPending() throws Exception {
        cleanJobsFiles();
        cleanApplicationFiles();

        String moName = "mo1";
        String jobId = "JOB-6";
        String appId = "APP-6";

        new JobRepository().saveAll(List.of(
                new Job(jobId, "math", "Module TA", 10, moName, "mo@bupt.edu",
                        "Recruiting", "2026-03-11 10:00:00", "math grade>90",
                        "help student", "2026-9")
        ));

        new ApplicationRepository().saveAll(List.of(
                new Application(appId, "TA-6", jobId, "2026-03-12 12:00:00", "Approved", "old")
        ));

        MOService service = new MOService();
        boolean ok = service.reviewApplication(appId, moName, "Rejected", "new");
        assertFalse(ok);

        List<Application> after = new ApplicationRepository().loadAll();
        Application updated = findById(after, appId);
        assertNotNull(updated);
        assertEquals("Approved", updated.getStatus());
        assertEquals("old", updated.getReviewComment());
    }
}


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

public class JobServiceTest {

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

    @Test
    void shouldGetActiveJobs_onlyRecruiting_sortedByReleaseTimeDesc() throws Exception {
        cleanJobsFiles();

        JobRepository jobRepo = new JobRepository();
        List<Job> jobs = List.of(
                new Job("JOB-1", "math", "Module TA", 10, "mo", "mo@bupt.edu",
                        "Recruiting", "2026-03-01 10:00:00", "math grade>90", "help student", "2026-9"),
                new Job("JOB-2", "java", "Module TA", 40, "mo", "mo@bupt.edu",
                        "Closed", "2026-03-05 10:00:00", "java grade>90", "help teacher", "2026-5"),
                new Job("JOB-3", "math", "Module TA", 13, "huang", "huang@bupt.edu",
                        "Recruiting", "2026-03-10 23:31:18", "math skill", "help teacher", "2026-9")
        );
        jobRepo.saveAll(jobs);

        JobService service = new JobService();
        List<Job> active = service.getActiveJobs();

        assertEquals(2, active.size());
        assertEquals("JOB-3", active.get(0).getJobId()); // newer first
        assertEquals("JOB-1", active.get(1).getJobId()); // older second
        assertEquals("Recruiting", active.get(0).getStatus());
        assertEquals("Recruiting", active.get(1).getStatus());
    }

    @Test
    void shouldSubmitApplication_firstTime_returnsAppId_andSavesPending() throws Exception {
        cleanApplicationFiles();

        JobService service = new JobService();
        String taId = "TA-001";
        String jobId = "JOB-XYZ";

        String appId = service.submitApplication(taId, jobId);
        assertNotNull(appId);
        assertTrue(appId.startsWith("APP-"));

        ApplicationRepository appRepo = new ApplicationRepository();
        List<Application> all = appRepo.loadAll();
        assertEquals(1, all.size());

        Application saved = all.get(0);
        assertEquals(appId, saved.getApplicationId());
        assertEquals(taId, saved.getTaId());
        assertEquals(jobId, saved.getJobId());
        assertEquals("Pending", saved.getStatus());
        assertEquals("", saved.getReviewComment());
    }

    @Test
    void shouldSubmitApplication_secondTime_returnsNull_andDoesNotDuplicate() throws Exception {
        cleanApplicationFiles();

        JobService service = new JobService();
        String taId = "TA-002";
        String jobId = "JOB-ABC";

        String first = service.submitApplication(taId, jobId);
        assertNotNull(first);

        ApplicationRepository appRepo = new ApplicationRepository();
        assertEquals(1, appRepo.loadAll().size());

        String second = service.submitApplication(taId, jobId);
        assertNull(second);

        assertEquals(1, appRepo.loadAll().size());
    }

    @Test
    void shouldGetMyApplications_filtersByTaId_andSortedByApplicationTimeDesc() throws Exception {
        cleanApplicationFiles();

        ApplicationRepository appRepo = new ApplicationRepository();
        List<Application> apps = List.of(
                new Application("APP-1", "TA-1", "JOB-1", "2026-04-01 10:00:00", "Pending", ""),
                new Application("APP-2", "TA-1", "JOB-2", "2026-04-02 09:00:00", "Approved", "ok"),
                new Application("APP-3", "TA-2", "JOB-1", "2026-04-03 08:00:00", "Pending", "")
        );
        appRepo.saveAll(apps);

        JobService service = new JobService();
        List<Application> mine = service.getMyApplications("TA-1");

        assertEquals(2, mine.size());
        // 2026-04-02 09:00:00 should come before 2026-04-01 10:00:00
        assertEquals("APP-2", mine.get(0).getApplicationId());
        assertEquals("APP-1", mine.get(1).getApplicationId());
        assertEquals("TA-1", mine.get(0).getTaId());
        assertEquals("TA-1", mine.get(1).getTaId());
    }

    @Test
    void shouldGetJobById_returnsJobOrNull() throws Exception {
        cleanJobsFiles();

        JobRepository jobRepo = new JobRepository();
        List<Job> jobs = List.of(
                new Job("JOB-A", "math", "Module TA", 10, "mo", "mo@bupt.edu",
                        "Recruiting", "2026-03-01 10:00:00", "math grade>90", "help student", "2026-9"),
                new Job("JOB-B", "java", "Module TA", 20, "mo", "mo@bupt.edu",
                        "Recruiting", "2026-03-02 10:00:00", "java grade>90", "help student", "2026-9")
        );
        jobRepo.saveAll(jobs);

        JobService service = new JobService();
        Job found = service.getJobById("JOB-B");
        assertNotNull(found);
        assertEquals("java", found.getCourseName());

        Job missing = service.getJobById("JOB-MISSING");
        assertNull(missing);
    }
}


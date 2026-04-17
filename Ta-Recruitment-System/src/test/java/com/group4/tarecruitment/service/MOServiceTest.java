package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.repository.ApplicationRepository;
import com.group4.tarecruitment.repository.JobRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MOService.
 *
 * 为什么用集成测试而不是 Mock：
 *   reviewApplication() 的权限校验依赖"读 Job 文件确认 moName"，
 *   状态变更依赖"读出 Application → 修改 → 写回文件"的完整流程。
 *   Mock 掉后只能验证调用链，无法验证数据是否真的被正确持久化。
 *
 * 优化点：
 *   1. 用 @TempDir 替代手动删文件，每个测试完全隔离
 *   2. 用 @BeforeEach 统一初始化依赖
 *   3. 提取 seedJob / seedApp / findById 辅助方法，消除重复
 *   4. 每个测试的 Arrange 只准备当前测试需要的最少数据
 */
@DisplayName("MOService Integration Tests")
public class MOServiceTest {

    @TempDir
    Path tempDir;

    private MOService moService;
    private JobRepository jobRepo;
    private ApplicationRepository appRepo;

    @BeforeEach
    void setUp() {
        Path jobsPath = tempDir.resolve("jobs.csv");
        Path appsPath = tempDir.resolve("applications.csv");

        jobRepo   = new JobRepository(jobsPath);
        appRepo   = new ApplicationRepository(appsPath);
        moService = new MOService(jobsPath, appsPath);
    }

    // ── 辅助方法 ─────────────────────────────────────────────────────────────

    /** 写入单个 Job 到文件 */
    private void seedJob(String jobId, String course, String moName) throws Exception {
        jobRepo.saveAll(List.of(new Job(
                jobId, course, "Module TA", 10, moName, moName + "@bupt.edu",
                "Recruiting", "2026-03-01 10:00:00", "skill", "help", "2026-9"
        )));
    }

    /** 写入单个 Application 到文件 */
    private void seedApp(String appId, String taId, String jobId,
                         String status, String comment) throws Exception {
        appRepo.saveAll(List.of(
                new Application(appId, taId, jobId, "2026-03-02 12:00:00", status, comment)
        ));
    }

    /** 从文件中按 ID 查找 Application */
    private Application findById(String appId) throws Exception {
        return appRepo.loadAll().stream()
                .filter(a -> appId.equals(a.getApplicationId()))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // 正常场景
    // =========================================================================

    @Test
    @DisplayName("Review approved: status becomes Approved and comment is saved")
    void reviewApplication_approved_updatesStatusAndComment() throws Exception {
        seedJob("JOB-1", "math", "mo1");
        seedApp("APP-1", "TA-1", "JOB-1", "Pending", "");

        boolean result = moService.reviewApplication("APP-1", "mo1", "Approved", "good");

        assertTrue(result);
        Application updated = findById("APP-1");
        assertNotNull(updated);
        assertEquals("Approved", updated.getStatus());
        assertEquals("good",     updated.getReviewComment());
    }

    @Test
    @DisplayName("Review rejected: status becomes Rejected and comment is saved")
    void reviewApplication_rejected_updatesStatusAndComment() throws Exception {
        seedJob("JOB-2", "java", "mo1");
        seedApp("APP-2", "TA-2", "JOB-2", "Pending", "");

        boolean result = moService.reviewApplication("APP-2", "mo1", "Rejected", "not fit");

        assertTrue(result);
        Application updated = findById("APP-2");
        assertNotNull(updated);
        assertEquals("Rejected", updated.getStatus());
        assertEquals("not fit",  updated.getReviewComment());
    }

    // =========================================================================
    // 输入校验
    // =========================================================================

    @Test
    @DisplayName("Review fails: comment longer than 50 characters is rejected")
    void reviewApplication_commentTooLong_returnsFalse() throws Exception {
        seedJob("JOB-3", "math", "mo1");
        seedApp("APP-3", "TA-3", "JOB-3", "Pending", "");

        boolean result = moService.reviewApplication("APP-3", "mo1", "Approved", "a".repeat(51));

        assertFalse(result);
    }

    @Test
    @DisplayName("Review fails (too long comment): original status and comment unchanged")
    void reviewApplication_commentTooLong_dataUnchanged() throws Exception {
        seedJob("JOB-3", "math", "mo1");
        seedApp("APP-3", "TA-3", "JOB-3", "Pending", "");

        moService.reviewApplication("APP-3", "mo1", "Approved", "a".repeat(51));

        Application updated = findById("APP-3");
        assertNotNull(updated);
        assertEquals("Pending", updated.getStatus());
        assertEquals("",        updated.getReviewComment());
    }

    // =========================================================================
    // 边界情况
    // =========================================================================

    @Test
    @DisplayName("Review fails: application ID not found returns false")
    void reviewApplication_appNotFound_returnsFalse() throws Exception {
        seedJob("JOB-4", "math", "mo1");
        seedApp("APP-OTHER", "TA-4", "JOB-4", "Pending", "");

        boolean result = moService.reviewApplication("APP-MISSING", "mo1", "Approved", "good");

        assertFalse(result);
    }

    @Test
    @DisplayName("Review fails (app not found): existing application is unchanged")
    void reviewApplication_appNotFound_existingDataUnchanged() throws Exception {
        seedJob("JOB-4", "math", "mo1");
        seedApp("APP-OTHER", "TA-4", "JOB-4", "Pending", "");

        moService.reviewApplication("APP-MISSING", "mo1", "Approved", "good");

        Application existing = findById("APP-OTHER");
        assertNotNull(existing);
        assertEquals("Pending", existing.getStatus());
        assertEquals("",        existing.getReviewComment());
    }

    // =========================================================================
    // 权限控制
    // =========================================================================

    @Test
    @DisplayName("Review fails: MO cannot review another MO's job")
    void reviewApplication_wrongMo_returnsFalse() throws Exception {
        seedJob("JOB-5", "java", "mo2");   // 岗位属于 mo2
        seedApp("APP-5", "TA-5", "JOB-5", "Pending", "");

        boolean result = moService.reviewApplication("APP-5", "mo1", "Approved", "good");  // mo1 审核

        assertFalse(result);
    }

    @Test
    @DisplayName("Review fails (wrong MO): application status and comment unchanged")
    void reviewApplication_wrongMo_dataUnchanged() throws Exception {
        seedJob("JOB-5", "java", "mo2");
        seedApp("APP-5", "TA-5", "JOB-5", "Pending", "");

        moService.reviewApplication("APP-5", "mo1", "Approved", "good");

        Application updated = findById("APP-5");
        assertNotNull(updated);
        assertEquals("Pending", updated.getStatus());
        assertEquals("",        updated.getReviewComment());
    }

    // =========================================================================
    // 状态机约束
    // =========================================================================

    @Test
    @DisplayName("Review fails: already Approved application cannot be reviewed again")
    void reviewApplication_alreadyApproved_returnsFalse() throws Exception {
        seedJob("JOB-6", "math", "mo1");
        seedApp("APP-6", "TA-6", "JOB-6", "Approved", "old");  // 已经是 Approved

        boolean result = moService.reviewApplication("APP-6", "mo1", "Rejected", "new");

        assertFalse(result);
    }

    @Test
    @DisplayName("Review fails (not Pending): original status and comment preserved")
    void reviewApplication_alreadyApproved_originalDataPreserved() throws Exception {
        seedJob("JOB-6", "math", "mo1");
        seedApp("APP-6", "TA-6", "JOB-6", "Approved", "old");

        moService.reviewApplication("APP-6", "mo1", "Rejected", "new");

        Application updated = findById("APP-6");
        assertNotNull(updated);
        assertEquals("Approved", updated.getStatus());
        assertEquals("old",      updated.getReviewComment());
    }
}
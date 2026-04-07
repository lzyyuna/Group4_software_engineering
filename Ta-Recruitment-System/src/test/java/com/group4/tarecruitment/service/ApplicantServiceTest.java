package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.repository.ApplicantCsvRepository;
import com.group4.tarecruitment.repository.ApplicantJsonRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicantServiceTest {

    private static final Path CSV_PATH = Path.of("data", "applicants.csv");
    private static final Path JSON_PATH = Path.of("data", "applicants.json");

    private void cleanApplicantFiles() {
        // 仅清理与 TA 创建/编辑档案相关的数据文件，避免影响其它测试
        try {
            Files.deleteIfExists(CSV_PATH);
            Files.deleteIfExists(JSON_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cleanup applicant files", e);
        }
    }

    @Test
    void shouldAddApplicantSuccessfully() throws Exception {
        cleanApplicantFiles();

        ApplicantService service = new ApplicantService();
        Applicant applicant = new Applicant("TA-TEST-1", "S001", "Alice", "alice@test.com", "Java", "Java,Communication", "123456");
        applicant.setUsername("user1");

        service.addApplicant(applicant);

        assertEquals(1, service.getAllApplicants().size());
        assertEquals("Alice", service.getAllApplicants().get(0).getName());

        // 校验 CSV 查询
        Applicant byStudentId = service.getApplicantByStudentId("S001");
        assertNotNull(byStudentId);
        assertEquals("Alice", byStudentId.getName());
        assertEquals("alice@test.com", byStudentId.getEmail());
        assertEquals("Java", byStudentId.getCourses());
        assertEquals("Java,Communication", byStudentId.getSkillTags());
        assertEquals("123456", byStudentId.getContact());

        Applicant byUsername = service.getApplicantByUsername("user1");
        assertNotNull(byUsername);
        assertEquals("S001", byUsername.getStudentId());

        // 校验 JSON 持久化
        ApplicantJsonRepository jsonRepo = new ApplicantJsonRepository();
        List<Applicant> jsonList = jsonRepo.loadAll();
        assertEquals(1, jsonList.size());
        assertEquals("TA-TEST-1", jsonList.get(0).getTaId());
        assertEquals("Alice", jsonList.get(0).getName());
        assertEquals("alice@test.com", jsonList.get(0).getEmail());

        // 校验 CSV 持久化（直接读 CSV 文件）
        ApplicantCsvRepository csvRepo = new ApplicantCsvRepository();
        List<Applicant> csvList = csvRepo.loadAll();
        assertEquals(1, csvList.size());
        assertEquals("TA-TEST-1", csvList.get(0).getTaId());
        assertEquals("Alice", csvList.get(0).getName());
    }

    @Test
    void shouldUpdateApplicantSuccessfully_csvAndJson() throws Exception {
        cleanApplicantFiles();

        ApplicantService service = new ApplicantService();
        Applicant origin = new Applicant(
                "TA-TEST-2",
                "S002",
                "Bob",
                "bob@test.com",
                "Python",
                "Python",
                "111"
        );
        origin.setUsername("user2");
        service.addApplicant(origin);

        Applicant updated = new Applicant(
                "TA-TEST-2",
                "S002",
                "Bobby",
                "bobby@test.com",
                "Python,Java",
                "Python,Java",
                "222"
        );
        updated.setUsername("user2");
        service.updateApplicant(updated);

        Applicant byStudentId = service.getApplicantByStudentId("S002");
        assertNotNull(byStudentId);
        assertEquals("Bobby", byStudentId.getName());
        assertEquals("bobby@test.com", byStudentId.getEmail());
        assertEquals("Python,Java", byStudentId.getCourses());
        assertEquals("Python,Java", byStudentId.getSkillTags());
        assertEquals("222", byStudentId.getContact());

        // JSON 校验
        ApplicantJsonRepository jsonRepo = new ApplicantJsonRepository();
        List<Applicant> jsonList = jsonRepo.loadAll();
        assertEquals(1, jsonList.size());
        assertEquals("Bobby", jsonList.get(0).getName());
        assertEquals("bobby@test.com", jsonList.get(0).getEmail());
    }
}
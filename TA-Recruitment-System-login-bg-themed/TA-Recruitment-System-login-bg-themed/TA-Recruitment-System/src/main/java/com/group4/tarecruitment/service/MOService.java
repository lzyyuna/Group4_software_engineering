package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.repository.ApplicationRepository;
import com.group4.tarecruitment.repository.JobRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MOService {
    private final JobRepository jobRepo = new JobRepository();
    private final ApplicationRepository appRepo = new ApplicationRepository();

    // MO-001: 发布TA招聘职位
    public String postJob(String courseName, String positionType, int weeklyWorkload,
                          String moName, String moEmail, String skillRequirements,
                          String jobContent, String deadline) throws Exception {
        // 验证必填字段
        if (courseName == null || courseName.trim().isEmpty() ||
            positionType == null || positionType.trim().isEmpty() ||
            weeklyWorkload <= 0) {
            return null; // 验证失败
        }

        String jobId = "JOB-" + UUID.randomUUID().toString().substring(0, 8);
        String releaseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Job job = new Job(jobId, courseName, positionType, weeklyWorkload,
                moName, moEmail, "Recruiting", releaseTime, skillRequirements,
                jobContent, deadline);

        List<Job> jobs = jobRepo.loadAll();
        jobs.add(job);
        jobRepo.saveAll(jobs);

        return jobId;
    }

    // MO-002: 查看MO发布的所有职位（按发布时间倒序）
    public List<Job> getMyPostedJobs(String moName) throws Exception {
        return jobRepo.loadAll().stream()
                .filter(j -> j.getMoName().equals(moName))
                .sorted(Comparator.comparing(Job::getReleaseTime).reversed())
                .collect(Collectors.toList());
    }

    // MO-003: 编辑已发布的活跃职位
    public boolean editJob(String jobId, String moName, String courseName,
                           String positionType, int weeklyWorkload,
                           String skillRequirements, String jobContent,
                           String deadline) throws Exception {
        List<Job> jobs = jobRepo.loadAll();
        Optional<Job> jobOpt = jobs.stream()
                .filter(j -> j.getJobId().equals(jobId) && j.getMoName().equals(moName))
                .findFirst();

        if (jobOpt.isEmpty()) {
            return false; // 职位不存在或不属于该MO
        }

        Job job = jobOpt.get();
        if (!"Recruiting".equals(job.getStatus())) {
            return false; // 只有Recruiting状态的职位可以编辑
        }

        // 验证必填字段
        if (courseName == null || courseName.trim().isEmpty() ||
            positionType == null || positionType.trim().isEmpty() ||
            weeklyWorkload <= 0) {
            return false;
        }

        // 更新字段（jobId不变）
        job.setCourseName(courseName);
        job.setPositionType(positionType);
        job.setWeeklyWorkload(weeklyWorkload);
        job.setSkillRequirements(skillRequirements);
        job.setJobContent(jobContent);
        job.setDeadline(deadline);

        jobRepo.saveAll(jobs);
        return true;
    }

    // MO-004: 关闭已发布职位
    public boolean closeJob(String jobId, String moName) throws Exception {
        List<Job> jobs = jobRepo.loadAll();
        Optional<Job> jobOpt = jobs.stream()
                .filter(j -> j.getJobId().equals(jobId) && j.getMoName().equals(moName))
                .findFirst();

        if (jobOpt.isEmpty()) {
            return false;
        }

        Job job = jobOpt.get();
        if (!"Recruiting".equals(job.getStatus())) {
            return false; // 只有Recruiting状态的职位可以关闭
        }

        job.setStatus("Closed");
        jobRepo.saveAll(jobs);
        return true;
    }

    // MO-005: 查看职位的申请记录
    public List<Application> getJobApplications(String jobId, String moName) throws Exception {
        // 验证该职位是否属于该MO
        List<Job> jobs = jobRepo.loadAll();
        boolean isMyJob = jobs.stream()
                .anyMatch(j -> j.getJobId().equals(jobId) && j.getMoName().equals(moName));

        if (!isMyJob) {
            return List.of(); // 返回空列表
        }

        return appRepo.loadAll().stream()
                .filter(a -> a.getJobId().equals(jobId))
                .sorted(Comparator.comparing(Application::getApplicationTime).reversed())
                .collect(Collectors.toList());
    }

    // MO-006: 审核申请（批准/拒绝）
    public boolean reviewApplication(String applicationId, String moName,
                                     String reviewResult, String reviewComment) throws Exception {
        // 验证评论长度
        if (reviewComment != null && reviewComment.length() > 50) {
            return false;
        }

        List<Application> apps = appRepo.loadAll();
        Optional<Application> appOpt = apps.stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst();

        if (appOpt.isEmpty()) {
            return false;
        }

        Application app = appOpt.get();

        // 验证该申请对应的职位是否属于该MO
        List<Job> jobs = jobRepo.loadAll();
        boolean isMyJob = jobs.stream()
                .anyMatch(j -> j.getJobId().equals(app.getJobId()) && j.getMoName().equals(moName));

        if (!isMyJob) {
            return false;
        }

        // 只有Pending状态的申请可以审核
        if (!"Pending".equals(app.getStatus())) {
            return false;
        }

        app.setStatus(reviewResult); // "Approved" 或 "Rejected"
        app.setReviewComment(reviewComment);

        appRepo.saveAll(apps);
        return true;
    }

    // 根据jobId获取职位
    public Job getJobById(String jobId) throws Exception {
        return jobRepo.loadAll().stream()
                .filter(j -> j.getJobId().equals(jobId))
                .findFirst().orElse(null);
    }
}

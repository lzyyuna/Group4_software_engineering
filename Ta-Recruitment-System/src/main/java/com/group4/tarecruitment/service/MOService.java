package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.repository.ApplicationRepository;
import com.group4.tarecruitment.repository.JobRepository;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MOService {
    private final JobRepository jobRepo;
    private final ApplicationRepository appRepo;

    // 生产环境使用默认路径
    public MOService() {
        this.jobRepo = new JobRepository();
        this.appRepo = new ApplicationRepository();
    }

    // 测试专用构造器，接收自定义路径
    public MOService(Path jobsPath, Path appsPath) {
        this.jobRepo = new JobRepository(jobsPath);
        this.appRepo = new ApplicationRepository(appsPath);
    }

    // MO-001: 发布TA招聘职位
    public String postJob(String courseName, String positionType, int weeklyWorkload,
                          String moName, String moEmail, String skillRequirements,
                          String jobContent, String deadline) throws Exception {
        if (courseName == null || courseName.trim().isEmpty() ||
            positionType == null || positionType.trim().isEmpty() ||
            weeklyWorkload <= 0) {
            return null;
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
            return false;
        }

        Job job = jobOpt.get();
        if (!"Recruiting".equals(job.getStatus())) {
            return false;
        }

        if (courseName == null || courseName.trim().isEmpty() ||
            positionType == null || positionType.trim().isEmpty() ||
            weeklyWorkload <= 0) {
            return false;
        }

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
            return false;
        }

        job.setStatus("Closed");
        jobRepo.saveAll(jobs);
        return true;
    }

    // MO-005: 查看职位的申请记录
    public List<Application> getJobApplications(String jobId, String moName) throws Exception {
        List<Job> jobs = jobRepo.loadAll();
        boolean isMyJob = jobs.stream()
                .anyMatch(j -> j.getJobId().equals(jobId) && j.getMoName().equals(moName));

        if (!isMyJob) {
            return List.of();
        }

        return appRepo.loadAll().stream()
                .filter(a -> a.getJobId().equals(jobId))
                .sorted(Comparator.comparing(Application::getApplicationTime).reversed())
                .collect(Collectors.toList());
    }

    // MO-006: 审核申请（批准/拒绝）
    public boolean reviewApplication(String applicationId, String moName,
                                     String reviewResult, String reviewComment) throws Exception {
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

        List<Job> jobs = jobRepo.loadAll();
        boolean isMyJob = jobs.stream()
                .anyMatch(j -> j.getJobId().equals(app.getJobId()) && j.getMoName().equals(moName));

        if (!isMyJob) {
            return false;
        }

        if (!"Pending".equals(app.getStatus())) {
            return false;
        }

        app.setStatus(reviewResult);
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
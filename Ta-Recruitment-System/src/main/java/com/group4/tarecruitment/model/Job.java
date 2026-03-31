package com.group4.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private String jobId;
    private String courseName;
    private String positionType;
    private int weeklyWorkload;
    private String moName;
    private String moEmail;
    private String status; // Recruiting / Closed
    private String releaseTime;
    private String skillRequirements;
    private String jobContent;
    private String deadline;

    // 无参构造 + 全参构造 + get/set 方法（和 Applicant 格式保持一致）
    public Job() {}

    public Job(String jobId, String courseName, String positionType, int weeklyWorkload,
               String moName, String moEmail, String status, String releaseTime, String skillRequirements,
               String jobContent, String deadline) {
        this.jobId = jobId;
        this.courseName = courseName;
        this.positionType = positionType;
        this.weeklyWorkload = weeklyWorkload;
        this.moName = moName;
        this.moEmail = moEmail;
        this.status = status;
        this.releaseTime = releaseTime;
        this.skillRequirements = skillRequirements;
        this.jobContent = jobContent;
        this.deadline = deadline;
    }

    // 生成所有 getter 和 setter
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getPositionType() { return positionType; }
    public void setPositionType(String positionType) { this.positionType = positionType; }
    public int getWeeklyWorkload() { return weeklyWorkload; }
    public void setWeeklyWorkload(int weeklyWorkload) { this.weeklyWorkload = weeklyWorkload; }
    public String getMoName() { return moName; }
    public void setMoName(String moName) { this.moName = moName; }
    public String getMoEmail() { return moEmail; }
    public void setMoEmail(String moEmail) { this.moEmail = moEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReleaseTime() { return releaseTime; }
    public void setReleaseTime(String releaseTime) { this.releaseTime = releaseTime; }
    public String getSkillRequirements() { return skillRequirements; }
    public void setSkillRequirements(String skillRequirements) { this.skillRequirements = skillRequirements; }
    public String getJobContent() { return jobContent; }
    public void setJobContent(String jobContent) { this.jobContent = jobContent; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
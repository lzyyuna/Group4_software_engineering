package com.group4.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {
    private String applicationId;
    private String taId;
    private String jobId;
    private String applicationTime;
    private String status; // Pending / Approved / Rejected
    private String reviewComment; // 最多50字

    public Application() {}

    public Application(String applicationId, String taId, String jobId,
                       String applicationTime, String status, String reviewComment) {
        this.applicationId = applicationId;
        this.taId = taId;
        this.jobId = jobId;
        this.applicationTime = applicationTime;
        this.status = status;
        this.reviewComment = reviewComment;
    }

    // get/set 方法
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getApplicationTime() { return applicationTime; }
    public void setApplicationTime(String applicationTime) { this.applicationTime = applicationTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
}
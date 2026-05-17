package com.group4.tarecruitment.model;

public class InviteCode {
    private String code;
    private String role;
    private boolean used;
    private String createdAt;

    public InviteCode() {}

    public InviteCode(String code, String role, boolean used, String createdAt) {
        this.code = code;
        this.role = role;
        this.used = used;
        this.createdAt = createdAt;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

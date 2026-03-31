package com.group4.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Applicant {
    private String taId;
    private String studentId;
    private String name;
    private String email;
    private String courses;
    private String skillTags;
    private String contact;
    private String password;
    private String resumePath;
    private String username; // 新增：绑定登录账号

    public Applicant() {}

    // 保留原有构造器
    public Applicant(String taId, String studentId, String name, String email,
                     String courses, String skillTags, String contact) {
        this.taId = taId;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.courses = courses;
        this.skillTags = skillTags;
        this.contact = contact;
        this.password = "";
        this.resumePath = "";
    }

    // 新增：带 username 的构造器
    public Applicant(String taId, String studentId, String name, String email,
                     String courses, String skillTags, String contact, String username) {
        this.taId = taId;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.courses = courses;
        this.skillTags = skillTags;
        this.contact = contact;
        this.password = "";
        this.resumePath = "";
        this.username = username;
    }

    // 补全所有 getter/setter
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourses() { return courses; }
    public void setCourses(String courses) { this.courses = courses; }

    public String getSkillTags() { return skillTags; }
    public void setSkillTags(String skillTags) { this.skillTags = skillTags; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // 别名方法，用于兼容不同命名
    public String getPhone() { return contact; }
    public String getSkills() { return skillTags; }
    public String getCvPath() { return resumePath; }
}
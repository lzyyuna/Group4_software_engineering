package com.group4.tarecruitment.model;

public class Admin {
    private String taId;
    private String taName;
    private String hireMo;
    private String positionName;
    private String courseName;
    private String department;
    private double weeklyWorkload;
    private double totalWorkload;
    private String hireDate;

    public Admin() {}

    public Admin(String taId, String taName, String hireMo, String positionName,
                 String courseName, String department, double weeklyWorkload,
                 double totalWorkload, String hireDate) {
        this.taId = taId;
        this.taName = taName;
        this.hireMo = hireMo;
        this.positionName = positionName;
        this.courseName = courseName;
        this.department = department;
        this.weeklyWorkload = weeklyWorkload;
        this.totalWorkload = totalWorkload;
        this.hireDate = hireDate;
    }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getTaName() { return taName; }
    public void setTaName(String taName) { this.taName = taName; }

    public String getHireMo() { return hireMo; }
    public void setHireMo(String hireMo) { this.hireMo = hireMo; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getWeeklyWorkload() { return weeklyWorkload; }
    public void setWeeklyWorkload(double weeklyWorkload) { this.weeklyWorkload = weeklyWorkload; }

    public double getTotalWorkload() { return totalWorkload; }
    public void setTotalWorkload(double totalWorkload) { this.totalWorkload = totalWorkload; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    @Override
    public String toString() {
        return "TA ID: " + taId +
                " | Name: " + taName +
                " | Hiring MO: " + hireMo +
                " | Course: " + courseName +
                " | Department: " + department +
                " | Weekly Workload: " + weeklyWorkload +
                " | Total Workload: " + totalWorkload +
                " | Hire Date: " + hireDate;
    }
}
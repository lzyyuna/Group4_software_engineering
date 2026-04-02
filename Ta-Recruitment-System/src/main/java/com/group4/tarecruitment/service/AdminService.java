package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.repository.AdminRepository;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminService {
    private AdminRepository repository = new AdminRepository();
    private double workloadThreshold = 10.0;

    // ===================== AD-001 =====================
    public List<Admin> getTaWorkloadOverview() {
        System.out.println("===== AD-001: TA Workload Overview =====");
        List<Admin> list = repository.findAllApprovedTa();
        list.sort((a, b) -> Double.compare(b.getTotalWorkload(), a.getTotalWorkload()));
        for (Admin admin : list) {
            System.out.println(admin);
        }
        return list;
    }

    public List<Admin> filterTa(String taName, String courseName, String moName) {
        List<Admin> list = repository.findAllApprovedTa();
        return list.stream()
                .filter(ta -> (taName == null || ta.getTaName().contains(taName)))
                .filter(ta -> (courseName == null || ta.getCourseName().contains(courseName)))
                .filter(ta -> (moName == null || ta.getHireMo().contains(moName)))
                .collect(Collectors.toList());
    }

    // ===================== AD-002 =====================
    public void exportWorkloadToCsv() {
        System.out.println("\n===== AD-002: Export CSV File =====");
        List<Admin> list = repository.findAllApprovedTa();
        String filePath = "TA_Workload_Statistics.csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("TA ID,Name,Hiring MO,Position,Course,Department,Weekly,Total,Hire Date\n");
            for (Admin ta : list) {
                writer.write(ta.getTaId() + "," +
                        ta.getTaName() + "," +
                        ta.getHireMo() + "," +
                        ta.getPositionName() + "," +
                        ta.getCourseName() + "," +
                        ta.getDepartment() + "," +
                        ta.getWeeklyWorkload() + "," +
                        ta.getTotalWorkload() + "," +
                        ta.getHireDate() + "\n");
            }
            System.out.println("Export successful! File: " + filePath);
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    // ===================== AD-003 =====================
    public void statByCourse() {
        System.out.println("\n===== AD-003: Statistics by Course =====");
        List<Admin> list = repository.findAllApprovedTa();
        Map<String, List<Admin>> group = list.stream().collect(Collectors.groupingBy(Admin::getCourseName));

        for (Map.Entry<String, List<Admin>> entry : group.entrySet()) {
            String course = entry.getKey();
            List<Admin> tas = entry.getValue();
            long count = tas.size();
            double total = tas.stream().mapToDouble(Admin::getTotalWorkload).sum();
            double avg = total / count;
            System.out.println("Course: " + course + " | Count: " + count + " | Total: " + total + " | Avg: " + String.format("%.1f", avg));
        }
    }

    public void statByDepartment() {
        System.out.println("\n===== AD-003: Statistics by Department =====");
        List<Admin> list = repository.findAllApprovedTa();
        Map<String, List<Admin>> group = list.stream().collect(Collectors.groupingBy(Admin::getDepartment));

        for (Map.Entry<String, List<Admin>> entry : group.entrySet()) {
            String dept = entry.getKey();
            List<Admin> tas = entry.getValue();
            long count = tas.size();
            double total = tas.stream().mapToDouble(Admin::getTotalWorkload).sum();
            double avg = total / count;
            System.out.println("Dept: " + dept + " | Count: " + count + " | Total: " + total + " | Avg: " + String.format("%.1f", avg));
        }
    }

    // ===================== AD-004 =====================
    public void setWorkloadThreshold(double threshold) {
        this.workloadThreshold = threshold;
        System.out.println("Threshold set to: " + threshold + " hours/week");
    }

    public void markOverloadTa() {
        System.out.println("\n===== AD-004: Overloaded TA Warning =====");
        List<Admin> list = repository.findAllApprovedTa();
        list.sort((a, b) -> Double.compare(b.getWeeklyWorkload() - workloadThreshold, a.getWeeklyWorkload() - workloadThreshold));

        for (Admin ta : list) {
            double over = ta.getWeeklyWorkload() - workloadThreshold;
            if (over > 0) {
                System.out.println(ta + " | [OVERLOADED] Excess: " + String.format("%.1f", over) + " | Suggestion: Reduce workload");
            } else {
                System.out.println(ta + " | Normal");
            }
        }
    }
}
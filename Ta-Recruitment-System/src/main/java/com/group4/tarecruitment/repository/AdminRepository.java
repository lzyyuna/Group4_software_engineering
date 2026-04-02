package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.Admin;
import java.util.ArrayList;
import java.util.List;

public class AdminRepository {
    private List<Admin> taList = new ArrayList<>();

    public AdminRepository() {
        initData();
    }

    private void initData() {
        taList.add(new Admin("TA001", "Zhang San", "MO001", "Java TA", "Java Programming", "School of Computer", 12, 48, "2026-04-01"));
        taList.add(new Admin("TA002", "Li Si", "MO002", "Python TA", "Python Basics", "School of Computer", 8, 32, "2026-04-01"));
        taList.add(new Admin("TA003", "Wang Wu", "MO003", "Database TA", "Database Principles", "School of Electronic Info", 15, 60, "2026-04-02"));
        taList.add(new Admin("TA004", "Zhao Liu", "MO001", "Frontend TA", "Web Development", "School of Computer", 9, 36, "2026-04-02"));
    }

    public List<Admin> findAllApprovedTa() {
        return new ArrayList<>(taList);
    }
}
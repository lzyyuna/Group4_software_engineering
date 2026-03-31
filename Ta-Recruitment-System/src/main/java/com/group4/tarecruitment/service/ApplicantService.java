package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.repository.ApplicantCsvRepository;
import com.group4.tarecruitment.repository.ApplicantJsonRepository;

import java.util.List;
import java.util.Optional;

public class ApplicantService {
    private final ApplicantCsvRepository csvRepo = new ApplicantCsvRepository();
    private final ApplicantJsonRepository jsonRepo = new ApplicantJsonRepository();

    // 获取所有申请者
    public List<Applicant> getAllApplicants() throws Exception {
        return csvRepo.loadAll();
    }

    // 根据学号获取
    public Applicant getApplicantByStudentId(String studentId) throws Exception {
        return csvRepo.findByStudentId(studentId);
    }

    // 添加申请者（同时保存到 CSV 和 JSON）
    public void addApplicant(Applicant applicant) throws Exception {
        csvRepo.save(applicant);
        jsonRepo.save(applicant);
    }

    // 更新申请者信息
    public void updateApplicant(Applicant applicant) throws Exception {
        csvRepo.update(applicant);
        jsonRepo.update(applicant);
    }

    // 按用户名查询（登录绑定档案用）
    public Applicant getApplicantByUsername(String username) throws Exception {
        return csvRepo.findByUsername(username);
    }

    // 根据ID获取申请者
    public Applicant getApplicantById(String id) throws Exception {
        return csvRepo.findById(id);
    }
}
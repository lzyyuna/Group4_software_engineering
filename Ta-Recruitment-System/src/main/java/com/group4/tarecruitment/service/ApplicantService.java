package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.repository.ApplicantCsvRepository;
import com.group4.tarecruitment.repository.ApplicantJsonRepository;

import java.util.List;
import java.util.Optional;

public class ApplicantService {
    private final ApplicantCsvRepository csvRepo = new ApplicantCsvRepository();
    private final ApplicantJsonRepository jsonRepo = new ApplicantJsonRepository();

    // 新增申请者
    public void addApplicant(Applicant applicant) throws Exception {
        csvRepo.save(applicant);
        jsonRepo.save(applicant);
    }

    // 查询所有申请者
    public List<Applicant> getAllApplicants() throws Exception {
        return csvRepo.loadAll();
    }

    // 按学号查询申请者（编辑档案用，TA-001验收标准4）
    public Applicant getApplicantByStudentId(String studentId) throws Exception {
        List<Applicant> applicants = csvRepo.loadAll();
        Optional<Applicant> optional = applicants.stream().filter(a -> a.getStudentId().equals(studentId)).findFirst();
        return optional.orElse(null);
    }

    // 更新申请者信息（同步到JSON/CSV，TA-001验收标准4）
    public void updateApplicant(Applicant applicant) throws Exception {
        List<Applicant> applicants = csvRepo.loadAll();
        // 删除原数据，添加新数据
        applicants.removeIf(a -> a.getStudentId().equals(applicant.getStudentId()));
        applicants.add(applicant);
        // 重新保存到文件
        csvRepo.saveAll(applicants);
        jsonRepo.saveAll(applicants);
    }
}
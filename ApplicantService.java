package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.repository.ApplicantCsvRepository;
import com.group4.tarecruitment.repository.ApplicantJsonRepository;

import java.nio.file.Path;
import java.util.List;

public class ApplicantService {
    private final ApplicantCsvRepository csvRepo;
    private final ApplicantJsonRepository jsonRepo;

    public ApplicantService() {
        this.csvRepo  = new ApplicantCsvRepository();
        this.jsonRepo = new ApplicantJsonRepository();
    }

    public ApplicantService(Path csvPath, Path jsonPath) {
        this.csvRepo  = new ApplicantCsvRepository(csvPath);
        this.jsonRepo = new ApplicantJsonRepository(jsonPath);
    }

    public List<Applicant> getAllApplicants() throws Exception {
        return csvRepo.loadAll();
    }

    public Applicant getApplicantByStudentId(String studentId) throws Exception {
        return csvRepo.findByStudentId(studentId);
    }

    public void addApplicant(Applicant applicant) throws Exception {
        csvRepo.save(applicant);
        jsonRepo.save(applicant);
    }

    public void updateApplicant(Applicant applicant) throws Exception {
        csvRepo.update(applicant);
        jsonRepo.update(applicant);
    }

    public Applicant getApplicantByUsername(String username) throws Exception {
        return csvRepo.findByUsername(username);
    }

    public Applicant getApplicantById(String id) throws Exception {
        return csvRepo.findById(id);
    }
}
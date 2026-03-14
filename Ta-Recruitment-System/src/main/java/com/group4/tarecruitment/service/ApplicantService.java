package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import java.util.ArrayList;
import java.util.List;

public class ApplicantService {
    private final List<Applicant> applicants = new ArrayList<>();

    public void addApplicant(Applicant applicant) {
        applicants.add(applicant);
    }

    public List<Applicant> getAllApplicants() {
        return applicants;
    }
}
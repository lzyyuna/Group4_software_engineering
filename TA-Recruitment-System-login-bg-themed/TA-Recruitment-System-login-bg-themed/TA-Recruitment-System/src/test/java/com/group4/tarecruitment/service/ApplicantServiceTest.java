package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.Applicant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicantServiceTest {

    @Test
    void shouldAddApplicantSuccessfully() throws Exception {
        ApplicantService service = new ApplicantService();
        Applicant applicant = new Applicant("A001", "12345", "Alice", "alice@test.com", "Java", "Communication", "1234567890");

        service.addApplicant(applicant);

        assertEquals(1, service.getAllApplicants().size());
        assertEquals("Alice", service.getAllApplicants().get(0).getName());
    }
}
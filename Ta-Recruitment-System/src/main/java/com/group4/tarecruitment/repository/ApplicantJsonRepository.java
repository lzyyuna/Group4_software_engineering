package com.group4.tarecruitment.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.tarecruitment.model.Applicant;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApplicantJsonRepository {
    private final String filePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApplicantJsonRepository() {
        this.filePath = "data/applicants.json";
    }

    public ApplicantJsonRepository(Path path) {
        this.filePath = path.toString();
    }

    public void save(Applicant applicant) throws Exception {
        List<Applicant> applicants = loadAll();
        applicants.add(applicant);
        saveAll(applicants);
    }

    public List<Applicant> loadAll() throws Exception {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        return objectMapper.readValue(file, new TypeReference<List<Applicant>>() {});
    }

    public void saveAll(List<Applicant> applicants) throws Exception {
        File dir = new File(filePath).getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), applicants);
    }

    public void update(Applicant updatedApplicant) throws Exception {
        List<Applicant> applicants = loadAll();
        for (int i = 0; i < applicants.size(); i++) {
            if (applicants.get(i).getTaId().equals(updatedApplicant.getTaId())) {
                applicants.set(i, updatedApplicant);
                saveAll(applicants);
                return;
            }
        }
    }

    public Applicant findByUsername(String username) throws Exception {
        for (Applicant a : loadAll()) {
            if (username.equals(a.getUsername())) return a;
        }
        return null;
    }
}
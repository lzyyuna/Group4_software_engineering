package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.Applicant;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ApplicantCsvRepository {
    private static final String FILE_PATH = "data/applicants.csv";

    public void save(Applicant applicant) throws Exception {
        List<Applicant> applicants = loadAll();
        applicants.add(applicant);
        saveAll(applicants);
    }

    public List<Applicant> loadAll() throws Exception {
        List<Applicant> applicants = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return applicants;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                Applicant applicant = new Applicant();
                applicant.setTaId(line[0]);
                applicant.setStudentId(line[1]);
                applicant.setName(line[2]);
                applicant.setEmail(line[3]);
                applicant.setCourses(line[4]);
                applicant.setSkillTags(line[5]);
                applicant.setContact(line[6]);

                if (line.length > 7) {
                    applicant.setPassword(line[7]);
                }
                if (line.length > 8) {
                    applicant.setUsername(line[8]);
                }
                if (line.length > 9) {
                    applicant.setResumePath(line[9]);
                }

                applicants.add(applicant);
            }
        }
        return applicants;
    }

    public void saveAll(List<Applicant> applicants) throws Exception {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeNext(new String[]{
                    "taId", "studentId", "name", "email",
                    "courses", "skillTags", "contact", "password",
                    "username", "resumePath"
            });

            for (Applicant a : applicants) {
                writer.writeNext(new String[]{
                        a.getTaId(),
                        a.getStudentId(),
                        a.getName(),
                        a.getEmail(),
                        a.getCourses(),
                        a.getSkillTags(),
                        a.getContact(),
                        a.getPassword() == null ? "" : a.getPassword(),
                        a.getUsername() == null ? "" : a.getUsername(),
                        a.getResumePath() == null ? "" : a.getResumePath()
                });
            }
        }
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

    // 新增：根据用户名查找档案（登录绑定用）
    public Applicant findByUsername(String username) throws Exception {
        List<Applicant> applicants = loadAll();
        for (Applicant a : applicants) {
            if (username.equals(a.getUsername())) {
                return a;
            }
        }
        return null;
    }

    public Applicant findByStudentId(String studentId) throws Exception {
        List<Applicant> applicants = loadAll();
        for (Applicant a : applicants) {
            if (studentId.equals(a.getStudentId())) {
                return a;
            }
        }
        return null;
    }

    // 根据TA ID查找申请者
    public Applicant findById(String taId) throws Exception {
        List<Applicant> applicants = loadAll();
        for (Applicant a : applicants) {
            if (taId.equals(a.getTaId())) {
                return a;
            }
        }
        return null;
    }
}
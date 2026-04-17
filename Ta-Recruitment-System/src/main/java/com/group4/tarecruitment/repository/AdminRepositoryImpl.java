package com.group4.tarecruitment.repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.User;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * Implementation of AdminRepository that reads and writes CSV files using OpenCSV.
 */
public class AdminRepositoryImpl implements AdminRepository {

    private static final String DATA_FOLDER = "data";
    private static final String WORKLOAD_CSV = "data/ta_workload.csv";
    private static final String USER_CSV = "data/user.csv";
    private static final String JOB_CSV = "data/jobs.csv";
    private static final String EXPORT_CSV = "data/ta_workload_export.csv";

    @Override
    public List<Admin> loadTaWorkloadData() throws Exception {
        ensureDataFolderExists();
        File file = new File(WORKLOAD_CSV);
        if (!file.exists()) {
            createSampleTaWorkloadFile(file);
        }

        List<Admin> result = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] values;
            boolean firstLine = true;
            while ((values = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (values.length < 5) {
                    continue;
                }

                String courseId = values[0].trim();
                String courseName = values[1].trim();
                String taName = values[2].trim();
                String weeklyWorkloadRaw = values[3].trim();
                String status = values[4].trim();

                double weeklyWorkload = 0;
                try {
                    weeklyWorkload = Double.parseDouble(weeklyWorkloadRaw);
                } catch (NumberFormatException ignored) {
                }

                Admin record = new Admin(
                        courseId,
                        taName,
                        status,
                        "TA",
                        courseName,
                        "未知",
                        weeklyWorkload,
                        weeklyWorkload,
                        ""
                );
                result.add(record);
            }
        }
        return result;
    }

    @Override
    public List<User> loadAllUsers() throws Exception {
        ensureDataFolderExists();
        File file = new File(USER_CSV);
        if (!file.exists()) {
            createSampleUserFile(file);
        }
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] values;
            boolean firstLine = true;
            while ((values = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (values.length < 3) {
                    continue;
                }
                String username = values[0].trim();
                String password = values[1].trim();
                String role = values[2].trim();
                if (!username.isEmpty() && !password.isEmpty() && !role.isEmpty()) {
                    users.add(new User(username, password, role));
                }
            }
        }
        return users;
    }

    @Override
    public List<Job> loadAllJobs() throws Exception {
        ensureDataFolderExists();
        File file = new File(JOB_CSV);
        if (!file.exists()) {
            createSampleJobFile(file);
        }
        List<Job> jobs = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] values;
            boolean firstLine = true;
            while ((values = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (values.length < 7) {
                    continue;
                }
                Job job = new Job();
                job.setJobId(values[0].trim());
                job.setCourseName(values[1].trim());
                job.setPositionType(values[2].trim());
                job.setWeeklyWorkload(parseIntSafe(values[3].trim()));
                job.setMoName(values[4].trim());
                job.setMoEmail(values[5].trim());
                job.setStatus(values[6].trim());
                if (values.length > 7) {
                    job.setReleaseTime(values[7].trim());
                }
                if (values.length > 8) {
                    job.setSkillRequirements(values[8].trim());
                }
                if (values.length > 9) {
                    job.setJobContent(values[9].trim());
                }
                if (values.length > 10) {
                    job.setDeadline(values[10].trim());
                }
                jobs.add(job);
            }
        }
        return jobs;
    }

    @Override
    public void updateJobStatus(String jobId, String status) throws Exception {
        List<Job> jobs = loadAllJobs();
        boolean updated = false;
        for (Job job : jobs) {
            if (job.getJobId() != null && job.getJobId().equals(jobId)) {
                job.setStatus(status);
                updated = true;
            }
        }
        if (updated) {
            saveJobs(jobs);
        }
    }

    @Override
    public void exportTaWorkloadData(String exportFilePath) throws Exception {
        List<Admin> workloadList = loadTaWorkloadData();
        try (CSVWriter writer = new CSVWriter(new FileWriter(exportFilePath))) {
            writer.writeNext(new String[]{"courseId", "courseName", "taName", "weeklyWorkload", "status"});
            for (Admin record : workloadList) {
                writer.writeNext(new String[]{
                        record.getTaId(),
                        record.getCourseName(),
                        record.getTaName(),
                        String.valueOf(record.getWeeklyWorkload()),
                        record.getHireMo()
                });
            }
        }
    }

    private int parseIntSafe(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void saveJobs(List<Job> jobs) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(JOB_CSV))) {
            writer.writeNext(new String[]{
                    "jobId", "courseName", "positionType", "weeklyWorkload",
                    "moName", "moEmail", "status", "releaseTime",
                    "skillRequirements", "jobContent", "deadline"
            });
            for (Job job : jobs) {
                writer.writeNext(new String[]{
                        job.getJobId(),
                        job.getCourseName(),
                        job.getPositionType(),
                        String.valueOf(job.getWeeklyWorkload()),
                        job.getMoName(),
                        job.getMoEmail(),
                        job.getStatus(),
                        job.getReleaseTime(),
                        job.getSkillRequirements(),
                        job.getJobContent(),
                        job.getDeadline()
                });
            }
        }
    }

    private void ensureDataFolderExists() throws Exception {
        Path path = Paths.get(DATA_FOLDER);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void createSampleTaWorkloadFile(File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"courseId", "courseName", "taName", "weeklyWorkload", "status"});
            writer.writeNext(new String[]{"COURSE-001", "Java Programming", "Zhang San", "20", "Active"});
            writer.writeNext(new String[]{"COURSE-002", "Database Systems", "Li Si", "15", "Active"});
            writer.writeNext(new String[]{"COURSE-003", "Web Development", "Wang Wu", "18", "Pending"});
        }
    }

    private void createSampleUserFile(File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"username", "password", "role"});
            writer.writeNext(new String[]{"ta001", "123456", "TA"});
            writer.writeNext(new String[]{"mo001", "123456", "MO"});
            writer.writeNext(new String[]{"admin001", "123456", "Admin"});
        }
    }

    private void createSampleJobFile(File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{
                    "jobId", "courseName", "positionType", "weeklyWorkload",
                    "moName", "moEmail", "status", "releaseTime",
                    "skillRequirements", "jobContent", "deadline"
            });
            writer.writeNext(new String[]{"JOB-0001", "Java Programming", "Module TA", "20", "mo001", "mo001@example.com", "Recruiting", "2026-04-01 10:00:00", "Java基础", "辅助课程", "2026-05-01"});
            writer.writeNext(new String[]{"JOB-0002", "Database Systems", "Module TA", "18", "mo002", "mo002@example.com", "Closed", "2026-04-02 10:00:00", "数据库基础", "辅助课程", "2026-05-15"});
        }
    }
}

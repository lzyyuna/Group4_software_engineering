package com.group4.tarecruitment.service;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Applicant;
import com.group4.tarecruitment.model.Application;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.User;
import com.group4.tarecruitment.repository.AdminRepository;
import com.group4.tarecruitment.repository.AdminRepositoryImpl;
import com.group4.tarecruitment.repository.ApplicationRepository;
import com.group4.tarecruitment.repository.JobRepository;
import com.opencsv.CSVWriter;

/**
 * Implementation of AdminService.
 */
public class AdminServiceImpl implements AdminService {

    private final AdminRepository repository = new AdminRepositoryImpl();
    private final JobRepository jobRepository = new JobRepository();
    private final ApplicationRepository appRepository = new ApplicationRepository();
    private final ApplicantService applicantService = new ApplicantService();

    @Override
    public List<Admin> getTaWorkload() throws Exception {
        List<Admin> result = new ArrayList<>();
        
        List<Application> allApps = appRepository.loadAll();
        List<Job> allJobs = jobRepository.loadAll();
        List<Applicant> allApplicants = applicantService.getAllApplicants();

        // Group total workload by taId first
        Map<String, Double> totalWorkloadMap = new HashMap<>();
        for (Application app : allApps) {
            if ("Approved".equals(app.getStatus())) {
                Job job = allJobs.stream().filter(j -> j.getJobId().equals(app.getJobId())).findFirst().orElse(null);
                if (job != null) {
                    totalWorkloadMap.put(app.getTaId(), totalWorkloadMap.getOrDefault(app.getTaId(), 0.0) + job.getWeeklyWorkload());
                }
            }
        }

        // Create Admin objects
        for (Application app : allApps) {
            if ("Approved".equals(app.getStatus())) {
                Job job = allJobs.stream().filter(j -> j.getJobId().equals(app.getJobId())).findFirst().orElse(null);
                Applicant applicant = allApplicants.stream().filter(a -> a.getTaId().equals(app.getTaId())).findFirst().orElse(null);
                
                if (job != null && applicant != null) {
                    Admin record = new Admin();
                    record.setTaId(applicant.getTaId());
                    record.setTaName(applicant.getName());
                    record.setHireMo(job.getMoName());
                    record.setPositionName(job.getPositionType());
                    record.setCourseName(job.getCourseName());
                    record.setDepartment(job.getDepartment() != null ? job.getDepartment() : "General/Others"); 
                    record.setWeeklyWorkload(job.getWeeklyWorkload());
                    record.setTotalWorkload(totalWorkloadMap.getOrDefault(app.getTaId(), 0.0));
                    record.setHireDate(app.getApplicationTime());
                    record.setExcessAmount(0.0);
                    record.setSuggestion("");
                    
                    result.add(record);
                }
            }
        }
        return result;
    }

    @Override
    public void printTaWorkloadReport() throws Exception {
        List<Admin> workload = getTaWorkload();
        System.out.println("===== TA 工作量报告 =====");
        for (Admin record : workload) {
            System.out.println(record);
        }
        System.out.println("===== 结束 =====");
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        return repository.loadAllUsers();
    }

    @Override
    public List<Job> getAllJobs() throws Exception {
        return jobRepository.loadAll();
    }

    @Override
    public void updateJobStatus(String jobId, String status) throws Exception {
        repository.updateJobStatus(jobId, status);
    }

    @Override
    public String exportTaWorkload() throws Exception {
        String exportPath = "data/ta_workload_export.csv";
        exportTaWorkloadData(getTaWorkload(), exportPath);
        return exportPath;
    }

    public void exportTaWorkloadData(List<Admin> workloadList, String exportFilePath) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(exportFilePath))) {
            writer.writeNext(new String[]{
                "TA ID", "Name", "Hiring MO", "Position Name", 
                "Weekly Workload", "Total Workload", "Hiring Time"
            });
            for (Admin record : workloadList) {
                writer.writeNext(new String[]{
                        record.getTaId(),
                        record.getTaName(),
                        record.getHireMo(),
                        record.getPositionName(),
                        String.valueOf(record.getWeeklyWorkload()),
                        String.valueOf(record.getTotalWorkload()),
                        record.getHireDate()
                });
            }
        }
    }
}

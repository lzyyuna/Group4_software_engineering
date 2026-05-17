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
 *
 * 依赖通过构造器注入，便于单元测试时替换为 Mock 对象。
 */
public class AdminServiceImpl implements AdminService {

    private final AdminRepository repository;
    private final JobRepository jobRepository;
    private final ApplicationRepository appRepository;
    private final ApplicantService applicantService;

    // ── 生产环境使用的无参构造器（自行 new 真实实现） ─────────────────────
    public AdminServiceImpl() {
        this.repository       = new AdminRepositoryImpl();
        this.jobRepository    = new JobRepository();
        this.appRepository    = new ApplicationRepository();
        this.applicantService = new ApplicantService();
    }

    // ── 测试专用构造器（接收外部注入的依赖，Mockito @InjectMocks 会优先使用此构造器） ──
    public AdminServiceImpl(AdminRepository repository,
                            JobRepository jobRepository,
                            ApplicationRepository appRepository,
                            ApplicantService applicantService) {
        this.repository       = repository;
        this.jobRepository    = jobRepository;
        this.appRepository    = appRepository;
        this.applicantService = applicantService;
    }

    // =========================================================================

    @Override
    public List<Admin> getTaWorkload() throws Exception {
        List<Admin> result = new ArrayList<>();

        List<Application> allApps       = appRepository.loadAll();
        List<Job>         allJobs       = jobRepository.loadAll();
        List<Applicant>   allApplicants = applicantService.getAllApplicants();

        // 先统计每个 TA 所有已批准职位的总工作量
        Map<String, Double> totalWorkloadMap = new HashMap<>();
        for (Application app : allApps) {
            if ("Approved".equals(app.getStatus())) {
                Job job = allJobs.stream()
                        .filter(j -> j.getJobId().equals(app.getJobId()))
                        .findFirst().orElse(null);
                if (job != null) {
                    totalWorkloadMap.put(
                            app.getTaId(),
                            totalWorkloadMap.getOrDefault(app.getTaId(), 0.0) + job.getWeeklyWorkload()
                    );
                }
            }
        }

        // 为每条已批准的申请生成 Admin 记录
        for (Application app : allApps) {
            if ("Approved".equals(app.getStatus())) {
                Job job = allJobs.stream()
                        .filter(j -> j.getJobId().equals(app.getJobId()))
                        .findFirst().orElse(null);
                Applicant applicant = allApplicants.stream()
                        .filter(a -> a.getTaId().equals(app.getTaId()))
                        .findFirst().orElse(null);

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
        System.out.println("===== TA Workload Report =====");

        for (Admin record : workload) {
            System.out.println(record);
        }
        System.out.println("===== End =====");
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

    @Override
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
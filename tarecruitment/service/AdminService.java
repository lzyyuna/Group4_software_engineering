package com.group4.tarecruitment.service;

import java.util.List;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.User;

/**
 * Admin service interface for Admin module business logic.
 */
public interface AdminService {

    /**
     * Get TA workload records from backend source.
     * @return list of TA workload records.
     * @throws Exception if load fails.
     */
    List<Admin> getTaWorkload() throws Exception;

    /**
     * Print TA workload report to console.
     * @throws Exception if data load fails.
     */
    void printTaWorkloadReport() throws Exception;

    /**
     * Get all user accounts.
     * @return list of user accounts.
     * @throws Exception if load fails.
     */
    List<User> getAllUsers() throws Exception;

    /**
     * Get all job postings.
     * @return list of jobs.
     * @throws Exception if load fails.
     */
    List<Job> getAllJobs() throws Exception;

    /**
     * Update a job posting status.
     * @param jobId target job id.
     * @param status new job status.
     * @throws Exception if update fails.
     */
    void updateJobStatus(String jobId, String status) throws Exception;

    /**
     * Export TA workload data to a CSV file.
     * @return export file path.
     * @throws Exception if export fails.
     */
    String exportTaWorkload() throws Exception;

    /**
     * Export a specific list of TA workload data to a CSV file.
     * @param workloadList The list of data to export.
     * @param exportFilePath The path to export to.
     * @throws Exception if export fails.
     */
    void exportTaWorkloadData(List<Admin> workloadList, String exportFilePath) throws Exception;
}
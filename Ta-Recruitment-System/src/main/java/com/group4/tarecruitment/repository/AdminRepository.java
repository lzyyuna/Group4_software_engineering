package com.group4.tarecruitment.repository;

import java.util.List;

import com.group4.tarecruitment.model.Admin;
import com.group4.tarecruitment.model.Job;
import com.group4.tarecruitment.model.User;

/**
 * Admin repository interface for TA workload, user management, and job management.
 */
public interface AdminRepository {

    /**
     * Load TA workload data from CSV storage.
     * @return list of TA workload records.
     * @throws Exception when CSV read fails.
     */
    List<Admin> loadTaWorkloadData() throws Exception;

    /**
     * Load all user accounts from CSV storage.
     * @return list of users.
     * @throws Exception when CSV read fails.
     */
    List<User> loadAllUsers() throws Exception;

    /**
     * Load all job postings from CSV storage.
     * @return list of jobs.
     * @throws Exception when CSV read fails.
     */
    List<Job> loadAllJobs() throws Exception;

    /**
     * Update a job posting status and persist the change.
     * @param jobId job identifier.
     * @param status new status value.
     * @throws Exception when CSV write fails.
     */
    void updateJobStatus(String jobId, String status) throws Exception;

    /**
     * Export TA workload data to a separate CSV file.
     * @param exportFilePath destination file path.
     * @throws Exception when CSV write fails.
     */
    void exportTaWorkloadData(String exportFilePath) throws Exception;
}
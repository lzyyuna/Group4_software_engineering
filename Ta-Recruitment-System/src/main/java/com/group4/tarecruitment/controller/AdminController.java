package com.group4.tarecruitment.controller;

import com.group4.tarecruitment.service.AdminService;

public class AdminController {
    private AdminService adminService = new AdminService();

    public static void main(String[] args) {
        AdminController controller = new AdminController();
        controller.showAllWorkload();
        controller.exportWorkload();
        controller.statWorkload();
        controller.checkOverload();
    }

    public void showAllWorkload() {
        adminService.getTaWorkloadOverview();
    }

    public void exportWorkload() {
        adminService.exportWorkloadToCsv();
    }

    public void statWorkload() {
        adminService.statByCourse();
        adminService.statByDepartment();
    }

    public void checkOverload() {
        adminService.setWorkloadThreshold(10);
        adminService.markOverloadTa();
    }
}
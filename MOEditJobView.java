package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.Application;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {
    private static final String FILE_PATH = "data/applications.csv";

    public void save(Application app) throws Exception {
        List<Application> apps = loadAll();
        apps.add(app);
        saveAll(apps);
    }

    public List<Application> loadAll() throws Exception {
        List<Application> apps = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return apps;

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            while ((line = reader.readNext()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.length < 6) continue;
                Application app = new Application(
                        line[0], line[1], line[2], line[3], line[4], line[5]
                );
                apps.add(app);
            }
        }
        return apps;
    }

    public void saveAll(List<Application> apps) throws Exception {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdir();
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeNext(new String[]{
                    "applicationId", "taId", "jobId", "applicationTime", "status", "reviewComment"
            });
            for (Application a : apps) {
                writer.writeNext(new String[]{
                        a.getApplicationId(), a.getTaId(), a.getJobId(),
                        a.getApplicationTime(), a.getStatus(), a.getReviewComment()
                });
            }
        }
    }
}
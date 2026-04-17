package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.Application;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {
    private final String filePath;

    public ApplicationRepository() {
        this.filePath = "data/applications.csv";
    }

    public ApplicationRepository(Path path) {
        this.filePath = path.toString();
    }

    public void save(Application app) throws Exception {
        List<Application> apps = loadAll();
        apps.add(app);
        saveAll(apps);
    }

    public List<Application> loadAll() throws Exception {
        List<Application> apps = new ArrayList<>();
        File file = new File(filePath);          // ÐÞ¸´£ºFILE_PATH ¡ú filePath
        if (!file.exists()) return apps;

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            while ((line = reader.readNext()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.length < 6) continue;
                apps.add(new Application(
                        line[0], line[1], line[2], line[3], line[4], line[5]
                ));
            }
        }
        return apps;
    }

    public void saveAll(List<Application> apps) throws Exception {
        File dir = new File(filePath).getParentFile();   // ÐÞ¸´£ºÓÃ filePath ÍÆµ¼Ä¿Â¼
        if (dir != null && !dir.exists()) dir.mkdirs();
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {  // ÐÞ¸´£ºFILE_PATH ¡ú filePath
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
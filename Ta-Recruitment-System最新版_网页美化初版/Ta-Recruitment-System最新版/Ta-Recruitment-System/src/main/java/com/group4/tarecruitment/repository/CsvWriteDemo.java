package com.group4.tarecruitment.repository;


import com.opencsv.CSVWriter;

import java.io.FileWriter;

public class CsvWriteDemo {
    public static void main(String[] args) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter("data/applicants.csv"));

        String[] header = {"id", "name", "email", "skills"};
        String[] row1 = {"A001", "Alice", "alice@test.com", "Java"};
        String[] row2 = {"A002", "Bob", "bob@test.com", "Python"};

        writer.writeNext(header);
        writer.writeNext(row1);
        writer.writeNext(row2);

        writer.close();

        System.out.println("CSV written.");
    }
}

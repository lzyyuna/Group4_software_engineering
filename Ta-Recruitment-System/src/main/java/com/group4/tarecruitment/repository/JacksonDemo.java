package com.group4.tarecruitment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.tarecruitment.model.Applicant;

import java.io.File;

public class JacksonDemo {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Applicant applicant = new Applicant("A001", "Alice", "alice@test.com", "Java");

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("data/applicant.json"), applicant);

        System.out.println("JSON file saved.");
    }
}
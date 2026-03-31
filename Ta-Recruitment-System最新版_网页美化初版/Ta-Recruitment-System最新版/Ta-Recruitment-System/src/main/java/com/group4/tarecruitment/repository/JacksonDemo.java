package com.group4.tarecruitment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.tarecruitment.model.Applicant;

import java.io.File;

public class JacksonDemo {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 使用你真实存在的 7 参数构造器
        Applicant applicant = new Applicant(
                "TA-001",        // taId
                "20260001",      // studentId
                "Alice",         // name
                "alice@test.com",// email
                "Java Course",   // courses
                "Java,English",  // skillTags
                "13800138000"    // contact
        );

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("data/applicant.json"), applicant);

        System.out.println("JSON file saved successfully!");
    }
}
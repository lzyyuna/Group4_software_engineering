package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final List<User> users = new ArrayList<>();

    private static final Path CSV_PATH = Paths.get("data", "user.csv");
    private static final Path JSON_PATH = Paths.get("data", "user.json");

    public UserRepository() {
        loadUsers();
    }

    private void loadUsers() {
        users.clear();

        if (Files.exists(CSV_PATH)) {
            loadFromCsv();
        } else {

            users.add(new User("ta001", "123456", "TA"));
            users.add(new User("mo001", "123456", "MO"));
            users.add(new User("admin001", "123456", "Admin"));
            saveUsers();
        }
    }

    public User findByUsernameAndRole(String username, String role) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getRole().equals(role)) {
                return user;
            }
        }
        return null;
    }

    public boolean register(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            return false;
        }

        username = username.trim();
        password = password.trim();
        role = role.trim();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            return false;
        }

        // �����Ұ����û���ȫ��Ψһ������
        if (existsByUsername(username)) {
            return false;
        }

        users.add(new User(username, password, role));
        saveUsers();
        return true;
    }

    public boolean existsByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    private void saveUsers() {
        saveToCsv();
        saveToJson();
    }

    private void loadFromCsv() {
        try {
            List<String> lines = Files.readAllLines(CSV_PATH);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                // ��������
                if (line.isEmpty()) {
                    continue;
                }

                // ������ͷ
                if (i == 0 && line.equalsIgnoreCase("username,password,role")) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim();

                    if (!username.isEmpty() && !password.isEmpty() && !role.isEmpty()) {
                        users.add(new User(username, password, role));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from user.csv", e);
        }
    }

    private void saveToCsv() {
        try (BufferedWriter writer = Files.newBufferedWriter(CSV_PATH)) {
            writer.write("username,password,role");
            writer.newLine();

            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to user.csv", e);
        }
    }

    private void saveToJson() {
        try (BufferedWriter writer = Files.newBufferedWriter(JSON_PATH)) {
            writer.write("[");
            writer.newLine();

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);

                writer.write("  {");
                writer.newLine();
                writer.write("    \"username\": \"" + escapeJson(user.getUsername()) + "\",");
                writer.newLine();
                writer.write("    \"password\": \"" + escapeJson(user.getPassword()) + "\",");
                writer.newLine();
                writer.write("    \"role\": \"" + escapeJson(user.getRole()) + "\"");
                writer.newLine();
                writer.write("  }");

                if (i < users.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }

            writer.write("]");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to user.json", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
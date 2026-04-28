package com.group4.tarecruitment.repository;

import com.group4.tarecruitment.model.InviteCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InviteCodeRepository {

    private static final Path CSV_PATH = Paths.get("data", "invite_codes.csv");
    private final List<InviteCode> codes = new ArrayList<>();

    public InviteCodeRepository() {
        load();
    }

    private void load() {
        codes.clear();
        if (!Files.exists(CSV_PATH)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(CSV_PATH);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                if (i == 0 && line.equalsIgnoreCase("code,role,used,createdAt")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length == 4) {
                    codes.add(new InviteCode(
                            parts[0].trim(),
                            parts[1].trim(),
                            Boolean.parseBoolean(parts[2].trim()),
                            parts[3].trim()
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load invite_codes.csv", e);
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CSV_PATH)) {
            writer.write("code,role,used,createdAt");
            writer.newLine();
            for (InviteCode c : codes) {
                writer.write(c.getCode() + "," + c.getRole() + "," + c.isUsed() + "," + c.getCreatedAt());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save invite_codes.csv", e);
        }
    }

    /** Returns all codes (for admin view). */
    public List<InviteCode> findAll() {
        return new ArrayList<>(codes);
    }

    /** Add a new invite code and persist. */
    public void add(InviteCode code) {
        codes.add(code);
        save();
    }

    /**
     * Validate and consume a code atomically.
     * Returns true only if the code exists, matches the given role, and has not been used.
     */
    public boolean validateAndConsume(String code, String role) {
        for (InviteCode ic : codes) {
            if (ic.getCode().equals(code) && ic.getRole().equals(role) && !ic.isUsed()) {
                ic.setUsed(true);
                save();
                return true;
            }
        }
        return false;
    }
}

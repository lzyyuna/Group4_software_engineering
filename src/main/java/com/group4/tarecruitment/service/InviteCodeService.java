package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.InviteCode;
import com.group4.tarecruitment.repository.InviteCodeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class InviteCodeService {

    private final InviteCodeRepository repository;

    public InviteCodeService() {
        this.repository = new InviteCodeRepository();
    }

    public InviteCodeService(InviteCodeRepository repository) {
        this.repository = repository;
    }

    /**
     * Generate a new one-time invite code for the given role (TA or MO only).
     * Throws if caller tries to generate an Admin code.
     */
    public String generateCode(String role) {
        if (!"TA".equals(role) && !"MO".equals(role)) {
            throw new IllegalArgumentException("Invite codes can only be generated for TA or MO roles.");
        }
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        repository.add(new InviteCode(code, role, false, LocalDate.now().toString()));
        return code;
    }

    /**
     * Validate and consume a code. Returns true if valid and not yet used.
     */
    public boolean validateAndConsume(String code, String role) {
        if (code == null || code.isBlank()) return false;
        return repository.validateAndConsume(code.trim().toUpperCase(), role);
    }

    /** Return all codes for admin display. */
    public List<InviteCode> getAllCodes() {
        return repository.findAll();
    }
}

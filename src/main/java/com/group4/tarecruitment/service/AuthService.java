package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.User;
import com.group4.tarecruitment.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();
    private final InviteCodeService inviteCodeService = new InviteCodeService();

    public boolean login(String username, String password, String role) {
        User user = userRepository.findByUsernameAndRole(username, role);
        return user != null && user.getPassword().equals(password);
    }

    /**
     * Register a new TA or MO account using a valid invite code.
     * Admin accounts cannot be registered through this method.
     */
    public boolean register(String username, String password, String role, String inviteCode) {
        if (!"TA".equals(role) && !"MO".equals(role)) {
            return false;
        }
        if (!inviteCodeService.validateAndConsume(inviteCode, role)) {
            return false;
        }
        return userRepository.register(username, password, role);
    }
}

package com.group4.tarecruitment.service;

import com.group4.tarecruitment.model.User;
import com.group4.tarecruitment.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    public boolean login(String username, String password, String role) {
        User user = userRepository.findByUsernameAndRole(username, role);
        return user != null && user.getPassword().equals(password);
    }

    public boolean register(String username, String password, String role) {
        return userRepository.register(username, password, role);
    }
}
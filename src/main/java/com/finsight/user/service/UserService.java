package com.finsight.user.service;

import com.finsight.user.dto.AuthResponse;
import com.finsight.user.dto.LoginRequest;
import com.finsight.user.dto.SignUpRequest;
import com.finsight.user.model.User;

import java.util.Optional;

public interface UserService {
    AuthResponse authenticateUser(LoginRequest loginRequest);
    User registerUser(SignUpRequest signUpRequest);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User updateUserProfile(Long userId, User updatedUser);
    void updatePremiumStatus(Long userId, boolean isPremium);
    void deleteUser(Long userId);
} 
package com.finsight.user.controller;

import com.finsight.user.dto.AuthResponse;
import com.finsight.user.dto.LoginRequest;
import com.finsight.user.dto.SignUpRequest;
import com.finsight.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            userService.registerUser(signUpRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }
}

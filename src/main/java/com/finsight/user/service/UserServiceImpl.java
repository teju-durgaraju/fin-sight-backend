package com.finsight.user.service;

import com.finsight.security.JwtUtils;
import com.finsight.security.UserDetailsImpl;
import com.finsight.user.dto.AuthResponse;
import com.finsight.user.dto.LoginRequest;
import com.finsight.user.dto.SignUpRequest;
import com.finsight.user.model.User;
import com.finsight.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        return new AuthResponse(
            jwt,
            userDetails.getId(),
            userDetails.getName(),
            userDetails.getEmail(),
            userDetails.isPremium()
        );
    }

    @Override
    @Transactional
    public User registerUser(SignUpRequest signUpRequest) {
        if (existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId, User updatedUser) {
        return userRepository.findById(userId)
            .map(user -> {
                user.setName(updatedUser.getName());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
                return userRepository.save(user);
            })
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional
    public void updatePremiumStatus(Long userId, boolean isPremium) {
        userRepository.findById(userId)
            .map(user -> {
                user.setPremium(isPremium);
                return userRepository.save(user);
            })
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
} 
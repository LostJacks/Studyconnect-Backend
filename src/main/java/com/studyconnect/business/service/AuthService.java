package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponse register(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getName() == null || request.getName().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getRole() == null) {
            throw new IllegalArgumentException("All fields must be filled in");
        }

        String password = request.getPassword();

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters, contain 1 uppercase letter and 1 number"
            );
        }

        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User userToSave = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword,
                request.getRole()
        );

        User savedUser = userRepository.save(userToSave);

        return new AuthResponse(
                savedUser.getEmail(),
                savedUser.getRole(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("All fields must be filled in");
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new AuthResponse(
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }
}
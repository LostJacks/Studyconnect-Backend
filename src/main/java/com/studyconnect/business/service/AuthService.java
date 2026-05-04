package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(IUserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
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

        userRepository.save(userToSave);

        UserEntity savedUserEntity = userRepository.findEntityByEmail(request.getEmail());

        return new AuthResponse(
                savedUserEntity.getId(),
                null,
                savedUserEntity.getName(),
                savedUserEntity.getEmail(),
                Role.valueOf(savedUserEntity.getRole()),
                savedUserEntity.getProfileImageUrl()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository.findEntityByEmail(request.getEmail());
        if (userEntity == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                userEntity.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                userEntity.getEmail(),
                userEntity.getRole()
        );

        return new AuthResponse(
                userEntity.getId(),
                token,
                userEntity.getName(),
                userEntity.getEmail(),
                Role.valueOf(userEntity.getRole()),
                userEntity.getProfileImageUrl()
        );
    }
}
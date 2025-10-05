package com.wom.login.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wom.login.dto.ApiResponse;
import com.wom.login.dto.AuthResponse;
import com.wom.login.dto.RegisterRequest;
import com.wom.login.models.UserModel;
import com.wom.login.repositories.UserRepository;
import com.wom.login.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Value("${security.max-failed-attempts}")
    private int maxFailed;

    @Value("${security.lock-duration-ms}")
    private long lockDurationMs;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void incrementFailedAttempts(UserModel user) {
        int attempts = user.getFailed_attempts() + 1;
        user.setFailed_attempts(attempts);
        if (attempts >= maxFailed) {
            user.setLocked_until(Instant.now().plusMillis(lockDurationMs));
            user.setFailed_attempts(0); // reset after lock
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(UserModel user) {
        user.setFailed_attempts(0);
        user.setLocked_until(null);
        userRepository.save(user);
    }

    public UserModel registerUser(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "El nombre de usuario ya está en uso", null));
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "El correo ya está registrado", null));
        }

        UserModel user = new UserModel();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreated_at(Instant.now());
        user.setUpdated_at(Instant.now());

        // Guardar usuario en DB
        UserModel savedUser = userRepository.save(user);

        // Generar JWT
        String token = jwtTokenProvider.generateToken(savedUser.getUsername());

        AuthResponse authData = new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                token,
                null);

        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario creado exitosamente", authData));
    }
}

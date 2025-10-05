package com.wom.login.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wom.login.dto.LoginRequest;
import com.wom.login.dto.RegisterRequest;
import com.wom.login.models.UserModel;
import com.wom.login.repositories.UserRepository;
import com.wom.login.services.AuthService;
import com.wom.login.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // Retornar errores de validación
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        return userService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return authService.login(username, password, ip);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String refresh = body.get("refreshToken");
        String ip = request.getRemoteAddr();
        return authService.refresh(refresh, ip);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String refresh = body.get("refreshToken");
        String ip = request.getRemoteAddr();
        authService.logout(refresh, ip);
        return ResponseEntity.ok(Map.of("message", "logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(401).build();
        String username = authentication.getName();
        UserModel u = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(u);
    }
}

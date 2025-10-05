package com.wom.login.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wom.login.dto.ApiResponse;
import com.wom.login.dto.AuthResponse;
import com.wom.login.models.RefreshToken;
import com.wom.login.models.UserModel;
import com.wom.login.repositories.RefreshTokenRepository;
import com.wom.login.repositories.UserRepository;
import com.wom.login.security.JwtTokenProvider;
import com.wom.login.security.RefreshTokenPair;
import com.wom.login.util.TokenUtils;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuditService auditService;
    @Autowired
    private UserService userService;

    public ResponseEntity<?> login(String identifier, String password, String ip) {
        Optional<UserModel> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty())
            userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            auditService.log(null, "LOGIN_FAILED", "Usuario o correo no encontrado", ip);
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Usuario o correo no encontrado", null));
        }

        UserModel user = userOpt.get();

        // check locked
        if (user.getLocked_until() != null && user.getLocked_until().isAfter(Instant.now())) {
            auditService.log(user.getId(), "LOGIN_BLOCKED", "Cuenta bloqueada. Intenta más tarde.", ip);
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(false, "Cuenta bloqueada. Intenta más tarde.", null));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            // increment failed attempts
            userService.incrementFailedAttempts(user);
            auditService.log(user.getId(), "LOGIN_FAILED", "Contraseña incorrecta", ip);
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Contraseña incorrecta", null));
        }

        // reset failed attempts
        userService.resetFailedAttempts(user);

        // Generate access token
        String token = jwtProvider.generateAccessToken(user.getUsername(), List.of("USER"));

        // Generate refresh token and persist
        RefreshTokenPair pair = jwtProvider.generateRefreshToken(user.getUsername());
        RefreshToken refresh = new RefreshToken();
        refresh.setJti(pair.getJti());
        refresh.setUser(user);
        refresh.setToken_hash(TokenUtils.hash(pair.getToken()));
        refresh.setCreated_at(Instant.now());
        refresh.setLast_used_at(Instant.now());
        refresh.setExpires_at(pair.getExpiresAt());
        refresh.setRevoked(false);
        refreshTokenRepository.save(refresh);

        auditService.log(user.getId(), "LOGIN_SUCCESS", "login ok", ip);

        AuthResponse authData = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token,
                pair.getToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", authData));
    }

    public ResponseEntity<?> refresh(String refreshTokenStr, String ip) {
        if (!jwtProvider.validate(refreshTokenStr)) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Refresh token inválido.", null));
        }
        String jti = jwtProvider.getJtiFromToken(refreshTokenStr);
        String username = jwtProvider.getUsernameFromToken(refreshTokenStr);

        Optional<RefreshToken> rtOpt = refreshTokenRepository.findByJti(jti);
        if (rtOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Refresh token no encontrado", null));
        }
        RefreshToken rt = rtOpt.get();

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            auditService.log(rt.getUser().getId(), "REFRESH_REJECTED", "revocado o expiro", ip);
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Refresh token fue revocado o expiro", null));
        }

        // validate token content (prevent theft)
        if (!TokenUtils.hash(refreshTokenStr).equals(rt.getToken_hash())) {
            // possible token theft: revoke this token and all others for user
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            refreshTokenRepository.findByUser(rt.getUser()).forEach(t -> {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            });
            auditService.log(rt.getUser().getId(), "REFRESH_THEFT_DETECTED", "Token no coincide.", ip);
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Token no coincide.", null));
        }

        // rotate: revoke old and create new
        rt.setRevoked(true);
        RefreshTokenPair newPair = jwtProvider.generateRefreshToken(username);
        rt.setReplaced_by_jti(newPair.getJti());
        refreshTokenRepository.save(rt);

        RefreshToken newRt = new RefreshToken();
        newRt.setJti(newPair.getJti());
        newRt.setUser(rt.getUser());
        newRt.setToken_hash(TokenUtils.hash(newPair.getToken()));
        newRt.setCreated_at(Instant.now());
        newRt.setLast_used_at(Instant.now());
        newRt.setExpires_at(newPair.getExpiresAt());
        newRt.setRevoked(false);
        refreshTokenRepository.save(newRt);

        // new access token
        String newAccess = jwtProvider.generateAccessToken(username, List.of("USER"));

        auditService.log(rt.getUser().getId(), "REFRESH_SUCCESS", "rotated", ip);

        return ResponseEntity.ok(new AuthResponse(rt.getUser().getId(), username, rt.getUser().getEmail(), newAccess,
                newPair.getToken()));
    }

    public void logout(String refreshTokenStr, String ip) {
        if (!jwtProvider.validate(refreshTokenStr))
            return;
        String jti = jwtProvider.getJtiFromToken(refreshTokenStr);
        refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            auditService.log(rt.getUser().getId(), "LOGOUT", "user logout", ip);
        });
    }
}

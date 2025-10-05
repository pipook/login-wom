package com.wom.login.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

    @Value("${jwt.private-key}")
    private String privateKeyStr;

    @Value("${jwt.public-key}")
    private String publicKeyStr;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.access.expiration-ms}")
    private long accessExp;

    @Value("${jwt.refresh.expiration-ms}")
    private long refreshExp;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private PrivateKey getPrivateKey() {
        try {
            String key = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (java.security.NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException
                | IllegalArgumentException e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            String key = publicKeyStr
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (java.security.NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException
                | IllegalArgumentException e) {
            throw new RuntimeException("Error loading public key", e);
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateAccessToken(String username, Collection<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExp);
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public RefreshTokenPair generateRefreshToken(String username) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExp);

        String token = Jwts.builder()
                .setSubject(username)
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        return new RefreshTokenPair(jti, token, exp.toInstant());
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token);
    }

    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String getJtiFromToken(String token) {
        return parseToken(token).getBody().getId();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token); // implement this method to extract username from token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // public String getUsernameFromToken(String token) {
    // Claims claims = Jwts.parserBuilder()
    // .setSigningKey(getPublicKey())
    // .build()
    // .parseClaimsJws(token)
    // .getBody();
    // return claims.getSubject();
    // }
}
package com.wom.login.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wom.login.models.RefreshToken;
import com.wom.login.models.UserModel;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(String jti);

    List<RefreshToken> findByUser(UserModel user);

    void deleteByExpiresAtBefore(Instant cutoff);

}

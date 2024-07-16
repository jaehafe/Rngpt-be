package org.jh.oauthjwt.refreshToken;

import jakarta.transaction.Transactional;
import org.jh.oauthjwt.entity.RefreshEntity;
import org.jh.oauthjwt.jwt.JWTUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JWTUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String createNewAccessToken(String refreshToken) {
        RefreshEntity refreshEntity = refreshTokenRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (jwtUtil.isExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshEntity);
            throw new RuntimeException("Refresh token is expired");
        }

        String username = refreshEntity.getUsername();
        String email = refreshEntity.getEmail();
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.createJwt("access", username, email, role, 600000L);
    }

    @Transactional
    public void saveRefreshToken(String username, String email, String refreshToken, Long expirationTime) {
        RefreshEntity refreshEntity = refreshTokenRepository.findByUsername(username)
                .orElse(new RefreshEntity());

        refreshEntity.setUsername(username);
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refreshToken);
        refreshEntity.setExpiration(new Date(Instant.now().toEpochMilli() + expirationTime).toString());

        refreshTokenRepository.save(refreshEntity);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefresh(refreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefresh(refreshToken).isPresent() && !jwtUtil.isExpired(refreshToken);
    }
}

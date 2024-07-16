package org.jh.oauthjwt.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jh.oauthjwt.entity.RefreshEntity;
import org.jh.oauthjwt.jwt.JWTUtil;
import org.jh.oauthjwt.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenRefreshController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public TokenRefreshController(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        // Refresh 토큰 유효성 검사
        if (jwtUtil.validateToken(refreshToken)) {
            // RefreshRepository에서 토큰 확인
            Optional<RefreshEntity> refreshEntity = refreshRepository.findByRefresh(refreshToken);
            if (refreshEntity.isPresent()) {
                String username = refreshEntity.get().getUsername();
                String email = refreshEntity.get().getEmail();
                String role = jwtUtil.getRole(refreshToken);

                // 새 Access 토큰 생성
                String newAccessToken = jwtUtil.createJwt("access", username, email, role, 10000L);

                // 응답 생성
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}

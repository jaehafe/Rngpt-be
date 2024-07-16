package org.jh.oauthjwt.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jh.oauthjwt.refreshToken.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

    private final RefreshTokenService refreshTokenService;

    public LogoutController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("logout request >>" + request);
        String refreshToken = extractRefreshTokenFromCookie(request);
        System.out.println("refreshToken +" + refreshToken);

        if (refreshToken != null) {
            refreshTokenService.deleteRefreshToken(refreshToken);

            // RefreshToken 쿠키 삭제
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
            response.addCookie(cookie);

            return ResponseEntity.ok().body("Logged out successfully");
        }

        return ResponseEntity.badRequest().body("Refresh token not found");
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

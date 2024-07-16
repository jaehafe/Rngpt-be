package org.jh.oauthjwt.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jh.oauthjwt.entity.RefreshEntity;
import org.jh.oauthjwt.jwt.JWTUtil;
import org.jh.oauthjwt.repository.RefreshRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

//        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//
//        String username = oauthUser.getName();
//        String email = oauthUser.getEmail();
//        String role = oauthUser.getAuthorities().iterator().next().getAuthority();
//
//        long accessTokenValidityInMilliseconds = 600000L; // 10분
//        long refreshTokenValidityInMilliseconds = 8640000L; // 24시간
//
//        String access = jwtUtil.createJwt("access", username, email, role, accessTokenValidityInMilliseconds);
//        String refresh = jwtUtil.createJwt("refresh", username, email, role, refreshTokenValidityInMilliseconds);
//
//        saveRefreshToken(email, refresh, refreshTokenValidityInMilliseconds);
//
//        // Refresh 토큰 저장 (LoginFilter의 addRefreshEntity 메소드와 유사)
//        // refreshRepository.save(...);
//        System.out.println("access + refresh = " + access + " " + refresh);
//
//        response.setHeader("access", access);
//        response.addCookie(createCookie("refresh", refresh));
//        // refresh 토큰을 쿠키에 저장하는 로직 추가
//
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().write("OAuth2 login success");
        String username;
        String email;
        String role;

        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getName();
            email = oauthUser.getEmail();
            role = oauthUser.getAuthorities().iterator().next().getAuthority();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
            email = username;
            role = userDetails.getAuthorities().iterator().next().getAuthority();
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }

//        long accessTokenValidityInMilliseconds = 600000L; // 10분
//        long refreshTokenValidityInMilliseconds = 8640000L; // 2시간 20분
        long accessTokenValidityInMilliseconds = 10000L; // 10초
        long refreshTokenValidityInMilliseconds = 60000L; // 1분


        String access = jwtUtil.createJwt("access", username, email, role, accessTokenValidityInMilliseconds);
        String refresh = jwtUtil.createJwt("refresh", username, email, role, refreshTokenValidityInMilliseconds);

        saveRefreshToken(email, refresh, refreshTokenValidityInMilliseconds);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"message\":\"Login success\",\"accessToken\":\"%s\"}", access);
        response.getWriter().write(jsonResponse);

        response.addCookie(createCookie("refresh", refresh));

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void saveRefreshToken(String email, String refresh, long validityInMilliseconds) {
        Date expirationDate = new Date(System.currentTimeMillis() + validityInMilliseconds);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(expirationDate.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 24시간
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // HTTPS를 사용하는 경우 활성화
        return cookie;
    }
}

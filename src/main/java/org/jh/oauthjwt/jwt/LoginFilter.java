package org.jh.oauthjwt.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.jh.oauthjwt.dto.CustomUserDetails;
import org.jh.oauthjwt.dto.LoginDTO;
import org.jh.oauthjwt.entity.RefreshEntity;
import org.jh.oauthjwt.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
            ObjectMapper objectMapper, RefreshRepository refreshRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        LoginDTO loginDTO = new LoginDTO();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDTO = objectMapper.readValue(messageBody, LoginDTO.class);

            System.out.println("loginDTO>>>>" + " " + loginDTO);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        String username = loginDTO.getUsername();
        String username = loginDTO.getEmail();
        System.out.println("username>>>>" + " " + username);
//        System.out.println("username1>>>>" + " " + username1);
        String password = loginDTO.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공 시 실행하는 메서드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        // 유저 정보
//        String username = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String email = userDetails.getEmail();


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

//        long accessTokenValidityInMilliseconds = 10000L; // 10초
//        long refreshTokenValidityInMilliseconds = 60000L; // 1분
        long accessTokenValidityInMilliseconds = 600000L; // 10분
        long refreshTokenValidityInMilliseconds = 8640000L; // 2시간 20분

        // 토큰
        String access = jwtUtil.createJwt("access", username, email, role, accessTokenValidityInMilliseconds);
        String refresh = jwtUtil.createJwt("refresh", username, email, role, refreshTokenValidityInMilliseconds);

        // Refresh 토큰 저장
        addRefreshEntity(username, email, refresh,refreshTokenValidityInMilliseconds);

        // refreshToken을 쿠키에 설정
        Cookie refreshCookie = createCookie("refreshToken", refresh);
        response.addCookie(refreshCookie);

        // accessToken을 응답 본문에 포함
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", access);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        response.setStatus(HttpStatus.OK.value());

//        // 응답  설정
//        response.setHeader("access", access);
//        response.addCookie(createCookie("refresh", refresh));
//        response.setStatus(HttpStatus.OK.value());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("accessToken", access);
//        responseBody.put("refreshToken", refresh);
//
//        // 응답 설정
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
//        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(String username, String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    // 로그인 실패 시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);  // HTTPS에서만 사용하도록 설정 (프로덕션 환경에서 활성화)
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

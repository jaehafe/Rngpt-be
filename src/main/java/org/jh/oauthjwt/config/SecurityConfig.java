package org.jh.oauthjwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.jh.oauthjwt.jwt.JWTFilter;
import org.jh.oauthjwt.jwt.JWTUtil;
import org.jh.oauthjwt.jwt.LoginFilter;
import org.jh.oauthjwt.oauth.CustomOAuth2UserService;
import org.jh.oauthjwt.oauth.CustomSuccessHandler;
import org.jh.oauthjwt.refreshToken.RefreshTokenService;
import org.jh.oauthjwt.repository.RefreshRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    // AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객제 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;
    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            JWTUtil jwtUtil,
            ObjectMapper objectMapper,
            RefreshRepository refreshRepository,
            CustomOAuth2UserService customOAuth2UserService,
            CustomSuccessHandler customSuccessHandler,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshRepository = refreshRepository;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.refreshTokenService = refreshTokenService;
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // 암호화 진행할 때 진행 && Bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
            }
        }));

        // csrf disable
        http.csrf((auth) -> auth.disable());

        // Form 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 basic
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) ->
                auth
                        .requestMatchers(
                                "/login",
                                "/",
                                "/join",
                                "/verify",
                                "/oauth2/**",
                                "/send-notification",
                                "/fcm-token",
                                "/logout",
                                "/refresh",
                                "/test-controller",
                                "/api/todo"
                        )
                        .permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/reissue")
                        .permitAll()
                        .anyRequest()
                        .authenticated()

        );

        // Oauth
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                )
                .successHandler(customSuccessHandler)
        );

        http.addFilterBefore(new JWTFilter(jwtUtil, refreshTokenService), LoginFilter.class);

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper, refreshRepository), UsernamePasswordAuthenticationFilter.class);

//        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 세션 설정
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

package com.fifo.compasstep.security.config;

import com.fifo.compasstep.security.filter.JwtAuthenticationFilter;
import com.fifo.compasstep.security.jwt.JwtAuthenticationEntryPoint;
import com.fifo.compasstep.security.userDetails.AdminUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminUserDetailsService adminUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDenyHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 안 된 접근 -> 401
                        .accessDeniedHandler(customAccessDeniedHandler)     // 인증 됐지만 권한 부족 -> 403 <-- 추가!
                )
                
                .authorizeHttpRequests(auth -> auth
                        //로그인 밑 회원가입 경로 전부 허용
                        .requestMatchers("/api/admin/login", "/api/admin/logout", "/auth/oauth/**", "/api/admin/refresh",
                                "/api/user/auth/login", "/api/user/auth/logout").permitAll()
                        //Swagger API 허용
                        .requestMatchers(
                                "/swagger-ui.html",    // 스웨거 UI 페이지
                                "/swagger-ui/**",      // 스웨거 UI 리소스 (css, js 등)
                                "/v3/api-docs/**",     // OpenAPI 3.0 스펙 JSON
                                "/api-docs/**",        // (하위 버전 호환용)
                                "/swagger-resources/**" // 스웨거 리소스
                        ).permitAll()
                        // 게스트 공개 (게시글 상세, 댓글 작성, CSRF 발급)
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/csrf-token").permitAll()
                        //
                        .requestMatchers("/api/admin/**").hasAnyRole("GENERAL", "ROOT")
                        .requestMatchers("/api/user/**").hasAnyAuthority("STATUS_NORMAL", "STATUS_SUSPENDED")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(adminAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

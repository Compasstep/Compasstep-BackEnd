package com.fifo.compasstep.security.config;

import com.fifo.compasstep.security.filter.JwtAuthenticationFilter;
import com.fifo.compasstep.security.jwt.JwtAuthenticationEntryPoint;
import com.fifo.compasstep.security.userDetails.AdminUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminUserDetailsService adminUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDenyHandler customAccessDeniedHandler;

    @Value("${cors.allowed-origins:}")
    private String allowedOriginsProp;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 필터 활성화
                .cors(Customizer.withDefaults())
                // CSRF (JWT 기반이므로 보통 비활성)
                .csrf(csrf -> csrf.disable())
                // 세션 미사용
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 안됨 → 401
                        .accessDeniedHandler(customAccessDeniedHandler)        // 권한 부족 → 403
                )
                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // Preflight(OPTIONS) 전부 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 로그인/로그아웃/리프레시/소셜 콜백 등 공개
                        .requestMatchers(
                                "/api/admin/login", "/api/admin/logout", "/auth/oauth/**", "/api/admin/refresh",
                                "/api/user/auth/login", "/api/user/auth/logout"
                        ).permitAll()

                        // Swagger 공개
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // 프로메테우스 공개
                        .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()

                        // 게스트 공개 (예시)
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/csrf-token").permitAll()

                        // 권한 필요한 영역
                        .requestMatchers("/api/admin/**").hasAnyRole("GENERAL", "ROOT")
                        .requestMatchers("/api/user/**").hasAnyAuthority("STATUS_NORMAL", "STATUS_SUSPENDED")

                        .anyRequest().authenticated()
                )
                // 인증 프로바이더/필터
                .authenticationProvider(adminAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** CORS 설정 소스 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 콤마/리스트 바인딩 모두 대응 (리스트면 split 결과가 1개이지만 그대로 동작)
        List<String> origins = Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // credentials(쿠키/Authorization) 사용할 경우 와일드카드(*) 금지 → 정확히 나열
        cfg.setAllowedOrigins(origins);

        // 포트를 자주 바꾸고 쿠키를 안쓴다면 패턴도 가능:
        // cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        cfg.setExposedHeaders(List.of("Location")); // 필요 시만 노출
        cfg.setAllowCredentials(true);              // 프론트가 withCredentials/credentials: 'include'면 필수
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

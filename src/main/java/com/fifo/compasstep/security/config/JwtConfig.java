package com.fifo.compasstep.security.config;

import com.fifo.compasstep.security.jwt.JwtProperties;
import com.fifo.compasstep.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    // Spring이 application.yml 값을 읽어 자동으로 생성하고 주입해준 Bean
    private final JwtProperties jwtProperties;

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        // 값이 모두 채워진 jwtProperties를 사용하여 TokenProvider 생성
        return new JwtTokenProvider(jwtProperties);
    }
}

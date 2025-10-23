package com.fifo.compasstep.common.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.awt.SystemColor.info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("My API Project")
                .version("1.0.0")
                .description("API 명세서");
        // 1. 보안 스키마 정의 (API Key 방식)
        String schemeName = "CSRF";
        SecurityScheme securityScheme = new SecurityScheme()
                .name("X-CSRF-TOKEN") // 실제 헤더 이름
                .type(SecurityScheme.Type.APIKEY) // 타입: APIKEY
                .in(SecurityScheme.In.HEADER); // 위치: 헤더

// 2. 보안 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(schemeName); // 위에서 정의한 "CSRF"

// 3. OpenAPI 객체에 반영
        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes(schemeName, securityScheme))
                .addSecurityItem(securityRequirement);
    }
}

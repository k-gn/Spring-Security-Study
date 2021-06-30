package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    // CORS 정책으로 부터 자유롭도록 설정 (Cross Origin 허용)
    // 인증이 필요한 요청들은 어노테이션으로 해결이 안되서 필터를 사용해서 등록해준다.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답 시 json을 자바스크립트에서 처리할 수 있게 할지 설정
        config.addAllowedOrigin("*"); // 모든 ip 응답 허용
        config.addAllowedHeader("*"); // 모든 헤더에 응답 허용
        config.addAllowedMethod("*"); // 모든 http 요청을 허용
        source.registerCorsConfiguration("/api/**", config); // 해당 요청이 들어올 때 설정 적용
        return new CorsFilter(source); // 필터 생성
    }
}

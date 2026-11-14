package com.igemons.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 서비스의 현재 시각 기준 (테스트에서 고정 시각으로 교체 가능) */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

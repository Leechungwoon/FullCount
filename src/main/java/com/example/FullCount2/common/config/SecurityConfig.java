package com.example.FullCount2.common.config;

import org.springframework.http.HttpMethod;
import com.example.FullCount2.common.filter.JwtFilter;
import com.example.FullCount2.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화: JWT 방식은 쿠키/세션을 안쓰므로 CSRF 공격 위험이 없어 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.securityContext(securityContext -> securityContext.requireExplicitSave(false));

        //경로별 접근 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/games/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/games").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/auth/logout").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated());

        //JWFilter를 Security 필터 체인에 등록

        http.addFilterBefore(
                new JwtFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class //필터 앞에 JwtFilter 삽입
        );
        return http.build(); //위 설정들을 적용한 SecurityFilterChain 반환
    }
}

package com.cos.jwt.config;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final CorsFilter corsFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 커스텀 필트는 그냥 addFilter로 시큐리티에서 등록할 수 없고 addFilterBefore나 addFilterAfter 를 사용해야 에러가 안난다.
                .addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class)// 시큐리티 필터가 우선으로 실행
                // jwt 사용을 위한 시큐리티 설정
                .addFilter(corsFilter) // @CrossOrigin(인증 X), 시큐리티 필터에 등록(인증 O) - 모든 요청 허용
                .csrf().disable() // CSRF 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다.
                .and()
                .formLogin().disable() // formlogin 비활성화 (안쓸거라)
                .httpBasic().disable() // 기본설정 사용안함
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // Authentication Manager가 필요
                // 외부에서 자바스크립트로 요청 및 쿠키 전송을 하려고 할 때 기본적으로 서버에서 요청을 거부한다.
                // 그리고 쿠키 방식을 쓰면 서버가 많아질 수록 관리하기가 힘들다.
                // 그래서 basic 방식으로 헤더에 authorization에 인증정보를 같이 보내는데 암호화가 안되있어서 노출될 위험성이 있다.
                // -> authorization에 토큰을 넣어서 인증 -> Bearer 방식 사용 -> jwt 토큰 사용
                
                .authorizeRequests()
                    .antMatchers("/api/v1/user/**")
                    .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                    .antMatchers("/api/v1/manager/**")
                    .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                    .antMatchers("/api/v1/admin/**")
                    .access("hasRole('ROLE_ADMIN')")
                    .anyRequest().permitAll();

    }
}

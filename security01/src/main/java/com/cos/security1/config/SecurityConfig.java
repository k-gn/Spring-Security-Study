package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // 시큐리티 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").hasAnyRole("ROLE_ADMIN", "ROLE_MANAGER")
                .antMatchers("/admin/**").hasAnyRole("ROLE_ADMIN")
                .anyRequest().permitAll()
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/loginProc")
                .defaultSuccessUrl("/")
            .and()
            .oauth2Login()
                .loginPage("/login") // 근데 여기 아래로 생략해도 알아서 잘 동작한다...
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
        
        // 소셜 로그인 후 후처리가 필요함. (Tip. oauth2-client 사용 시 코드 X, 액세스 토큰 + 사용자 정보를 한번에 받아 편리함)
        // 1. 코드받기(인증), 2. 액세스 토큰(해당 사용자 정보에 접근 권한),
        // 3. 사용자 프로필정보를 가져옴, 4. 그 정보를 토대로 회원가입 처리 or 그냥 필요 시 사용

    }
}

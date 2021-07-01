package me.silvernine.tutorial.config;

import lombok.RequiredArgsConstructor;
import me.silvernine.tutorial.jwt.JwtAccessDeniedHandler;
import me.silvernine.tutorial.jwt.JwtAuthenticationEntryPoint;
import me.silvernine.tutorial.jwt.JwtSecurityConfig;
import me.silvernine.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

// 인증 ( Authentication ) 이란, 방문자가 자신이 회사 건물에 들어 갈 수있는지 확인 받는 과정이다.
// 인가 ( Authorization ) 이란, 방문자가 회사 건물에 방문했을 때, 허가된 공간에만 접근 가능하다
@EnableWebSecurity // 기본적인 웹보안 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 추가적인 설정을 위해 WebSecurityConfigurerAdapter 상속

    private final TokenProvider tokenProvider;
//    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 스프링 시큐리티의 비밀번호 암호화 설정은 필수 -> PasswordEncoder 를 등록해야 한다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 스프링 시큐리티 룰을 무시하게 하는 Url 규칙
    // security 전역 설정을 할 수 있다. 밑에 HttpSecurity 보다 우선시 되며, static 파일 (css, js 같은) 인증이 필요없는 리소스는 이곳에서 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico", "/error");
    }

    // # 스프링시큐리티의 각종 설정은 HttpSecurity로 대부분 진행 (인가)
    // - 리소스(URL) 접근 권한 설정
    // - 인증 전체 흐름에 필요한 Login, Logout 페이지 인증완료 후 페이지 인증 실패 시 이동페이지 등등 설정
    // - 인증 로직을 커스텀하기위한 커스텀 필터 설정
    // - 기타 csrf, 강제 https 호출 등등 거의 모든 스프링시큐리티의 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests() // http 요청들에 대한 접근제한을 설정하겠다는 의미
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));; // jwt filter 를 등록할 설정 파일을 연결
    }

    // 스프링 시큐리티의 인증에 대한 지원을 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }
}

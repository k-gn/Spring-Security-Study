package me.silvernine.tutorial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// 인증 ( Authentication ) 이란, 방문자가 자신이 회사 건물에 들어 갈 수있는지 확인 받는 과정이다.
// 인가 ( Authorization ) 이란, 방문자가 회사 건물에 방문했을 때, 허가된 공간에만 접근 가능하다
@Configuration
@EnableWebSecurity // 기본적인 웹보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 추가적인 설정을 위해 WebSecurityConfigurerAdapter 상속

    // 스프링 시큐리티 룰을 무시하게 하는 Url 규칙
    // security 전역 설정을 할 수 있다. 밑에 HttpSecurity 보다 우선시 되며, static 파일 (css, js 같은) 인증이 필요없는 리소스는 이곳에서 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    // # 스프링시큐리티의 각종 설정은 HttpSecurity로 대부분 진행 (인가)
    // - 리소스(URL) 접근 권한 설정
    // - 인증 전체 흐름에 필요한 Login, Logout 페이지 인증완료 후 페이지 인증 실패 시 이동페이지 등등 설정
    // - 인증 로직을 커스텀하기위한 커스텀 필터 설정
    // - 기타 csrf, 강제 https 호출 등등 거의 모든 스프링시큐리티의 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // http 요청들에 대한 접근제한을 설정하겠다는 의미
                .antMatchers("/api/hello").permitAll()
                .anyRequest().authenticated();
    }

    // 스프링 시큐리티의 인증에 대한 지원을 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }
}

package com.example.springsecuritypractice.config;

import com.example.springsecuritypractice.filter.StopwatchFilter;
import com.example.springsecuritypractice.filter.TestAuthenticationFilter;
import com.example.springsecuritypractice.user.User;
import com.example.springsecuritypractice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security 설정 Config
 */
@Configuration
// WebSecurityConfigurerAdapter를 상속받은 config 클래스에 @EnableWebSecurity 어노테이션을 달면 SpringSecurityFilterChain이 자동으로 포함됨
@EnableWebSecurity 
@RequiredArgsConstructor
// WebSecurityConfigurerAdapter : 개발자가 좀 더 시큐리티 설정을 쉽게 할 수 있도록 구현되어 있다. (필요에 따라 오버라이딩해서 구현)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    // 스프링 시큐리티 규칙 대부분을 설정하는 메소드 (웹 기반 보안을 구성)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // add filter
        http.addFilterBefore(new StopwatchFilter(), WebAsyncManagerIntegrationFilter.class);
        http.addFilterBefore(new TestAuthenticationFilter(this.authenticationManager()), UsernamePasswordAuthenticationFilter.class);
        // basic authentication : 요청마다 username, password 데이터를 포함시켜서 보내는 방식 (세션이 필요없지만 보안에 취약해서 https 사용 권장)
        http.httpBasic().disable(); // basic authentication filter 비활성화
        // csrf
        http.csrf();
        // anonymous 설정, 인증이 안된 유저가 요청을 하거나 들어올 경우 시큐리티는 익명 유저로 만들어 authentication 객체 넣어준다.
//        http.anonymous().principal("");
        // remember-me
        http.rememberMe(); // 자동 로그인 (remember-me 쿠키 발행), rememberMeAuthenticationFilter 활성화
        // 경로별 권한 설정    
        // authorization, 인가 설정 시작
        http.authorizeRequests() 
                // /와 /home, /signup 은 모두에게 허용
                .antMatchers("/", "/home", "/signup").permitAll()
                .antMatchers("/note").hasRole("USER")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated();
        // login
        http.formLogin() // 폼 로그인 설정
                .loginPage("/login") // 로그인 페이지 url
                .defaultSuccessUrl("/") // 로그인 성공 시 이동할 url
                .permitAll(); // 모두 허용
        // logout
        // requestMatchers : 명확하게 요청 대상을 지정하는 경우 사용
        http.logout() // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 로그아웃 요청 경로
                .logoutSuccessUrl("/"); // 로그아웃 성공 시 이동할 url
    }

    // 전역 보안에 영향을주는 구성 설정에 사용됩니다 (자원 무시, 디버그 모드 설정, 사용자 지정 방화벽 정의를 구현하여 요청 거부 등)
    @Override
    public void configure(WebSecurity web) {
        // 정적 리소스 spring security 대상에서 제외, 필터 자체를 사용하지 않아서 permitall 보다 성능이 우수
//        web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // css, js, image, web jars, favicon
    }

    // HttpSecurity 패턴은 보안처리
    // WebSecurity 패턴은 보안예외처리(정적리소스, HTML)



    /**
     * UserDetailsService 구현
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    // 사용자를 찾을 때 사용한다. (빈 등록만 하면 알아서 동작)
    public UserDetailsService userDetailsService() {
        // 스프링이 로그인 요청을 가로챈 후 해당 사용자가 DB에 있는지 확인 후 리턴
        return username -> { // 람다 가능
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            return user;
        };
    }
}

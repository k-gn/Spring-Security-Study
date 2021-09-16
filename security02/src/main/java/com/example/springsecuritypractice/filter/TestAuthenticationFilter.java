package com.example.springsecuritypractice.filter;

import com.example.springsecuritypractice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테스트 유저인 경우 어드민과 유저 권한을 모두 준다.
 */
public class TestAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // 로그인 시 동작

    public TestAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);  // 로그인 커스텀 필터 구현 시 필수 작업
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Authentication authentication = super.attemptAuthentication(request, response);
        User user = (User) authentication.getPrincipal();
        if (user.getUsername().startsWith("tester")) {
            return new UsernamePasswordAuthenticationToken(user, null,
                    Stream.of("ROLE_ADMIN", "ROLE_USER").map(auth -> (GrantedAuthority) () -> auth).collect(Collectors.toList()));
        }

        return authentication;
    }
}

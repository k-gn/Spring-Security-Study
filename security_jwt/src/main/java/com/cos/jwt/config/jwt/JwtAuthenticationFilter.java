package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음.
// /login 요청해서 username, password 전송 시(post) 동작하는 필터
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    // /login 요청을 하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication : /login");
        // authenticationManager 로 로그인을 시도하면 PrincipalDetailsService 가 호출된다.
        // 이 후 PrincipalDetails를 세션에 담는다. (권한 관리를 위해서)
        // 마지막으로 JWT 토큰을 만들어 응답.

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("JwtAuthenticationFilter : "+loginRequestDto);

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword());

        // 인증 처리
        Authentication authentication =
                getAuthenticationManager().authenticate(authenticationToken);
        
        PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("Authentication : "+principalDetailis.getUser().getUsername());

        // authentication 객체가 세션영역에 저장 => 로그인 성공
        // 권한 관리를 시큐리티가 해준다.
        return authentication;
    }

    // 인증이 정상적으로 되었으면 실행되는 함수
    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos") // 토큰 이름
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) // 만료시간(현재시간에 더하기)
                .withClaim("id", principalDetailis.getUser().getId())
                .withClaim("username", principalDetailis.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET)); // Hash 암호방식의 secret key

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}

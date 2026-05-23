package com.example.FullCount2.common.filter;

import com.example.FullCount2.common.enums.UserRole;
import com.example.FullCount2.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor //final 필드 생성자 주입으로 처리, Autowired 없이 의존성 주입 가능
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; //JWT 생성/덤증/파싱 담당 유틸 클래스

    /**
     * 필터 로직
     * <p>
     * 모든 HTTP 요청이 메서드를 통과한다.
     * 토큰 추출 -> 검증 -> SecurityContext 저장 순서로 처리
     * 토큰이 없는 로그인 회원가입은 필터 통과
     *
     * @param request     클라이언트의 HTTP 요청 객체
     * @param response    서버의 HTTP 응답 객체
     * @param filterChain 다음 필터로 넘기는 채인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 토큰 꺼내기
        String tokenValue = request.getHeader("Authorization");

        //Bearer 제거 후 순수 토큰 문자열 추출 (없으면 null)
        String token = jwtUtil.extractToken(tokenValue);

        //토큰 없으면 다음 필터로 (로그인/회원가입은 토큰 없어도 통과)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 유효성 검증 (서명, 만료 여부 검증)
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401에러
            response.getWriter().write("유효하지 않은 토큰입니다.");
            return; //컨트롤러로 요청 가는 걸 차단
        }

        //토큰에서 userId 추출
        Long userId = jwtUtil.getUserId(token);

        //토큰에서 role 추출
        UserRole role = jwtUtil.getRole(token);

        //SecurityContext에 인증 정보 저장
        /**
         * Spring Security 인증 객체 생성
         *
         * principal: 인증된 사용자 식별자 -> userId를 넣어 컨트롤러에서 @AuthenticationPrincipal Long userId로 꺼냄
         * credentials -> 비밀번호 자리(JWT에서는 필요 없어 null)
         * authorities-> 사용자 권한 목록 Spring Security는 권한 앞에 "ROLE_" 접두사를 요구함
         */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );

        // 생성한 인증 객체를 SecurityContext에 저장 후 요청 처리 내낸 인증 정보로 사용
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //인증 완료 후 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}

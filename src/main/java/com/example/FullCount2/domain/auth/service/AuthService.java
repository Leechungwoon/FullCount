package com.example.FullCount2.domain.auth.service;

import com.example.FullCount2.common.enums.UserRole;
import com.example.FullCount2.common.util.JwtUtil;
import com.example.FullCount2.domain.auth.modle.AuthLoginRequest;
import com.example.FullCount2.domain.auth.modle.AuthLoginResponse;
import com.example.FullCount2.domain.auth.modle.AuthSignRequest;
import com.example.FullCount2.domain.auth.modle.AuthSignResponse;
import com.example.FullCount2.domain.user.entity.User;
import com.example.FullCount2.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthSignResponse signup(AuthSignRequest request) {

        //이메일 중복 여부 체크
        boolean exitsEmail = userRepository.existsByEmail(request.getEmail());

        if (exitsEmail) throw new RuntimeException("중복된 이메일 입니다.");

        //전화 번호 중복 체크
        boolean exitsPhone = userRepository.existsByPhone(request.getPhone());

        if (exitsPhone) throw new RuntimeException("중복된 전화번호입니다.");

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //회원가입 요청 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .address(request.getAddress())
                .birth(request.getBirth())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return AuthSignResponse.from(user);
    }

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request) {

        //회원 이메일 여부 확인
        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입된 회원이 아닙니다."));

        //비밀번호 데이터 불러오기
        boolean matches = passwordEncoder.matches(request.getPassword(), foundUser.getPassword());

        //비밀번호 검증
        if (!matches) {
            throw new RuntimeException("유효한 비밀번호가 아닙니다.");
        }

        //토큰 생성
        String accessToken = jwtUtil.createToken(
                foundUser.getId(),
                foundUser.getEmail(),
                foundUser.getNickname(),
                foundUser.getRole()
        );
        return AuthLoginResponse.from(accessToken);
    }

    //로그아웃
    @Transactional
    public void logout(Long userId) {
    }
}

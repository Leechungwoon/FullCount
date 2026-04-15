package com.example.FullCount2.domain.auth.service;

import com.example.FullCount2.domain.auth.modle.AuthSignRequest;
import com.example.FullCount2.domain.auth.modle.AuthSignResponse;
import com.example.FullCount2.domain.user.entity.User;
import com.example.FullCount2.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthSignResponse signup(AuthSignRequest request) {

        //이메일 중복 여부 체크
        boolean exitsEmail = userRepository.exitsByEmail(request.getEmail());

        if (exitsEmail) throw new RuntimeException("중복된 이메일 입니다.");

        //전화 번호 중복 체크
        boolean exitsPhone = userRepository.exitsByPhone(request.getPhone());

        if (exitsPhone) throw new RuntimeException("중복된 전화번호입니다.");

        //회원가입 요청 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .address(request.getAddress())
                .birth(request.getBirth())
                .build();

        userRepository.save(user);

        return AuthSignResponse.from(user);
    }

}

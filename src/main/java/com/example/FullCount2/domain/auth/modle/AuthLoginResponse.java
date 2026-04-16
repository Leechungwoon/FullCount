package com.example.FullCount2.domain.auth.modle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 생성, JSON 역직렬화할 때 빈생성자가 필요함
@AllArgsConstructor //@Builder 내부적으로 전체 필드 생성자 필요해서 사용
@Builder // from안에서 .builder 사용하기 위함
public class AuthLoginResponse {

    private String accessToken;

    public static AuthLoginResponse from(String accessToken) {
        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}



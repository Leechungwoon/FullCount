package com.example.FullCount2.domain.auth.modle;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthSignRequest {

    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String address;
    private String birth;

    @Builder
    public AuthSignRequest(String email, String password, String nickname, String phone, String address, String birth) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.birth = birth;
    }
}

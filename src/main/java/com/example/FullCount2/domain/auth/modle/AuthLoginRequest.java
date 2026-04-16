package com.example.FullCount2.domain.auth.modle;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginRequest {

    private String email;
    private String password;
}

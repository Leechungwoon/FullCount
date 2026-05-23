package com.example.FullCount2.domain.auth.contorller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.auth.modle.AuthLoginRequest;
import com.example.FullCount2.domain.auth.modle.AuthLoginResponse;
import com.example.FullCount2.domain.auth.modle.AuthSignRequest;
import com.example.FullCount2.domain.auth.modle.AuthSignResponse;
import com.example.FullCount2.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원가입 api
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signupApi(@RequestBody AuthSignRequest request) {

        //핵심 비지니스
        AuthSignResponse response = authService.signup(request);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("회원가입 됐습니다.", response));
    }

    //로그인 api
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> loginApi(@RequestBody AuthLoginRequest request) {

        //핵심 비지니스
        AuthLoginResponse response = authService.login(request);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("로그인 됐습니다.", response));
    }

    //로그아웃 api
    @DeleteMapping("/logout")
    public ResponseEntity<CommonResponse> logoutApi() {

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("로그아웃 됐습니다.",null));
    }
}

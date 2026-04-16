package com.example.FullCount2.domain.auth.contorller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.auth.modle.AuthSignRequest;
import com.example.FullCount2.domain.auth.modle.AuthSignResponse;
import com.example.FullCount2.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signupApi(@RequestBody AuthSignRequest request) {

        //핵심 비지니스
        AuthSignResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("회원가입 됐습니다.", response));
    }
}

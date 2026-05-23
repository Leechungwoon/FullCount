package com.example.FullCount2.domain.auth.modle;

import com.example.FullCount2.common.enums.UserRole;
import com.example.FullCount2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AuthSignResponse {

    private Long id;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String birth;
    private UserRole role;
    private LocalDateTime createAt;

    @Builder
    public AuthSignResponse(Long id, String email, String phone, String address, String birth, UserRole role, LocalDateTime createAt) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birth = birth;
        this.role = role;
        this.createAt = createAt;
    }

    public static AuthSignResponse from(User user) {
        return AuthSignResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .birth(user.getBirth())
                .role(user.getRole())
                .createAt(user.getCreateAt())
                .build();
    }
}

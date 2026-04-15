package com.example.FullCount2.domain.user.entity;

import com.example.FullCount2.common.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class User {
    @Entity
    @Table(name = "users")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class User extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 100)
        private String email;

        @Column(nullable = false, length = 255)
        private String password;

        @Column(nullable = false, length = 50)
        private String nickname;

        @Column(nullable = false)
        private boolean isDeleted = false;

        @Builder
        private User(String email, String password, String nickname) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
        }
    }
}

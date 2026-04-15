package com.example.FullCount2.domain.stadium.entity;

import com.example.FullCount2.common.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stadiums")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stadium extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private int totalCapacity;

    @Builder
    private Stadium(String name, String location, int totalCapacity) {
        this.name = name;
        this.location = location;
        this.totalCapacity = totalCapacity;
    }
}

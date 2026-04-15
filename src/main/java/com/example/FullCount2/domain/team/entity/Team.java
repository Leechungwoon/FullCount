package com.example.FullCount2.domain.team.entity;

import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.stadium.entity.Stadium;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, updatable = true, length = 50)
    private String name;

    @Column(nullable = false, updatable = true, length = 10)
    private String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @Builder
    private Team(String name, String shortName, Stadium stadium) {
        this.name = name;
        this.shortName = shortName;
        this.stadium = stadium;
    }
}

package com.example.FullCount2.domain.game.entity;

import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.stadium.entity.Stadium;
import com.example.FullCount2.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @Column(nullable = false)
    private LocalDateTime gameDateTime;

    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED"; // SCHEDULED / OPEN / CLOSED / CANCELLED

    @Builder
    private Game(Team homeTeam, Team awayTeam, Stadium stadium, LocalDateTime gameDateTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.stadium = stadium;
        this.gameDateTime = gameDateTime;
    }
}

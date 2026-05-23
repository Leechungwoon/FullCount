package com.example.FullCount2.domain.stadium.repository;

import com.example.FullCount2.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    // 경기장 조회
    Optional<Stadium> findById(Long Id);
}

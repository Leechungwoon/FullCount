package com.example.FullCount2.domain.section.reposiroty;

import com.example.FullCount2.domain.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByStadiumId(Long stadiumId);
}

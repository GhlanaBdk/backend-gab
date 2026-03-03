package com.gap.backendgap.repository;

import com.gap.backendgap.entity.Gab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GabRepository extends JpaRepository<Gab, Long> {

    Optional<Gab> findByCode(String code);

}

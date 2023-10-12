package com.example.mainserver.repository;

import com.example.mainserver.entity.Calculation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalculationRepository extends JpaRepository<Calculation, Long> {
    List<Calculation> findByUser_IdOrderByNumberAsc(Long userId);
    List<Calculation> findByUser_IdOrderByTimeDesc(Long userId);

}


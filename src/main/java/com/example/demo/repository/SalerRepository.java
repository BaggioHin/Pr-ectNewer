package com.example.demo.repository;

import com.example.demo.entity.sales.Saler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SalerRepository extends JpaRepository<Saler, Long> {
    @Query("select max(s.code) from Saler s where s.code like concat(:prefix, '%')")
    String findMaxSalerCodeByPrefix(@Param("prefix") String prefix);
}

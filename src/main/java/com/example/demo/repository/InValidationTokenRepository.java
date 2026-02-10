package com.example.demo.repository;

import com.example.demo.entity.authAndUser.InvalidationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InValidationTokenRepository extends JpaRepository<InvalidationTokenEntity,String> {
}

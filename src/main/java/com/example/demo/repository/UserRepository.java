package com.example.demo.repository;

import com.example.demo.entity.authAndUser.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    boolean existsByUsernameAndIdNot(String username, Long userId);

    @Query(
            value = """
                    select u.*
                    from users u
                    join user_profile p on p.user_id = u.id
                    where p.name = :name
                    """,
            nativeQuery = true
    )
    List<User> findByProfileName(@Param("name") String name);

    @Query("select count(u) from User u where not exists (select r from u.roles r where r.name = :role)")
    long countUsersExcludingRole(@Param("role") String role);

    @Query("select count(u) from User u join u.roles r where r.name = :role")
    long countUsersByRole(@Param("role") String role);

    @Query("select count(u) from User u join u.roles r " +
            "where r.name = :role and u.createdAt >= :start and u.createdAt < :end")
    long countUsersByRoleAndCreatedAtBetween(
            @Param("role") String role,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("select u from User u join u.roles r where r.name = :role")
    List<User> findByRoleName(@Param("role") String role);
}

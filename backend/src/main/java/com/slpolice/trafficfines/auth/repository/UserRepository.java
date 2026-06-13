package com.slpolice.trafficfines.auth.repository;

import com.slpolice.trafficfines.auth.entity.Role;
import com.slpolice.trafficfines.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // ── Admin reporting queries ─────────────────────────────────────────────

    long countByRole(Role role);

    @Query("SELECT u.id, u.fullName, u.district, u.phoneNumber FROM User u WHERE u.role = 'OFFICER'")
    List<Object[]> findAllOfficerSummaries();
}

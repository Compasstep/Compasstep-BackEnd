package com.fifo.compasstep.admin.repository;

import com.fifo.compasstep.admin.domain.Admin;
import com.fifo.compasstep.admin.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findById(Long AdminId);
    Boolean existsByEmail(String email);
    List<Admin> findByRole(Role role);
}

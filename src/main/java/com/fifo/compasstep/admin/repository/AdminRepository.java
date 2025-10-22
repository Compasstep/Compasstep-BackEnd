package com.fifo.compasstep.admin.repository;

import com.fifo.compasstep.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findById(Long AdminId);
    Boolean existsByEmail(String email);

}

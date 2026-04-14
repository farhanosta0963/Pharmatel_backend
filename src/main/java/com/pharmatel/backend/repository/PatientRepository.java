package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByAccountId(UUID accountId);
    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

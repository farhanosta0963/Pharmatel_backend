package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Page<Prescription> findByPatientIdAndDeletedFalse(Integer patientId, Pageable pageable);

    Optional<Prescription> findByIdAndDeletedFalse(UUID id);
    Page<Prescription> findByDeletedFalse(Pageable pageable);
}

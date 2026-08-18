package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Medicine;
import com.pharmatel.backend.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Page<Prescription> findByPatientIdAndDeletedFalse(Integer patientId, Pageable pageable);

    Page<Prescription> findByPharmacy_IdAndDeletedFalseAndMedicine_NameContainingIgnoreCase(
    Integer pharmacyId,
    String medicineName,
    Pageable pageable
);
    Page<Prescription> findByPharmacy_IdAndDeletedFalse(Integer pharmacyId, Pageable pageable);
    // Page<Medicine> findByNameContainingIgnoreCase(String name, Pageable
    // pageable);

    Optional<Prescription> findByIdAndDeletedFalse(UUID id);

    Page<Prescription> findByDeletedFalse(Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Prescription p WHERE p.isDone = false AND (p.deleted = false OR p.deleted IS NULL)")
    java.util.List<Prescription> findActivePrescriptions();
}

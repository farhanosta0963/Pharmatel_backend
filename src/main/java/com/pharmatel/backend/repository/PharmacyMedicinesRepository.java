package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.PharmacyMedicines;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PharmacyMedicinesRepository extends JpaRepository<PharmacyMedicines, Integer> {
    // List<PharmacyMedicines> findByPharmacyId(Integer pharmacyId);
    Optional<PharmacyMedicines> findByPharmacyIdAndMedicineId(Integer pharmacyId, Integer medicineId);
    Page<PharmacyMedicines> findAll(Pageable pageable);

    Page<PharmacyMedicines> findByPharmacyId(Integer pharmacyId, Pageable pageable);
}

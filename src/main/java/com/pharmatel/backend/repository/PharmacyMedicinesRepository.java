package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.entity.PharmacyMedicines;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PharmacyMedicinesRepository extends JpaRepository<PharmacyMedicines, Integer> {
    // List<PharmacyMedicines> findByPharmacyId(Integer pharmacyId);
    Optional<PharmacyMedicines> findByPharmacyIdAndMedicineId(Integer pharmacyId, Integer medicineId);
    Page<PharmacyMedicines> findAll(Pageable pageable);

    Page<PharmacyMedicines> findByPharmacyId(Integer pharmacyId, Pageable pageable);
    @Query("SELECT AVG(pm.price) FROM PharmacyMedicines pm WHERE pm.medicine.id = :medicineId and pm.available = true")
    Double findAveragePriceByMedicineId(Integer medicineId);
    Page<PharmacyMedicines> findByPharmacyIdAndMedicine_NameContainingIgnoreCase(Integer pharmacyId, String medicineName,
            PageRequest of);

}

package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    Page<Medicine> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Medicine> findByNameIgnoreCase(String name);
}

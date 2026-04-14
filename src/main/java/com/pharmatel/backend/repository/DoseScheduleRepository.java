package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.DoseSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DoseScheduleRepository extends JpaRepository<DoseSchedule, Integer> {

    @Query("""
        select ds from DoseSchedule ds
        join ds.prescription p
        where p.patient.id = :patientId
          and coalesce(ds.deleted, false) = false
          and coalesce(p.deleted, false) = false
        """)
    Page<DoseSchedule> findPatientDoseSchedules(@Param("patientId") Integer patientId, Pageable pageable);

    List<DoseSchedule> findByPrescriptionId(UUID prescriptionId);
    Page<DoseSchedule> findByDeletedFalse(Pageable pageable);
}

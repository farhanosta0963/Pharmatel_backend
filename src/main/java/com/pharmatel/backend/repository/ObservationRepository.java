package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Observation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ObservationRepository extends JpaRepository<Observation, UUID> {

    @Query("""
        select o from Observation o
        join o.observationSession os
        where os.patient.id = :patientId
        """)
    Page<Observation> findByPatientId(@Param("patientId") Integer patientId, Pageable pageable);
}

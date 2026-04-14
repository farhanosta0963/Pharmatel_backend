package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.ObservationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservationSessionRepository extends JpaRepository<ObservationSession, UUID> {
}

package com.pharmatel.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "observation_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationSession {

    @Id
    private UUID id;

    @ManyToOne 
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne 
    @JoinColumn(name = "dose_schedule_id")
    private DoseSchedule doseSchedule;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

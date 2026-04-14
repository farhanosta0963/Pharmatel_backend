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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    private UUID id;

    @ManyToOne 
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne 
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private String dose;

    private String frequency;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "by_pharmacist")
    private Boolean byPharmacist;

    @ManyToOne 
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @Column(name = "food_requirement")
    private String foodRequirement;

    @Column(name = "deleted")
    private Boolean deleted;
}

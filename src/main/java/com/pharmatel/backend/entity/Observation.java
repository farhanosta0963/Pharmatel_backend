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

import java.util.UUID;

@Entity
@Table(name = "observation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observation {

    @Id
    private UUID id;

    @ManyToOne 
    @JoinColumn(name = "observation_session_id")
    private ObservationSession observationSession;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @Column(name = "value_numeric")
    private Double valueNumeric;

    @Column(name = "value_text")
    private String valueText;


    @Column(name = "symptom_type")
    private String symptomType;

    @Column(name = "measurement_unit")
    private String measurementUnit;

    
}

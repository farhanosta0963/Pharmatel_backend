package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.observation.ObservationDto;
import com.pharmatel.backend.entity.Observation;
import org.springframework.stereotype.Component;

@Component
public class ObservationMapper {

    public ObservationDto toDto(Observation observation) {
        return ObservationDto.builder()
            .id(observation.getId())
            .observationSessionId(observation.getObservationSession().getId())
            .patientId(observation.getObservationSession().getPatient().getId())
            .doseScheduleId(observation.getObservationSession().getDoseSchedule() == null
                ? null
                : observation.getObservationSession().getDoseSchedule().getId())
            .symptomType(observation.getSymptomType())
            .measurementUnit(observation.getMeasurementUnit())
            .valueBoolean(observation.getValueBoolean())
            .valueNumeric(observation.getValueNumeric())
            .valueText(observation.getValueText())
            .createdAt(observation.getObservationSession().getCreatedAt())
            .build();
    }
}

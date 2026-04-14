package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.doseschedule.DoseScheduleDto;
import com.pharmatel.backend.entity.DoseSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoseScheduleMapper {

    public DoseScheduleDto toDto(DoseSchedule doseSchedule) {
        return DoseScheduleDto.builder()
            .id(doseSchedule.getId())
            .prescriptionId(doseSchedule.getPrescription().getId())
            .takeAt(doseSchedule.getTakeAt())
            .taken(doseSchedule.getTaken())
            .takenAt(doseSchedule.getTakenAt())
            .patientPersonalNote(doseSchedule.getPatientPersonalNote())
            .createdAt(doseSchedule.getCreatedAt())
            .build();
    }
}

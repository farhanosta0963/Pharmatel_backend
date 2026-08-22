package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.patient.PatientDto;
import com.pharmatel.backend.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientDto toDto(Patient patient) {
        return PatientDto.builder()
            .id(patient.getId())
            .name(patient.getName())
            .email(patient.getEmail())
            .phoneNumber(patient.getPhoneNumber())
            .medicalRecordNumber(patient.getMedicalRecordNumber())
            .dateOfBirth(patient.getDateOfBirth())
            .gender(patient.getGender())
            .heightCm(patient.getHeightCm())
            .weightKg(patient.getWeightKg())
            .imageBase64(patient.getImage())
            .diagnosis(patient.getDiagnosis())
            .allergies(patient.getAllergies())
            .build();
    }
}

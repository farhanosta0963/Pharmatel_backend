package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.observation.CreateObservationRequest;
import com.pharmatel.backend.dto.observation.ObservationDto;
import com.pharmatel.backend.dto.observation.UpdateObservationRequest;
import com.pharmatel.backend.entity.DoseSchedule;
import com.pharmatel.backend.entity.Observation;
import com.pharmatel.backend.entity.ObservationSession;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.ObservationMapper;
import com.pharmatel.backend.repository.DoseScheduleRepository;
import com.pharmatel.backend.repository.ObservationRepository;
import com.pharmatel.backend.repository.ObservationSessionRepository;
import com.pharmatel.backend.repository.PatientRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final ObservationSessionRepository observationSessionRepository;
    private final PatientRepository patientRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final ObservationMapper observationMapper;

    @Transactional
    public ObservationDto create(AppUserDetails user, CreateObservationRequest request) {
        log.info("Create observation patientId={} doseScheduleId={} user={}", request.getPatientId(),  user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        ensurePatientOrPharmacy(user, patient);

        // DoseSchedule doseSchedule = null;

        //     if (request.getDoseScheduleId() != null) {
        //         doseSchedule = doseScheduleRepository.findById(request.getDoseScheduleId())
        //                 .orElseThrow(() -> new ResourceNotFoundException(
        //                         "Dose schedule not found: " + request.getDoseScheduleId()));

        //         if (doseSchedule.getPrescription() == null
        //                 || doseSchedule.getPrescription().getPatient() == null
        //                 || !doseSchedule.getPrescription().getPatient().getId().equals(patient.getId())) {
        //             throw new IllegalArgumentException("Dose schedule does not belong to patient");
        //         }
        //     }


        ObservationSession session = resolveSession(request, patient);

        Observation observation = Observation.builder()
            .id(UUID.randomUUID())
            .observationSession(session)
            .valueBoolean(request.getValueBoolean())
            .valueNumeric(request.getValueNumeric())
            .valueText(request.getValueText())
            .symptomType(request.getSymptomType())
            .measurementUnit(request.getMeasurementUnit())
            .build();

        return observationMapper.toDto(observationRepository.save(observation));
    }

    public PageResponse<ObservationDto> listByPatient(AppUserDetails user, Integer patientId, int page, int size) {
        log.info("List observations patientId={} page={} size={} user={}", patientId, page, size, user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        ensurePatientOrPharmacy(user, patient);
        return PageResponse.from(observationRepository.findByPatientId(patientId, PageRequest.of(page, size))
            .map(observationMapper::toDto));
    }

    public PageResponse<ObservationDto> listAll(AppUserDetails user, int page, int size) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can list all observations");
        }
        log.info("List all observations page={} size={} user={}", page, size, user.getUsername());
        return PageResponse.from(observationRepository.findAll(PageRequest.of(page, size)).map(observationMapper::toDto));
    }

    public ObservationDto getById(AppUserDetails user, UUID id) {
        log.info("Get observation id={} user={}", id, user == null ? null : user.getUsername());
        Observation observation = observationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Observation not found: " + id));
        ensurePatientOrPharmacy(user, observation.getObservationSession().getPatient());
        return observationMapper.toDto(observation);
    }

    @Transactional
    public ObservationDto update(AppUserDetails user, UUID id, UpdateObservationRequest request) {
        log.info("Update observation id={} user={}", id, user == null ? null : user.getUsername());
        Observation observation = observationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Observation not found: " + id));
        ensurePatientOrPharmacy(user, observation.getObservationSession().getPatient());
        


        observation.setSymptomType(request.getSymptomType());
        observation.setMeasurementUnit(request.getMeasurementUnit());
        observation.setValueBoolean(request.getValueBoolean());
        observation.setValueNumeric(request.getValueNumeric());
        observation.setValueText(request.getValueText());
        return observationMapper.toDto(observationRepository.save(observation));
    }

    @Transactional
    public void delete(AppUserDetails user, UUID id) {
        log.info("Delete observation id={} user={}", id, user == null ? null : user.getUsername());
        Observation observation = observationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Observation not found: " + id));
        ensurePatientOrPharmacy(user, observation.getObservationSession().getPatient());
        observationRepository.delete(observation);
    }

    private void ensurePatientOrPharmacy(AppUserDetails user, Patient patient) {
        if (user == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (user.getRole() == AppRole.PHARMACY) {
            return;
        }
        if (patient.getAccount() == null || !patient.getAccount().getId().equals(user.getAccountId())) {
            throw new ForbiddenException("You cannot access this patient's observations");
        }
    }

    private ObservationSession resolveSession(CreateObservationRequest request, Patient patient) {
        if (request.getObservationSessionId() == null) {
            ObservationSession session = ObservationSession.builder()
                .id(UUID.randomUUID())
                .patient(patient)
                .createdAt(LocalDateTime.now())
                .build();
            return observationSessionRepository.save(session);
        }

        ObservationSession existing = observationSessionRepository.findById(request.getObservationSessionId())
            .orElseThrow(() -> new ResourceNotFoundException("Observation session not found: " + request.getObservationSessionId()));

        if (existing.getPatient() == null || !existing.getPatient().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("Observation session does not belong to patient");
        }
        
        return existing;
    }
}

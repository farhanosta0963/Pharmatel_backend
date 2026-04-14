package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.doseschedule.CreateDoseScheduleRequest;
import com.pharmatel.backend.dto.doseschedule.DoseScheduleDto;
import com.pharmatel.backend.dto.doseschedule.TakeDoseRequest;
import com.pharmatel.backend.dto.doseschedule.UpdateDoseScheduleRequest;
import com.pharmatel.backend.entity.DoseSchedule;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.entity.Prescription;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.DoseScheduleMapper;
import com.pharmatel.backend.repository.DoseScheduleRepository;
import com.pharmatel.backend.repository.PatientRepository;
import com.pharmatel.backend.repository.PrescriptionRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoseScheduleService {

    private final DoseScheduleRepository doseScheduleRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoseScheduleMapper doseScheduleMapper;

    public PageResponse<DoseScheduleDto> listByPatient(AppUserDetails user, Integer patientId, int page, int size) {
        log.info("List dose schedules by patientId={} page={} size={} user={}", patientId, page, size, user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        ensurePatientOrPharmacy(user, patient);

        return PageResponse.from(
            doseScheduleRepository.findPatientDoseSchedules(patientId, PageRequest.of(page, size))
                .map(doseScheduleMapper::toDto)
        );
    }

    public PageResponse<DoseScheduleDto> listAll(AppUserDetails user, int page, int size) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can list all dose schedules");
        }
        log.info("List all dose schedules page={} size={} user={}", page, size, user.getUsername());
        return PageResponse.from(doseScheduleRepository.findByDeletedFalse(PageRequest.of(page, size)).map(doseScheduleMapper::toDto));
    }

    public DoseScheduleDto getById(AppUserDetails user, Integer id) {
        log.info("Get dose schedule id={} user={}", id, user == null ? null : user.getUsername());
        DoseSchedule schedule = doseScheduleRepository.findById(id)
            .filter(ds -> !Boolean.TRUE.equals(ds.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Dose schedule not found: " + id));
        ensurePatientOrPharmacy(user, schedule.getPrescription().getPatient());
        return doseScheduleMapper.toDto(schedule);
    }

    @Transactional
    public DoseScheduleDto create(AppUserDetails user, CreateDoseScheduleRequest request) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can create dose schedules");
        }
        log.info("Create dose schedule prescriptionId={} user={}", request.getPrescriptionId(), user.getUsername());
        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(request.getPrescriptionId())
            .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + request.getPrescriptionId()));

        DoseSchedule schedule = DoseSchedule.builder()
            .prescription(prescription)
            .takeAt(request.getTakeAt())
            .taken(Boolean.TRUE.equals(request.getTaken()))
            .takenAt(request.getTakenAt())
            .patientPersonalNote(request.getPatientPersonalNote())
            .createdAt(LocalDateTime.now())
            .deleted(false)
            .build();
        return doseScheduleMapper.toDto(doseScheduleRepository.save(schedule));
    }

    @Transactional
    public DoseScheduleDto update(AppUserDetails user, Integer id, UpdateDoseScheduleRequest request) {
        log.info("Update dose schedule id={} user={}", id, user == null ? null : user.getUsername());
        DoseSchedule schedule = doseScheduleRepository.findById(id)
            .filter(ds -> !Boolean.TRUE.equals(ds.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Dose schedule not found: " + id));
        ensurePatientOrPharmacy(user, schedule.getPrescription().getPatient());
        if (request.getTakeAt() != null) schedule.setTakeAt(request.getTakeAt());
        if (request.getTaken() != null) schedule.setTaken(request.getTaken());
        if (request.getTakenAt() != null) schedule.setTakenAt(request.getTakenAt());
        if (request.getPatientPersonalNote() != null) schedule.setPatientPersonalNote(request.getPatientPersonalNote());
        return doseScheduleMapper.toDto(doseScheduleRepository.save(schedule));
    }

    @Transactional
    public DoseScheduleDto markTaken(AppUserDetails user, Integer id, TakeDoseRequest request) {
        log.info("Mark dose taken id={} user={}", id, user == null ? null : user.getUsername());
        DoseSchedule schedule = doseScheduleRepository.findById(id)
            .filter(ds -> !Boolean.TRUE.equals(ds.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Dose schedule not found: " + id));
        ensurePatientOrPharmacy(user, schedule.getPrescription().getPatient());

        schedule.setTaken(true);
        schedule.setTakenAt(LocalDateTime.now());
        if (request != null) {
            schedule.setPatientPersonalNote(request.getPatientPersonalNote());
        }

        return doseScheduleMapper.toDto(doseScheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(AppUserDetails user, Integer id) {
        log.info("Delete dose schedule id={} user={}", id, user == null ? null : user.getUsername());
        DoseSchedule schedule = doseScheduleRepository.findById(id)
            .filter(ds -> !Boolean.TRUE.equals(ds.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Dose schedule not found: " + id));
        ensurePatientOrPharmacy(user, schedule.getPrescription().getPatient());
        schedule.setDeleted(true);
        doseScheduleRepository.save(schedule);
    }

    private void ensurePatientOrPharmacy(AppUserDetails user, Patient patient) {
        if (user == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (user.getRole() == AppRole.PHARMACY) {
            return;
        }
        if (patient.getAccount() == null || !patient.getAccount().getId().equals(user.getAccountId())) {
            throw new ForbiddenException("You cannot access this patient's dose schedules");
        }
    }
}

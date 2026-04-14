package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.prescription.CreatePrescriptionRequest;
import com.pharmatel.backend.dto.prescription.PrescriptionDto;
import com.pharmatel.backend.dto.prescription.UpdatePrescriptionRequest;
import com.pharmatel.backend.entity.DoseSchedule;
import com.pharmatel.backend.entity.Medicine;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.entity.Prescription;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.PrescriptionMapper;
import com.pharmatel.backend.repository.DoseScheduleRepository;
import com.pharmatel.backend.repository.MedicineRepository;
import com.pharmatel.backend.repository.PatientRepository;
import com.pharmatel.backend.repository.PharmacyRepository;
import com.pharmatel.backend.repository.PrescriptionRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;
    private final PharmacyRepository pharmacyRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final PrescriptionMapper prescriptionMapper;
    private static final Pattern HOURS_PATTERN = Pattern.compile("(\\d+)");

    
    
    @Transactional
    public PrescriptionDto create(AppUserDetails user, CreatePrescriptionRequest request) {
        log.info("Creating prescription for patientId={} medicineId={} by user={}", request.getPatientId(), request.getMedicineId(), user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        ensurePatientOrPharmacy(user, patient);

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + request.getMedicineId()));

        Pharmacy pharmacy = null;
        if (request.getPharmacyId() != null) {
            pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found: " + request.getPharmacyId()));
        }
        if(request.getByPharmacist() != checkIfPharmacist(user)){ // TODO Coordination with fornt 
            throw new ForbiddenException("Wrong byPharmacist flag ") ; 
        }
        Prescription prescription = Prescription.builder()
            .id(UUID.randomUUID())
            .patient(patient)
            .medicine(medicine)
            .dose(request.getDose())
            .frequency(request.getFrequency())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .issuedAt(LocalDateTime.now())
            .byPharmacist(checkIfPharmacist(user))
            .pharmacy(pharmacy)
            .foodRequirement(request.getFoodRequirement())
            .deleted(false)
            .build();
        
        Prescription saved = prescriptionRepository.save(prescription);

        createDoseSchedules(saved);

        // if (request.getScheduleTimes() != null) {
        //     List<DoseSchedule> schedules = request.getScheduleTimes().stream()
        //         .map(time -> DoseSchedule.builder()
        //             .prescription(saved)
        //             .takeAt(time)
        //             .taken(false)
        //             .createdAt(LocalDateTime.now())
        //             .deleted(false)
        //             .build())
        //         .toList();
        //     doseScheduleRepository.saveAll(schedules);
        // }

        return prescriptionMapper.toDto(saved);
    }

    public PageResponse<PrescriptionDto> listByPatient(AppUserDetails user, Integer patientId, int page, int size) {
        log.info("Listing prescriptions by patientId={} page={} size={} user={}", patientId, page, size, user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        ensurePatientOrPharmacy(user, patient);

        return PageResponse.from(
            prescriptionRepository.findByPatientIdAndDeletedFalse(patientId, PageRequest.of(page, size))
                .map(prescriptionMapper::toDto)
        );
    }

    public PageResponse<PrescriptionDto> listAll(AppUserDetails user, int page, int size) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can list all prescriptions");
        }
        log.info("Listing all prescriptions page={} size={} user={}", page, size, user.getUsername());
        return PageResponse.from(prescriptionRepository.findByDeletedFalse(PageRequest.of(page, size)).map(prescriptionMapper::toDto));
    }

    public PrescriptionDto getById(AppUserDetails user, UUID id) {
        log.info("Get prescription id={} user={}", id, user == null ? null : user.getUsername());
        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        ensurePatientOrPharmacy(user, prescription.getPatient());
        return prescriptionMapper.toDto(prescription);
    }

    @Transactional
    public PrescriptionDto update(AppUserDetails user, UUID id, UpdatePrescriptionRequest request) {
        log.info("Update prescription id={} user={}", id, user == null ? null : user.getUsername());
        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        ensurePatientOrPharmacy(user, prescription.getPatient());
        if (request.getDose() != null) prescription.setDose(request.getDose());
        if (request.getFrequency() != null) prescription.setFrequency(request.getFrequency());
        if (request.getStartDate() != null) prescription.setStartDate(request.getStartDate());
        prescription.setEndDate(request.getEndDate());
        if (request.getByPharmacist() != null) prescription.setByPharmacist(request.getByPharmacist());
        if (request.getFoodRequirement() != null) prescription.setFoodRequirement(request.getFoodRequirement());
        if (request.getPharmacyId() != null) {
            Pharmacy pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found: " + request.getPharmacyId()));
            prescription.setPharmacy(pharmacy);
        }
        return prescriptionMapper.toDto(prescriptionRepository.save(prescription));
    }

    @Transactional
    public void softDelete(AppUserDetails user, UUID id) {
        log.info("Soft delete prescription id={} user={}", id, user == null ? null : user.getUsername());
        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        ensurePatientOrPharmacy(user, prescription.getPatient());

        prescription.setDeleted(true);
        prescriptionRepository.save(prescription);

        List<DoseSchedule> schedules = doseScheduleRepository.findByPrescriptionId(id);
        schedules.forEach(schedule -> schedule.setDeleted(true));
        doseScheduleRepository.saveAll(schedules);
    }

    private Boolean checkIfPharmacist ( AppUserDetails user) {
        return user != null && user.getRole() == AppRole.PHARMACY;
    }
    private void createDoseSchedules(Prescription prescription) {
        int intervalHours = extractIntervalHours(prescription.getFrequency()); // e.g. "2 hours" -> 2

        LocalDateTime cursor = prescription.getStartDate(); // start from startDate
        LocalDateTime end = prescription.getEndDate();

        if (intervalHours <= 0) {
            throw new IllegalArgumentException("Frequency must be a positive hour interval");
        }
        if (cursor == null || end == null || cursor.isAfter(end)) {
            return;
        }

        List<DoseSchedule> schedules = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        while (!cursor.isAfter(end)) {
            schedules.add(DoseSchedule.builder()
                    .prescription(prescription)
                    .takeAt(cursor)
                    .taken(false)
                    .takenAt(null)
                    .patientPersonalNote(null)
                    .createdAt(now)
                    .deleted(false)
                    .build());

            cursor = cursor.plusHours(intervalHours);
        }

        doseScheduleRepository.saveAll(schedules);
    }

    private int extractIntervalHours(String frequency) {
        // supports: "2 hours", "6h", "every 4 hours"
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency is required");
        }
        Matcher matcher = HOURS_PATTERN.matcher(frequency.toLowerCase());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid frequency format: " + frequency);
        }
        return Integer.parseInt(matcher.group(1));
    }

    private void ensurePatientOrPharmacy(AppUserDetails user, Patient patient) {
        if (user == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (user.getRole() == AppRole.PHARMACY) {
            return;
        }
        if (patient.getAccount() == null || !patient.getAccount().getId().equals(user.getAccountId())) {
            throw new ForbiddenException("You cannot access this patient's prescriptions");
        }
    }
}

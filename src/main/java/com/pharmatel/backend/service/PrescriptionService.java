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
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.Objects;

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
        log.info("Creating prescription for patientId={} medicineId={} by user={}", request.getPatientId(),
                request.getMedicineId(), user == null ? null : user.getUsername());
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
        if (request.getByPharmacist() != checkIfPharmacist(user)) { // TODO Coordination with fornt
            throw new ForbiddenException("Wrong byPharmacist flag ");
        }
        Prescription prescription = Prescription.builder()
                .id(UUID.randomUUID())
                .patient(patient)
                .medicine(medicine)
                .dose(request.getDose())
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .issuedAt(LocalDateTime.now())
                .byPharmacist(checkIfPharmacist(user))
                .pharmacy(pharmacy)
                .foodRequirement(request.getFoodRequirement())
                .note(request.getNote())
                .byDoctor(request.getByDoctor())
                .timeShift((request.getTimeShift() != null) ? request.getTimeShift() : 0)
                .doctorName(request.getDoctorName())
                .isDone(false)
                .deleted(false)
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        generateDoseSchedulesForPrescription(saved);

        return prescriptionMapper.toDto(saved);
    }

    public PageResponse<PrescriptionDto> listByPatient(AppUserDetails user, Integer patientId, int page, int size) {
        log.info("Listing prescriptions by patientId={} page={} size={} user={}", patientId, page, size,
                user == null ? null : user.getUsername());
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        ensurePatientOrPharmacy(user, patient);

        return PageResponse.from(
                prescriptionRepository.findByPatientIdAndDeletedFalse(patientId, PageRequest.of(page, size))
                        .map(prescriptionMapper::toDto));
    }

    public PageResponse<PrescriptionDto> listAll(AppUserDetails user, int page, int size) {
        // if (user == null || user.getRole() != AppRole.PHARMACY) {
        // throw new ForbiddenException("Only pharmacy users can list all
        // prescriptions");
        // }
        log.info("Listing all prescriptions page={} size={} user={}", page, size, user.getUsername());
        return PageResponse.from(
                prescriptionRepository.findByDeletedFalse(PageRequest.of(page, size)).map(prescriptionMapper::toDto));
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

        Integer oldTimeShift = prescription.getTimeShift();

        if (request.getDose() != null)
            prescription.setDose(request.getDose());
        if (request.getFoodRequirement() != null)
            prescription.setFoodRequirement(request.getFoodRequirement());
        if (request.getNote() != null)
            prescription.setNote(request.getNote());
        if (request.getTimeShift() != null)
            prescription.setTimeShift(request.getTimeShift());

        Prescription saved = prescriptionRepository.save(prescription);

        if (request.getTimeShift() != null ) {
            List<DoseSchedule> schedules = doseScheduleRepository.findByPrescriptionIdAndScheduledAtAfter(id, LocalDateTime.now());
            int diff = request.getTimeShift() - (oldTimeShift != null ? oldTimeShift : 0);
            for (DoseSchedule schedule : schedules) {
                if (Boolean.FALSE.equals(schedule.getTaken()) && schedule.getScheduledAt() != null) {
                    schedule.setScheduledAt(schedule.getScheduledAt().plusHours(diff));
                }
            }// front end should send the timeshift after sum with old timeshift 
            doseScheduleRepository.saveAll(schedules);
        }


        return prescriptionMapper.toDto(saved);
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

    @Transactional
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 2 * * *")
    public void generateDailySchedules() {
        log.info("Running daily scheduled job to generate dose schedules for active prescriptions");
        List<Prescription> activePrescriptions = prescriptionRepository.findActivePrescriptions();
        for (Prescription p : activePrescriptions) {
            try {
                generateDoseSchedulesForPrescription(p);
            } catch (Exception e) {
                log.error("Failed to generate schedules for prescription ID " + p.getId(), e);
            }
        }
    }

    private Boolean checkIfPharmacist(AppUserDetails user) {
        return user != null && user.getRole() == AppRole.PHARMACY;
    }

    @Transactional
    public void generateDoseSchedulesForPrescription(Prescription prescription) {
        if (Boolean.TRUE.equals(prescription.getIsDone()) || Boolean.TRUE.equals(prescription.getDeleted())) {
            return; // don't generate if done or deleted
        }
        log.info("Generating dose schedules for prescription ID {}", prescription.getId());
        int intervalHours = extractIntervalHours(prescription.getFrequency());
        if (intervalHours <= 0) {
            log.warn("Invalid frequency {} for prescription {}", prescription.getFrequency(), prescription.getId());
            return;
        }

                LocalDateTime cursor;

        List<DoseSchedule> existingSchedules =
                doseScheduleRepository.findByPrescriptionId(prescription.getId());

        LocalDateTime lastScheduled = null;

        if (existingSchedules != null && !existingSchedules.isEmpty()) {
            lastScheduled = existingSchedules.stream()
                    .map(DoseSchedule::getScheduledAt)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }

        if (lastScheduled != null) {
            cursor = lastScheduled.plusHours(intervalHours);
        } else {
            cursor = prescription.getStartDate();
        }

        if (cursor == null) {
            cursor = LocalDateTime.now();
        }

        LocalDateTime targetEnd = LocalDateTime.now().plusDays(30);

        List<DoseSchedule> toSave = new ArrayList<>();
        while (cursor.isBefore(targetEnd)) {
            if (prescription.getEndDate() != null && cursor.isAfter(prescription.getEndDate())) {
                break;
            }

            LocalDateTime scheduledTime = cursor;
            

            toSave.add(DoseSchedule.builder()
                    .prescription(prescription)
                    .scheduledAt(scheduledTime)
                    .taken(false)
                    .createdAt(LocalDateTime.now())
                    .deleted(false)
                    .build());

            cursor = cursor.plusHours(intervalHours);
        }

        if (!toSave.isEmpty()) {
            doseScheduleRepository.saveAll(toSave);  }
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

    public void markDone(AppUserDetails user, UUID id) {
        log.info("Mark prescription done id={} user={}", id, user == null ? null : user.getUsername());
        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        ensurePatientOrPharmacy(user, prescription.getPatient());
        prescription.setIsDone(true);
        prescription.setEndDate(LocalDateTime.now());
        List <DoseSchedule> schedules = doseScheduleRepository.findByPrescriptionIdAndScheduledAtAfter(prescription.getId(), LocalDateTime.now());
        doseScheduleRepository.deleteAll(schedules);
        prescriptionRepository.save(prescription);

    }

    public PageResponse<PrescriptionDto> listAllForPharmacist(AppUserDetails user, int page, int size, String medicineName) {
         if (user == null || user.getRole() != AppRole.PHARMACY) {
        throw new ForbiddenException("Only pharmacy users can list all prescriptions");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Prescription> prescriptions;

        Pharmacy pharmacy = pharmacyRepository.findByAccountId(user.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pharmacy account not found: " + user.getAccountId()));

        if (medicineName != null && !medicineName.trim().isEmpty()) {
            prescriptions =
                prescriptionRepository
                    .findByPharmacy_IdAndDeletedFalseAndMedicine_NameContainingIgnoreCase(
                        pharmacy.getId(),
                        medicineName.trim(),
                        pageable
                    );
        } else {
            prescriptions =
                prescriptionRepository
                    .findByPharmacy_IdAndDeletedFalse(pharmacy.getId(), pageable);
        }
        return PageResponse.from(prescriptions.map(prescriptionMapper::toDto));
    }

    }

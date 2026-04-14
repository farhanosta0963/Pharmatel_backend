package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.prescription.CreatePrescriptionRequest;
import com.pharmatel.backend.dto.prescription.PrescriptionDto;
import com.pharmatel.backend.dto.prescription.UpdatePrescriptionRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prescriptions", description = "Prescription management endpoints")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create prescription", description = "Creates a prescription and optional dose schedules.")
    public PrescriptionDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreatePrescriptionRequest request
    ) {
        log.info("Incoming create prescription patientId={} medicineId={}", request.getPatientId(), request.getMedicineId());
        return prescriptionService.create(user, request);
    }

    @GetMapping("/prescriptions")
    @Operation(summary = "List prescriptions", description = "Returns paginated prescriptions.")
    public PageResponse<PrescriptionDto> list(
        @AuthenticationPrincipal AppUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list prescriptions page={} size={}", page, size);
        return prescriptionService.listAll(user, page, size);
    }

    @GetMapping("/prescriptions/{id}")
    @Operation(summary = "Get prescription by ID", description = "Returns prescription details by UUID.")
    public PrescriptionDto get(@AuthenticationPrincipal AppUserDetails user, @PathVariable UUID id) {
        log.info("Incoming get prescription id={}", id);
        return prescriptionService.getById(user, id);
    }

    @GetMapping("/patients/{id}/prescriptions")
    @Operation(summary = "List prescriptions by patient", description = "Returns paginated prescriptions for a patient.")
    public PageResponse<PrescriptionDto> byPatient(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list prescriptions by patientId={} page={} size={}", id, page, size);
        return prescriptionService.listByPatient(user, id, page, size);
    }

    @PutMapping("/prescriptions/{id}")
    @Operation(summary = "Update prescription", description = "Updates mutable prescription fields.")
    public PrescriptionDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePrescriptionRequest request
    ) {
        log.info("Incoming update prescription id={}", id);
        return prescriptionService.update(user, id, request);
    }

    @DeleteMapping("/prescriptions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete prescription", description = "Soft-deletes prescription and linked schedules.")
    public void softDelete(@AuthenticationPrincipal AppUserDetails user, @PathVariable UUID id) {
        log.info("Incoming delete prescription id={}", id);
        prescriptionService.softDelete(user, id);
    }
}

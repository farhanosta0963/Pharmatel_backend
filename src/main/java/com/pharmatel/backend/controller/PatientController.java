package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.patient.CreatePatientRequest;
import com.pharmatel.backend.dto.patient.PatientDto;
import com.pharmatel.backend.dto.patient.UpdatePatientRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.PatientService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
@Slf4j
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "List patients", description = "Returns paginated patients with optional name filter.")
    public PageResponse<PatientDto> list(
        @AuthenticationPrincipal AppUserDetails user,
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list patients request page={} size={}", page, size);
        return patientService.list(user, name, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Returns patient details by identifier.")
    public PatientDto get(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming get patient id={}", id);
        return patientService.getById(user, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create patient", description = "Creates a new patient profile using request DTO.")
    public PatientDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreatePatientRequest request
    ) {
        log.info("Incoming create patient request email={}", request.getEmail());
        return patientService.create(user, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Updates patient profile fields by ID.")
    public PatientDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @Valid @RequestBody UpdatePatientRequest request
    ) {
        log.info("Incoming update patient id={}", id);
        return patientService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete patient", description = "Deletes patient record by ID.")
    public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming delete patient id={}", id);
        patientService.delete(user, id);
    }
}

package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.observation.CreateObservationRequest;
import com.pharmatel.backend.dto.observation.ObservationDto;
import com.pharmatel.backend.dto.observation.UpdateObservationRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.ObservationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Observations", description = "Observation and symptom measurement endpoints")
public class ObservationController {

    private final ObservationService observationService;

    @PostMapping("/observations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create observation", description = "Creates an observation linked to session, patient and dose schedule.")
    public ObservationDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreateObservationRequest request
    ) {
        log.info("Incoming create observation patientId={} doseScheduleId={}", request.getPatientId());
        return observationService.create(user, request);
    }

    @GetMapping("/observations")
    @Operation(summary = "List observations", description = "Returns paginated observations.")
    public PageResponse<ObservationDto> list(
        @AuthenticationPrincipal AppUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list observations page={} size={}", page, size);
        return observationService.listAll(user, page, size);
    }

    @GetMapping("/observations/{id}")
    @Operation(summary = "Get observation by ID", description = "Returns a single observation by UUID.")
    public ObservationDto get(@AuthenticationPrincipal AppUserDetails user, @PathVariable UUID id) {
        log.info("Incoming get observation id={}", id);
        return observationService.getById(user, id);
    }

    @GetMapping("/patients/{id}/observations")
    @Operation(summary = "List observations by patient", description = "Returns paginated observations for patient.")
    public PageResponse<ObservationDto> byPatient(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list observations by patientId={} page={} size={}", id, page, size);
        return observationService.listByPatient(user, id, page, size);
    }

    @PutMapping("/observations/{id}")
    @Operation(summary = "Update observation", description = "Updates observation values and optional symptom measurement.")
    public ObservationDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateObservationRequest request
    ) {
        log.info("Incoming update observation id={}", id);
        return observationService.update(user, id, request);
    }

    @DeleteMapping("/observations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete observation", description = "Deletes observation by UUID.")
    public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable UUID id) {
        log.info("Incoming delete observation id={}", id);
        observationService.delete(user, id);
    }
}

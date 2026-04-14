package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.doseschedule.CreateDoseScheduleRequest;
import com.pharmatel.backend.dto.doseschedule.DoseScheduleDto;
import com.pharmatel.backend.dto.doseschedule.TakeDoseRequest;
import com.pharmatel.backend.dto.doseschedule.UpdateDoseScheduleRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.DoseScheduleService;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dose Schedules", description = "Dose schedule tracking endpoints")
public class DoseScheduleController {

    private final DoseScheduleService doseScheduleService;

    @GetMapping("/patients/{id}/dose-schedules")
    @Operation(summary = "List dose schedules by patient", description = "Returns paginated dose schedules for patient.")
    public PageResponse<DoseScheduleDto> byPatient(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list dose schedules by patientId={} page={} size={}", id, page, size);
        return doseScheduleService.listByPatient(user, id, page, size);
    }

    @GetMapping("/dose-schedules")
    @Operation(summary = "List dose schedules", description = "Returns paginated dose schedules.")
    public PageResponse<DoseScheduleDto> list(
        @AuthenticationPrincipal AppUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list all dose schedules page={} size={}", page, size);
        return doseScheduleService.listAll(user, page, size);
    }

    @GetMapping("/dose-schedules/{id}")
    @Operation(summary = "Get dose schedule by ID", description = "Returns dose schedule details by identifier.")
    public DoseScheduleDto get(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming get dose schedule id={}", id);
        return doseScheduleService.getById(user, id);
    }

    @PostMapping("/dose-schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create dose schedule", description = "Creates dose schedule for prescription.")
    public DoseScheduleDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreateDoseScheduleRequest request
    ) {
        log.info("Incoming create dose schedule prescriptionId={}", request.getPrescriptionId());
        return doseScheduleService.create(user, request);
    }

    @PutMapping("/dose-schedules/{id}")
    @Operation(summary = "Update dose schedule", description = "Updates dose schedule fields.")
    public DoseScheduleDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @Valid @RequestBody UpdateDoseScheduleRequest request
    ) {
        log.info("Incoming update dose schedule id={}", id);
        return doseScheduleService.update(user, id, request);
    }

    @PostMapping("/dose-schedules/{id}/take")
    @Operation(summary = "Mark dose taken", description = "Marks a dose schedule as taken and stores optional note.")
    public DoseScheduleDto take(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @RequestBody(required = false) TakeDoseRequest request
    ) {
        log.info("Incoming take dose schedule id={}", id);
        return doseScheduleService.markTaken(user, id, request);
    }

    @DeleteMapping("/dose-schedules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete dose schedule", description = "Soft-deletes a dose schedule.")
    public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming delete dose schedule id={}", id);
        doseScheduleService.delete(user, id);
    }
}

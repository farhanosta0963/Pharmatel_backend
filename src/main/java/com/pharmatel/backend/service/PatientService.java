package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.patient.CreatePatientRequest;
import com.pharmatel.backend.dto.patient.PatientDetailsRequest;
import com.pharmatel.backend.dto.patient.PatientDto;
import com.pharmatel.backend.dto.patient.UpdatePatientRequest;
import com.pharmatel.backend.entity.Account;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.PatientMapper;
import com.pharmatel.backend.repository.AccountRepository;
import com.pharmatel.backend.repository.PatientRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.security.RoleUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final AccountRepository accountRepository;
    private final PatientMapper patientMapper;

    public PageResponse<PatientDto> list(AppUserDetails user, String name, int page, int size) {
        // if (user == null || user.getRole() != AppRole.PHARMACY) {
        //     throw new ForbiddenException("Only pharmacy users can list patients");
        // }
        log.info("Listing patients by user={} page={} size={}", user.getUsername(), page, size);
        PageRequest pr = PageRequest.of(page, size);
        Page<Patient> results = (name == null || name.isBlank())
            ? patientRepository.findAll(pr)
            : patientRepository.findByNameContainingIgnoreCase(name, pr);
        return PageResponse.from(results.map(patientMapper::toDto));
    }

    public PatientDto getById(AppUserDetails user, Integer id) {
        log.info("Get patient id={} by user={}", id, user == null ? null : user.getUsername());
        Patient patient = fetchEntity(id);
        return patientMapper.toDto(patient);
    }

    public PatientDto getByUsername(AppUserDetails user, String username) {
        log.info("Get patient username={} by user={}", username, user == null ? null : user.getUsername());
        Account account = accountRepository.findByUsername(RoleUtil.withPrefix(username, AppRole.PATIENT))
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + username));
        Patient patient = patientRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + username));
        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientDto create(AppUserDetails user, CreatePatientRequest request) {
        // if (user == null || user.getRole() != AppRole.PHARMACY) {
        //     throw new ForbiddenException("Only pharmacy users can create patients");
        // }
        log.info("Creating patient email={} by user={}", request.getEmail(), user.getUsername());
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + request.getAccountId()));
        Patient patient = Patient.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .account(account)
            .build();
        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Transactional
    public PatientDto update(AppUserDetails user, Integer id, UpdatePatientRequest request) {
        log.info("Updating patient id={} by user={}", id, user == null ? null : user.getUsername());
        Patient patient = fetchEntity(id);
        ensureCanAccess(user, patient);
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());
        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Transactional
    public void delete(AppUserDetails user, Integer id) {
        // if (user == null || user.getRole() != AppRole.PHARMACY) {
        //     throw new ForbiddenException("Only pharmacy users can delete patients");
        // }
        log.info("Deleting patient id={} by user={}", id, user.getUsername());
        Patient patient = fetchEntity(id);
        patientRepository.delete(patient);
    }

    private Patient fetchEntity(Integer id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    private void ensureCanAccess(AppUserDetails user, Patient patient) {
        if (user == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (user.getRole() == AppRole.PHARMACY) {
            return;
        }
        if (patient.getAccount() == null || !patient.getAccount().getId().equals(user.getAccountId())) {
            throw new ForbiddenException("You cannot access this patient");
        }
    }


    public PatientDto addDetails(AppUserDetails user,  PatientDetailsRequest request) {
    Patient patient = patientRepository.findByAccountId(user.getAccountId())
        .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + user.getAccountId()));

    if (request.getDateOfBirth() != null) {
        patient.setDateOfBirth(request.getDateOfBirth());
    }
    if (request.getGender() != null) {
        patient.setGender(Patient.Gender.valueOf(request.getGender().toUpperCase()));
    }
    if (request.getHeightCm() != null) {
        patient.setHeightCm(request.getHeightCm());
    }
    if (request.getWeightKg() != null) {
        patient.setWeightKg(request.getWeightKg());
    }
    if (request.getDiagnosis() != null) {
        patient.setDiagnosis(request.getDiagnosis());
    }
    if (request.getAllergies() != null) {
        patient.setAllergies(request.getAllergies());
    }
    if (request.getImageBase64() != null) {
        patient.setImage(Base64.getDecoder().decode(request.getImageBase64()));
    }

    Patient saved = patientRepository.save(patient);
    return patientMapper.toDto(saved);
}

}

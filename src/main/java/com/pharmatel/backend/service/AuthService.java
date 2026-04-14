package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.auth.AuthResponse;
import com.pharmatel.backend.dto.auth.LoginRequest;
import com.pharmatel.backend.dto.auth.RegisterRequest;
import com.pharmatel.backend.entity.Account;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.exception.ApiException;
import com.pharmatel.backend.exception.UnauthorizedException;
import com.pharmatel.backend.repository.AccountRepository;
import com.pharmatel.backend.repository.PatientRepository;
import com.pharmatel.backend.repository.PharmacyRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.security.JwtService;
import com.pharmatel.backend.security.RoleUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final AccountRepository accountRepository;
    private final PatientRepository patientRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws MessagingException {
        log.info("Register request username={} role={}", request.getUsername(), request.getRole());
        AppRole role = request.getRole() == null ? AppRole.PATIENT : request.getRole();
        String storedUsername = RoleUtil.withPrefix(request.getUsername(), role);

        if (accountRepository.existsByUsername(storedUsername)) {
            log.error("Register failed username={} role={} already exists", request.getUsername(), role);
            throw new ApiException("Username already exists for role " + role.name());
        }

        Account account = Account.builder()
            .id(UUID.randomUUID())
            .username(storedUsername)
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
        accountRepository.save(account);

        Integer patientId = null;
        Integer pharmacyId = null;

        if (role == AppRole.PATIENT) {
            Patient patient = Patient.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .account(account)
                .build();
            patient = patientRepository.save(patient);
            patientId = patient.getId();
        } else {
            Point location = null;
            if (request.getLat() != null && request.getLng() != null) {
                location = GEOMETRY_FACTORY.createPoint(new Coordinate(request.getLng(), request.getLat()));
                location.setSRID(4326);
            }
            Pharmacy pharmacy = Pharmacy.builder()
                .name(request.getPharmacyName() == null ? request.getUsername() : request.getPharmacyName())
                .pharmacistName(request.getPharmacistName())
                .location(location)
                .account(account)
                .build();
            pharmacy = pharmacyRepository.save(pharmacy);
            pharmacyId = pharmacy.getId();
        }

        AppUserDetails userDetails = new AppUserDetails(account);
        String token = jwtService.generateToken(userDetails, role);
        log.info("Register success username={} role={} accountId={}", request.getUsername(), role, account.getId());
        sendWelcomeEmail(request);
        return AuthResponse.builder()
            .token(token)
            .accountId(account.getId())
            .username(RoleUtil.stripPrefix(account.getUsername()))
            .role(role)
            .patientId(patientId)
            .pharmacyId(pharmacyId)
            .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt username={} requestedRole={}", request.getUsername(), request.getRole());
        Account account = resolveAccount(request.getUsername(), request.getRole())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            log.error("Login failed for username={}", request.getUsername());
            throw new UnauthorizedException("Invalid credentials");
        }

        AppRole role = RoleUtil.extractRole(account.getUsername());
        AppUserDetails userDetails = new AppUserDetails(account);
        String token = jwtService.generateToken(userDetails, role);
        log.info("Login success username={} role={} accountId={}", request.getUsername(), role, account.getId());

        Integer patientId = role == AppRole.PATIENT
            ? patientRepository.findByAccountId(account.getId()).map(Patient::getId).orElse(null)
            : null;

        Integer pharmacyId = role == AppRole.PHARMACY
            ? pharmacyRepository.findByAccountId(account.getId()).map(Pharmacy::getId).orElse(null)
            : null;

        return AuthResponse.builder()
            .token(token)
            .accountId(account.getId())
            .username(RoleUtil.stripPrefix(account.getUsername()))
            .role(role)
            .patientId(patientId)
            .pharmacyId(pharmacyId)
            .build();
    }

    private Optional<Account> resolveAccount(String username, AppRole requestedRole) {
        if (requestedRole != null) {
            return accountRepository.findByUsername(RoleUtil.withPrefix(username, requestedRole));
        }

        Optional<Account> patient = accountRepository.findByUsername(RoleUtil.withPrefix(username, AppRole.PATIENT));
        if (patient.isPresent()) {
            return patient;
        }
        return accountRepository.findByUsername(RoleUtil.withPrefix(username, AppRole.PHARMACY));
    }


    private void sendWelcomeEmail(RegisterRequest request) throws MessagingException {
        String subject = "Account Welcome to Pharmatel";

        
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Pharmatel!</h2>"
                + "<p style=\"font-size: 16px; color: #555;\">We’re excited to have you join our community. "
                + "At <strong>Pharmatel</strong>, you’ll find the best deals, price comparisons, and a smooth  experience tailored just for you.</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; "
                + "box-shadow: 0 0 10px rgba(0,0,0,0.1); margin-top: 15px;\">"
                + "<h3 style=\"color: #333;\">What’s Next?</h3>"
                + "<ul style=\"font-size: 15px; color: #555;\">"
                + "<li>Explore products and compare prices instantly</li>"
                + "<li>Save your favorite items to your account</li>"
                + "<li>Enjoy exclusive offers and updates</li>"
                + "</ul>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #777; margin-top: 20px;\">Thank you for being part of S3ERHA. "
                + "We’re here to make your shopping easier and smarter!</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        emailService.sendVerificationEmail(request.getEmail(), subject, htmlMessage);


    }
}

package com.pharmatel.backend.security;

public final class RoleUtil {

    private static final String PATIENT_PREFIX = "patient:";
    private static final String PHARMACY_PREFIX = "pharmacy:";

    private RoleUtil() {
    }

    public static String withPrefix(String username, AppRole role) {
        String normalized = username == null ? "" : username.trim();
        return switch (role) {
            case PATIENT -> PATIENT_PREFIX + normalized;
            case PHARMACY -> PHARMACY_PREFIX + normalized;
        };
    }

    public static String stripPrefix(String username) {
        if (username == null) {
            return null;
        }
        if (username.startsWith(PATIENT_PREFIX)) {
            return username.substring(PATIENT_PREFIX.length());
        }
        if (username.startsWith(PHARMACY_PREFIX)) {
            return username.substring(PHARMACY_PREFIX.length());
        }
        return username;
    }

    public static AppRole extractRole(String username) {
        if (username != null && username.startsWith(PHARMACY_PREFIX)) {
            return AppRole.PHARMACY;
        }
        return AppRole.PATIENT;
    }
}

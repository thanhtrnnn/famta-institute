package com.famta.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum QuyenTruyCap {
    ADMIN,
    GIAO_VIEN,
    HOC_VIEN,
    PHU_HUYNH;

    public static Optional<QuyenTruyCap> fromDatabaseValue(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return Arrays.stream(values())
            .filter(role -> role.name().equals(normalized))
            .findFirst();
    }

    public String toDisplayLabel() {
        String lower = name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return lower.substring(0, 1).toUpperCase(Locale.ROOT) + lower.substring(1);
    }
}

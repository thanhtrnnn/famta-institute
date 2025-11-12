package com.famta.service.dto;

/**
 * Lightweight option used by the reports feature when the user needs to select a class.
 */
public record ReportClassOption(String id, String name) {

    @Override
    public String toString() {
        if (name == null || name.isBlank()) {
            return id;
        }
        return name + " (" + id + ")";
    }
}

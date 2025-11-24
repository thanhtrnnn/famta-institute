package com.famta.util;

import com.famta.model.QuyenTruyCap;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class AccountUtils {
    
    public static String generateBaseUsername(String fullName, QuyenTruyCap role) {
        if (fullName == null || fullName.isBlank()) return "";
        
        String prefix = switch (role) {
            case HOC_VIEN -> "hs.";
            case GIAO_VIEN -> "gv.";
            case PHU_HUYNH -> "ngh.";
            default -> "";
        };
        
        String normalized = removeAccents(fullName).toLowerCase().trim();
        String[] parts = normalized.split("\\s+");
        
        if (parts.length == 0) return prefix;
        
        String lastName = parts[parts.length - 1];
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        
        return prefix + lastName + initials.toString();
    }
    
    private static String removeAccents(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
}

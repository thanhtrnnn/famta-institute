package com.famta.util;

import com.famta.model.QuyenTruyCap;
import com.famta.session.UserSession;

public class SecurityContext {
    public static void requireRole(QuyenTruyCap... roles) {
        var account = UserSession.getCurrentAccount().orElseThrow(() -> new SecurityException("Chưa đăng nhập"));
        for (QuyenTruyCap role : roles) {
            if (account.getQuyen() == role) return;
        }
        throw new SecurityException("Không có quyền truy cập");
    }
    
    public static void requireAdmin() {
        requireRole(QuyenTruyCap.ADMIN);
    }
    
    public static boolean hasRole(QuyenTruyCap... roles) {
        var account = UserSession.getCurrentAccount();
        if (account.isEmpty()) return false;
        for (QuyenTruyCap role : roles) {
            if (account.get().getQuyen() == role) return true;
        }
        return false;
    }
}

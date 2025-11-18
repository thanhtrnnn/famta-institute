package com.famta.session;

import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import java.util.Optional;

/**
 * Simple in-memory session holder that keeps the authenticated account
 * available for UI controllers and services during the JavaFX lifecycle.
 */
public final class UserSession {

    private static volatile TaiKhoan currentAccount;

    private UserSession() {
    }

    public static void setCurrentAccount(TaiKhoan account) {
        currentAccount = account;
    }

    public static void clear() {
        currentAccount = null;
    }

    public static Optional<TaiKhoan> getCurrentAccount() {
        return Optional.ofNullable(currentAccount);
    }

    public static boolean hasRole(QuyenTruyCap role) {
        return currentAccount != null && currentAccount.getQuyen() == role;
    }

    public static boolean isAdmin() {
        return hasRole(QuyenTruyCap.ADMIN);
    }
}

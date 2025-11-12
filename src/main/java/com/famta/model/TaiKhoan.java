package com.famta.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class TaiKhoan {
    private final String tenDangNhap;
    private String matKhauHash;
    private QuyenTruyCap quyen;

    public TaiKhoan(String tenDangNhap, String matKhau, QuyenTruyCap quyen) {
        this(tenDangNhap, quyen, hash(Objects.requireNonNull(matKhau, "matKhau")));
    }

    private TaiKhoan(String tenDangNhap, QuyenTruyCap quyen, String matKhauHash) {
        this.tenDangNhap = Objects.requireNonNull(tenDangNhap, "tenDangNhap");
        this.quyen = Objects.requireNonNull(quyen, "quyen");
        this.matKhauHash = Objects.requireNonNull(matKhauHash, "matKhauHash");
    }

    public static TaiKhoan fromHash(String tenDangNhap, String matKhauHash, QuyenTruyCap quyen) {
        return new TaiKhoan(tenDangNhap, quyen, matKhauHash);
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public QuyenTruyCap getQuyen() {
        return quyen;
    }

    public void setQuyen(QuyenTruyCap quyen) {
        this.quyen = Objects.requireNonNull(quyen, "quyen");
    }

    public boolean dangNhap(String matKhau) {
        return Objects.equals(matKhauHash, hash(matKhau));
    }

    public boolean doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (!dangNhap(matKhauCu)) {
            return false;
        }
        setMatKhau(matKhauMoi);
        return true;
    }

    public boolean kiemTraQuyen(QuyenTruyCap quyenCanKiemTra) {
        return quyen == quyenCanKiemTra;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    private void setMatKhau(String matKhauMoi) {
        this.matKhauHash = hash(Objects.requireNonNull(matKhauMoi, "matKhauMoi"));
    }

    public static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}

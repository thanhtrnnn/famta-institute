package com.famta.service;

import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import java.util.List;

/**
 * Administrative operations for managing application login accounts.
 */
public interface AccountAdminService {

    List<TaiKhoan> findAll();

    void createAccount(String username, String plainPassword, QuyenTruyCap role);

    void updateRole(String username, QuyenTruyCap role);

    void resetPassword(String username, String plainPassword);

    void deleteAccount(String username);
}

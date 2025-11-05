package com.famta.service;

import com.famta.service.impl.InMemoryHocVienService;

/**
 * Basic service locator to supply feature modules with shared service instances.
 */
public final class ServiceProvider {

    private static final HocVienService HOC_VIEN_SERVICE = createHocVienService();

    private ServiceProvider() {
    }

    public static HocVienService getHocVienService() {
        return HOC_VIEN_SERVICE;
    }

    private static HocVienService createHocVienService() {
        try {
            return new JdbcHocVienService();
        } catch (Exception ex) {
            System.err.println("Khởi tạo JdbcHocVienService thất bại, sẽ dùng dữ liệu mẫu: " + ex.getMessage());
            return new InMemoryHocVienService();
        }
    }
}

package com.famta.service.impl;

import com.famta.model.HocSinh;
import com.famta.service.HocVienService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation to provide demo data for the UI layer.
 */
public class InMemoryHocVienService implements HocVienService {

    private final Map<String, HocSinh> hocSinhStore = new ConcurrentHashMap<>();

    public InMemoryHocVienService() {
        seedDemoData();
    }

    @Override
    public List<HocSinh> getAllHocVien() {
        return hocSinhStore.values().stream()
            .sorted(Comparator.comparing(HocSinh::getMaHocSinh))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public HocSinh getHocVienById(String maHocVien) {
        return hocSinhStore.get(maHocVien);
    }

    @Override
    public boolean addHocVien(HocSinh hocVien) {
        Objects.requireNonNull(hocVien, "hocVien");
        return hocSinhStore.putIfAbsent(hocVien.getMaHocSinh(), hocVien) == null;
    }

    @Override
    public boolean updateHocVien(HocSinh hocVien) {
        Objects.requireNonNull(hocVien, "hocVien");
        if (!hocSinhStore.containsKey(hocVien.getMaHocSinh())) {
            return false;
        }
        hocSinhStore.put(hocVien.getMaHocSinh(), hocVien);
        return true;
    }

    @Override
    public boolean deleteHocVien(String maHocVien) {
        return hocSinhStore.remove(maHocVien) != null;
    }

    @Override
    public List<HocSinh> searchHocVienByName(String hoTen) {
        if (hoTen == null || hoTen.isBlank()) {
            return getAllHocVien();
        }
        String keyword = hoTen.toLowerCase(Locale.getDefault());
        return hocSinhStore.values().stream()
            .filter(hocSinh -> hocSinh.getHoTenDayDu().toLowerCase(Locale.getDefault()).contains(keyword))
            .sorted(Comparator.comparing(HocSinh::getHoTenDayDu))
            .collect(Collectors.toList());
    }

    private void seedDemoData() {
        List<HocSinh> demo = new ArrayList<>();
        demo.add(new HocSinh("HV001", "Nguyễn", "Văn", "An", LocalDate.of(2010, 3, 12), "Nam", LocalDate.of(2022, 9, 5)));
        demo.add(new HocSinh("HV002", "Trần", "Thị", "Bích", LocalDate.of(2011, 7, 21), "Nữ", LocalDate.of(2023, 9, 4)));
        demo.add(new HocSinh("HV003", "Lê", "Hoàng", "Cường", LocalDate.of(2009, 11, 2), "Nam", LocalDate.of(2021, 9, 6)));
        demo.add(new HocSinh("HV004", "Phạm", "Mai", "Duyên", LocalDate.of(2010, 1, 30), "Nữ", LocalDate.of(2022, 9, 5)));
        demo.add(new HocSinh("HV005", "Vũ", "Quang", "Hải", LocalDate.of(2009, 5, 18), "Nam", LocalDate.of(2021, 9, 6)));
        demo.add(new HocSinh("HV006", "Đào", "Thu", "Hằng", LocalDate.of(2012, 9, 9), "Nữ", LocalDate.of(2024, 9, 2)));
        demo.add(new HocSinh("HV007", "Phan", "Đức", "Khánh", LocalDate.of(2011, 12, 15), "Nam", LocalDate.of(2023, 9, 4)));
        demo.add(new HocSinh("HV008", "Đinh", "Phương", "Lan", LocalDate.of(2012, 4, 3), "Nữ", LocalDate.of(2024, 9, 2)));
        demo.add(new HocSinh("HV009", "Bùi", "Anh", "Minh", LocalDate.of(2010, 8, 27), "Nam", LocalDate.of(2022, 9, 5)));
        demo.add(new HocSinh("HV010", "Hoàng", "Thanh", "Ngọc", LocalDate.of(2011, 2, 8), "Nữ", LocalDate.of(2023, 9, 4)));

        for (HocSinh hocSinh : demo) {
            hocSinhStore.put(hocSinh.getMaHocSinh(), hocSinh);
        }
    }
}

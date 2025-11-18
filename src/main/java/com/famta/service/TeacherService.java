package com.famta.service;

import com.famta.model.GiaoVien;
import java.util.List;

public interface TeacherService {
    List<GiaoVien> findAll();

    boolean addTeacher(GiaoVien giaoVien);

    boolean updateTeacher(GiaoVien giaoVien);

    boolean deleteTeacher(String maGiaoVien);
}

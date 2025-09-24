package com.famta.service;

import com.famta.model.HocVien;
import java.util.List;

/**
 * Service interface for managing students (HocVien)
 */
public interface HocVienService {
    
    /**
     * Get all students
     * @return List of all students
     */
    List<HocVien> getAllHocVien();
    
    /**
     * Get student by ID
     * @param maHocVien Student ID
     * @return Student object or null if not found
     */
    HocVien getHocVienById(String maHocVien);
    
    /**
     * Add new student
     * @param hocVien Student to add
     * @return true if successful, false otherwise
     */
    boolean addHocVien(HocVien hocVien);
    
    /**
     * Update existing student
     * @param hocVien Student to update
     * @return true if successful, false otherwise
     */
    boolean updateHocVien(HocVien hocVien);
    
    /**
     * Delete student
     * @param maHocVien Student ID to delete
     * @return true if successful, false otherwise
     */
    boolean deleteHocVien(String maHocVien);
    
    /**
     * Search students by name
     * @param hoTen Name to search for
     * @return List of matching students
     */
    List<HocVien> searchHocVienByName(String hoTen);
}
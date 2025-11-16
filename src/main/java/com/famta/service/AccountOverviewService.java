package com.famta.service;

import com.famta.service.dto.AdminClassOverview;
import com.famta.service.dto.GuardianContactView;
import com.famta.service.dto.StudentClassEnrollment;
import java.util.List;

/**
 * Provides consolidated projections that power the account management screen.
 */
public interface AccountOverviewService {

    List<AdminClassOverview> fetchAdminClassOverview();

    List<StudentClassEnrollment> fetchStudentClassEnrollments();

    List<GuardianContactView> fetchGuardianContacts();
}

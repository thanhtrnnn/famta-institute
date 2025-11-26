package com.famta.controller;

import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import com.famta.service.JdbcTimetableService;
import com.famta.service.dto.TimetableEntry;
import com.famta.session.UserSession;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TimetableController {

    @FXML
    private GridPane timetableGrid;

    private final JdbcTimetableService timetableService = new JdbcTimetableService();

    @FXML
    private void initialize() {
        setupGrid();
        loadTimetable();
    }

    private void setupGrid() {
        // Headers
        String[] days = {"", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
        for (int i = 0; i < days.length; i++) {
            Label label = new Label(days[i]);
            label.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            GridPane.setHalignment(label, HPos.CENTER);
            timetableGrid.add(label, i, 0);
        }

        // Periods (1-10)
        for (int i = 1; i <= 10; i++) {
            Label label = new Label("Tiết " + i);
            label.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            GridPane.setHalignment(label, HPos.CENTER);
            timetableGrid.add(label, 0, i);
        }
    }

    private void loadTimetable() {
        TaiKhoan account = UserSession.getCurrentAccount().orElse(null);
        if (account == null) return;

        List<TimetableEntry> entries = List.of();
        if (account.getQuyen() == QuyenTruyCap.HOC_VIEN) {
            entries = timetableService.fetchStudentTimetable(account.getTenDangNhap());
        } else if (account.getQuyen() == QuyenTruyCap.GIAO_VIEN) {
            entries = timetableService.fetchTeacherTimetable(account.getTenDangNhap());
        } else if (account.getQuyen() == QuyenTruyCap.PHU_HUYNH) {
            entries = timetableService.fetchGuardianTimetable(account.getTenDangNhap());
        }

        for (TimetableEntry entry : entries) {
            addEntryToGrid(entry);
        }
    }

    private void addEntryToGrid(TimetableEntry entry) {
        int startRow = parsePeriod(entry.startPeriod());
        int endRow = parsePeriod(entry.endPeriod());
        int col = entry.dayOfWeek() - 1; // Mon (2) -> 1

        if (startRow > 0 && col > 0) {
            VBox card = new VBox(2);
            card.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 3; -fx-padding: 5;");
            
            Label subject = new Label(entry.subjectName());
            subject.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
            subject.setWrapText(true);
            
            card.getChildren().add(subject);
            
            if (entry.studentName() != null && !entry.studentName().isBlank()) {
                Label student = new Label(entry.studentName());
                student.setStyle("-fx-font-size: 9px; -fx-text-fill: #555;");
                student.setWrapText(true);
                card.getChildren().add(student);
            }
            
            Label room = new Label(entry.room());
            room.setStyle("-fx-font-size: 9px;");
            card.getChildren().add(room);
            
            int rowSpan = endRow - startRow + 1;
            if (rowSpan < 1) rowSpan = 1;
            
            timetableGrid.add(card, col, startRow, 1, rowSpan);
        }
    }
    
    private int parsePeriod(String periodName) {
        if (periodName == null) return 0;
        try {
            // "Tiết 1" -> 1
            return Integer.parseInt(periodName.replace("Tiết ", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

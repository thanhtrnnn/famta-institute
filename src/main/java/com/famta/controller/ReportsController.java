package com.famta.controller;

import com.famta.service.JdbcReportService;
import com.famta.service.dto.ReportSummary;
import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

/**
 * Controller that powers the reports dashboard view.
 */
public class ReportsController {

    @FXML
    private ComboBox<String> reportCombo;

    @FXML
    private ComboBox<String> unitCombo;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Label studentValue;

    @FXML
    private Label averageScoreValue;

    @FXML
    private Label teacherValue;

    @FXML
    private Label chartDescription;

    private final JdbcReportService reportService = new JdbcReportService();

    @FXML
    private void initialize() {
        setupFilters();
        refreshOverview();
    }

    @FXML
    private void handleRefresh() {
        refreshOverview();
    }

    private void setupFilters() {
        reportCombo.getItems().setAll("Tổng quan", "Chuyên cần", "Kết quả học tập");
        reportCombo.getSelectionModel().selectFirst();

        unitCombo.getItems().setAll("Toàn trung tâm", "Theo khối", "Theo lớp");
        unitCombo.getSelectionModel().selectFirst();

        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
    }

    private void refreshOverview() {
        try {
            ReportSummary summary = reportService.loadOverview(fromDatePicker.getValue(), toDatePicker.getValue());
            studentValue.setText(Integer.toString(summary.totalStudents()));
            averageScoreValue.setText(summary.formattedAverageScore());
            teacherValue.setText(Integer.toString(summary.totalTeachers()));
            updateChartDescription(summary.highlights(), summary.totalClasses());
        } catch (Exception ex) {
            studentValue.setText("--");
            averageScoreValue.setText("--");
            teacherValue.setText("--");
            chartDescription.setText("Không thể tải dữ liệu báo cáo: " + ex.getMessage());
        }
    }

    private void updateChartDescription(List<String> highlights, int totalClasses) {
        if (highlights == null || highlights.isEmpty()) {
            chartDescription.setText("Chưa có dữ liệu điểm số để hiển thị biểu đồ.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Top lớp học theo điểm trung bình (trên ").append(totalClasses).append(" lớp):\n");
        for (int i = 0; i < highlights.size(); i++) {
            builder.append(i + 1).append(". ").append(highlights.get(i)).append('\n');
        }
        chartDescription.setText(builder.toString().trim());
    }
}

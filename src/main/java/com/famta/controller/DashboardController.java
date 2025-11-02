package com.famta.controller;

import com.famta.service.JdbcDashboardService;
import com.famta.service.dto.DashboardSummary;
import com.famta.service.dto.ScheduleItem;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Populates the dashboard screen with live statistics and schedule snippets.
 */
public class DashboardController {

    private static final DateTimeFormatter META_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM");

    @FXML
    private Label totalStudentsValue;

    @FXML
    private Label studentsMetaLabel;

    @FXML
    private Label activeTeachersValue;

    @FXML
    private Label teachersMetaLabel;

    @FXML
    private Label runningClassesValue;

    @FXML
    private Label classesMetaLabel;

    @FXML
    private ListView<String> announcementsList;

    @FXML
    private ListView<String> scheduleList;

    private final JdbcDashboardService dashboardService = new JdbcDashboardService();

    @FXML
    private void initialize() {
        announcementsList.setItems(FXCollections.observableArrayList());
        scheduleList.setItems(FXCollections.observableArrayList());
        loadDashboard();
    }

    private void loadDashboard() {
        Platform.runLater(() -> {
            try {
                DashboardSummary summary = dashboardService.loadDashboard();
                updateSummaryCards(summary);
                updateAnnouncements(summary.announcements());
                updateSchedule(summary.scheduleItems());
            } catch (Exception ex) {
                showErrorState(ex.getMessage());
            }
        });
    }

    private void updateSummaryCards(DashboardSummary summary) {
        totalStudentsValue.setText(Integer.toString(summary.totalStudents()));
        activeTeachersValue.setText(Integer.toString(summary.activeTeachers()));
        runningClassesValue.setText(Integer.toString(summary.runningClasses()));

        String timestamp = summary.lastUpdated().format(META_FORMATTER);
        studentsMetaLabel.setText("Cập nhật " + timestamp);
        teachersMetaLabel.setText("Bao gồm GV nội bộ và thỉnh giảng");
        classesMetaLabel.setText("Tổng " + summary.runningClasses() + " lớp tuần này");
    }

    private void updateAnnouncements(List<String> announcements) {
        if (announcements == null || announcements.isEmpty()) {
            announcementsList.getItems().setAll("Chưa có thông báo nào");
            return;
        }
        announcementsList.getItems().setAll(announcements);
    }

    private void updateSchedule(List<ScheduleItem> items) {
        if (items == null || items.isEmpty()) {
            scheduleList.getItems().setAll("Không có lịch giảng dạy phù hợp");
            return;
        }
        scheduleList.getItems().setAll(items.stream()
            .map(item -> item.className() + " - " + item.teacherName() + "\n" + item.timeRange() + " | Phòng " + item.room())
            .toList());
    }

    private void showErrorState(String message) {
        totalStudentsValue.setText("--");
        activeTeachersValue.setText("--");
        runningClassesValue.setText("--");
        String safeMessage = message == null ? "Không thể tải dữ liệu bảng điều khiển" : message;
        studentsMetaLabel.setText(safeMessage);
        teachersMetaLabel.setText(safeMessage);
        classesMetaLabel.setText(safeMessage);
        announcementsList.getItems().setAll("Không thể tải thông báo");
        scheduleList.getItems().setAll("Không thể tải lịch học");
    }
}

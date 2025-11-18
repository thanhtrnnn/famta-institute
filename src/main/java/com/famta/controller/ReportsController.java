package com.famta.controller;

import com.famta.service.JdbcReportDocumentService;
import com.famta.service.JdbcReportService;
import com.famta.service.dto.ReportClassOption;
import com.famta.service.dto.ReportSummary;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.StringConverter;

/**
 * Controller that powers the reports dashboard view.
 */
public class ReportsController {

    @FXML
    private ComboBox<ReportType> reportCombo;

    @FXML
    private ComboBox<ReportClassOption> classCombo;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TextField thresholdField;

    @FXML
    private Label studentValue;

    @FXML
    private Label averageScoreValue;

    @FXML
    private Label teacherValue;

    @FXML
    private Label chartDescription;

    @FXML
    private Button generateButton;

    @FXML
    private Button copyButton;

    @FXML
    private Label reportStatusLabel;

    @FXML
    private TextArea reportPreview;

    private final JdbcReportService reportService = new JdbcReportService();
    private final JdbcReportDocumentService documentService = new JdbcReportDocumentService();

    @FXML
    private void initialize() {
        setupFilters();
        loadClassOptions();
        refreshOverview();
    }

    @FXML
    private void handleRefresh() {
        refreshOverview();
    }

    @FXML
    private void handleGenerate() {
        ReportType type = reportCombo.getValue();
        if (type == null) {
            setReportStatus("Vui lòng chọn loại báo cáo", true);
            return;
        }
        try {
            String content = switch (type) {
                case STUDENT_ROSTER -> documentService.buildStudentRosterReport();
                case CLASS_SCORE -> {
                    ReportClassOption option = classCombo.getValue();
                    if (option == null) {
                        throw new IllegalArgumentException("Vui lòng chọn lớp cần xuất báo cáo");
                    }
                    yield documentService.buildClassScoreReport(option.id());
                }
                case NEW_STUDENTS -> documentService.buildNewStudentReport(fromDatePicker.getValue(), toDatePicker.getValue());
                case EXCELLENT_STUDENTS -> documentService.buildExcellentStudentReport(parseThreshold());
            };
            reportPreview.setText(content);
            copyButton.setDisable(content == null || content.isBlank());
            setReportStatus("Đã tạo báo cáo: " + type.displayName, false);
        } catch (Exception ex) {
            reportPreview.clear();
            copyButton.setDisable(true);
            setReportStatus("Không thể tạo báo cáo: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void handleCopy() {
        String content = reportPreview.getText();
        if (content == null || content.isBlank()) {
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent data = new ClipboardContent();
        data.putString(content);
        clipboard.setContent(data);
        setReportStatus("Đã sao chép nội dung báo cáo vào clipboard", false);
    }

    private void setupFilters() {
        reportCombo.setItems(FXCollections.observableArrayList(ReportType.values()));
        reportCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(ReportType type) {
                return type == null ? "" : type.displayName;
            }

            @Override
            public ReportType fromString(String string) {
                for (ReportType type : ReportType.values()) {
                    if (type.displayName.equals(string)) {
                        return type;
                    }
                }
                return ReportType.STUDENT_ROSTER;
            }
        });
        reportCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ReportType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.displayName);
            }
        });
        reportCombo.valueProperty().addListener((obs, oldValue, newValue) -> toggleDynamicFilters(newValue));
        reportCombo.getSelectionModel().select(ReportType.STUDENT_ROSTER);

        thresholdField.setText("9.0");
        toggleDynamicFilters(reportCombo.getValue());

        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
    }

    private void loadClassOptions() {
        try {
            List<ReportClassOption> classes = documentService.listClasses();
            classCombo.setItems(FXCollections.observableArrayList(classes));
        } catch (Exception ex) {
            classCombo.setPromptText("Không thể tải lớp học");
            setReportStatus("Không thể tải danh sách lớp: " + ex.getMessage(), true);
        }
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

    private void toggleDynamicFilters(ReportType type) {
        if (type == null) {
            return;
        }
        toggleControl(classCombo, type.requiresClassSelection);
        toggleControl(thresholdField, type.requiresThreshold);
        if (!type.requiresClassSelection) {
            classCombo.getSelectionModel().clearSelection();
        }
    }

    private void toggleControl(javafx.scene.Node node, boolean enable) {
        node.setManaged(enable);
        node.setVisible(enable);
        node.setDisable(!enable);
    }

    private double parseThreshold() {
        try {
            String text = thresholdField.getText();
            if (text == null || text.isBlank()) {
                return 9.0;
            }
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Điểm chuẩn không hợp lệ");
        }
    }

    private void setReportStatus(String message, boolean error) {
        if (reportStatusLabel == null) {
            return;
        }
        List<String> classes = reportStatusLabel.getStyleClass();
        classes.removeAll(List.of("success-message", "error-message"));
        if (error) {
            if (!classes.contains("error-message")) {
                classes.add("error-message");
            }
        } else {
            if (!classes.contains("success-message")) {
                classes.add("success-message");
            }
        }
        reportStatusLabel.setText(message);
    }

    private enum ReportType {
        STUDENT_ROSTER("Danh sách học viên", false, false),
        CLASS_SCORE("Bảng điểm theo lớp", true, false),
        NEW_STUDENTS("Học viên mới", false, false),
        EXCELLENT_STUDENTS("Học viên xuất sắc", false, true);

        private final String displayName;
        private final boolean requiresClassSelection;
        private final boolean requiresThreshold;

        ReportType(String displayName, boolean requiresClassSelection, boolean requiresThreshold) {
            this.displayName = displayName;
            this.requiresClassSelection = requiresClassSelection;
            this.requiresThreshold = requiresThreshold;
        }
    }
}

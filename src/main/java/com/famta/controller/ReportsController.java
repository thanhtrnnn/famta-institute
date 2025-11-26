package com.famta.controller;

import com.famta.model.HocKy;
import com.famta.model.NamHoc;
import com.famta.service.JdbcCatalogService;
import com.famta.service.JdbcReportDocumentService;
import com.famta.service.JdbcReportService;
import com.famta.service.dto.ReportClassOption;
import com.famta.service.dto.ReportSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Controller that powers the reports dashboard view.
 */
public class ReportsController {

    @FXML
    private ComboBox<ReportType> reportCombo;

    @FXML
    private ComboBox<NamHoc> yearFilter;

    @FXML
    private ComboBox<HocKy> semesterFilter;

    @FXML
    private ComboBox<ReportClassOption> classCombo;

    @FXML
    private TextField thresholdField;

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
    private StackPane chartContainer;

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
    private final JdbcCatalogService catalogService = new JdbcCatalogService();

    @FXML
    private void initialize() {
        setupFilters();
        loadYears();
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
            
            generateChart(type);
        } catch (Exception ex) {
            reportPreview.clear();
            copyButton.setDisable(true);
            setReportStatus("Không thể tạo báo cáo: " + ex.getMessage(), true);
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(new Label("Không thể tạo biểu đồ: " + ex.getMessage()));
        }
    }

    private void generateChart(ReportType type) {
        chartContainer.getChildren().clear();
        
        switch (type) {
            case STUDENT_ROSTER -> {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Lớp học");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Số lượng học sinh");
                yAxis.setTickLabelFormatter(new IntegerStringConverter());
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Phân bố học sinh theo lớp");
                barChart.setLegendVisible(false);
                
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                Map<String, Integer> data = reportService.getStudentsPerClass();
                data.forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
                
                barChart.getData().add(series);
                chartContainer.getChildren().add(barChart);
            }
            case CLASS_SCORE -> {
                ReportClassOption option = classCombo.getValue();
                if (option != null) {
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Phân loại điểm");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Số lượng");
                    yAxis.setTickUnit(1);
                    yAxis.setMinorTickVisible(false);
                    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                    barChart.setTitle("Phổ điểm lớp " + option.name());
                    barChart.setLegendVisible(false);
                    
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    Map<String, Integer> data = reportService.getScoreDistribution(option.id());
                    data.forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
                    
                    barChart.getData().add(series);
                    chartContainer.getChildren().add(barChart);
                }
            }
            case NEW_STUDENTS -> {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Tháng");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Số lượng nhập học");
                yAxis.setTickUnit(1);
                yAxis.setMinorTickVisible(false);
                LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Xu hướng nhập học");
                lineChart.setLegendVisible(false);
                
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                Map<String, Integer> data = reportService.getNewStudentsTrend(fromDatePicker.getValue(), toDatePicker.getValue());
                data.forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
                
                lineChart.getData().add(series);
                chartContainer.getChildren().add(lineChart);
            }
            case EXCELLENT_STUDENTS -> {
                double threshold = parseThreshold();
                Map<String, Integer> data = reportService.getExcellentRatio(threshold);
                PieChart pieChart = new PieChart();
                pieChart.setTitle("Tỷ lệ học sinh xuất sắc (>= " + threshold + ")");
                
                data.forEach((k, v) -> pieChart.getData().add(new PieChart.Data(k + " (" + v + ")", v)));
                chartContainer.getChildren().add(pieChart);
            }
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

        yearFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(NamHoc object) {
                return object == null ? "" : object.getTenNamHoc();
            }
            @Override
            public NamHoc fromString(String string) { return null; }
        });
        yearFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadSemesters(newVal));

        semesterFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(HocKy object) {
                return object == null ? "" : "Học kỳ " + object.getThuTuKy();
            }
            @Override
            public HocKy fromString(String string) { return null; }
        });
        semesterFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadClassOptions());

        thresholdField.setText("9.0");
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
        toggleDynamicFilters(reportCombo.getValue());
    }

    public class IntegerStringConverter extends StringConverter<Number> {
        @Override
        public String toString(Number object) {
            if (object.intValue() != object.doubleValue()) {
                return "";
            }
            return String.valueOf(object.intValue());
        }

        @Override
        public Number fromString(String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private void loadYears() {
        try {
            List<NamHoc> years = catalogService.getAllNamHoc();
            yearFilter.setItems(FXCollections.observableArrayList(years));
            if (!years.isEmpty()) {
                yearFilter.getSelectionModel().selectFirst();
            }
        } catch (Exception ex) {
            setReportStatus("Không thể tải danh sách năm học: " + ex.getMessage(), true);
        }
    }

    private void loadSemesters(NamHoc year) {
        if (year == null) {
            semesterFilter.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            List<HocKy> semesters = catalogService.getHocKyByNamHoc(year.getMaNamHoc());
            semesterFilter.setItems(FXCollections.observableArrayList(semesters));
            if (!semesters.isEmpty()) {
                semesterFilter.getSelectionModel().selectFirst();
            } else {
                loadClassOptions();
            }
        } catch (Exception ex) {
            setReportStatus("Không thể tải danh sách học kỳ: " + ex.getMessage(), true);
        }
    }

    private void loadClassOptions() {
        HocKy semester = semesterFilter.getValue();
        if (semester == null) {
            classCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            List<ReportClassOption> classes = documentService.listClasses(semester.getMaHocKy());
            classCombo.setItems(FXCollections.observableArrayList(classes));
        } catch (Exception ex) {
            classCombo.setPromptText("Không thể tải lớp học");
            setReportStatus("Không thể tải danh sách lớp: " + ex.getMessage(), true);
        }
    }

    private void refreshOverview() {
        try {
            ReportSummary summary = reportService.loadOverview(null, null);
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
        toggleControl(yearFilter, type.requiresClassSelection);
        toggleControl(semesterFilter, type.requiresClassSelection);
        toggleControl(thresholdField, type.requiresThreshold);
        toggleControl(fromDatePicker, type.requiresDateSelection);
        toggleControl(toDatePicker, type.requiresDateSelection);
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
        STUDENT_ROSTER("Danh sách học sinh", false, false, false),
        CLASS_SCORE("Bảng điểm theo lớp", true, false, false),
        NEW_STUDENTS("Học sinh mới", false, false, true),
        EXCELLENT_STUDENTS("Học sinh xuất sắc", false, true, false);

        private final String displayName;
        private final boolean requiresClassSelection;
        private final boolean requiresThreshold;
        private final boolean requiresDateSelection;

        ReportType(String displayName, boolean requiresClassSelection, boolean requiresThreshold, boolean requiresDateSelection) {
            this.displayName = displayName;
            this.requiresClassSelection = requiresClassSelection;
            this.requiresThreshold = requiresThreshold;
            this.requiresDateSelection = requiresDateSelection;
        }
    }
}

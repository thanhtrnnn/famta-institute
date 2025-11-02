package com.famta.controller;

import com.famta.service.JdbcClassService;
import com.famta.service.dto.ClassSummary;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller backing the class management screen.
 */
public class ClassManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> yearFilter;

    @FXML
    private ComboBox<String> gradeFilter;

    @FXML
    private ComboBox<String> teacherFilter;

    @FXML
    private TableView<ClassSummary> classTable;

    @FXML
    private TableColumn<ClassSummary, String> codeColumn;

    @FXML
    private TableColumn<ClassSummary, String> nameColumn;

    @FXML
    private TableColumn<ClassSummary, String> teacherColumn;

    @FXML
    private TableColumn<ClassSummary, String> gradeColumn;

    @FXML
    private TableColumn<ClassSummary, Number> sizeColumn;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label summaryLabel;

    private final JdbcClassService classService = new JdbcClassService();
    private final ObservableList<ClassSummary> masterData = FXCollections.observableArrayList();
    private FilteredList<ClassSummary> filteredData;

    @FXML
    private void initialize() {
        configureTable();
        configureFilters();
        bindSelection();
        loadClasses();
    }

    @FXML
    private void handleRefresh() {
        loadClasses();
    }

    private void configureTable() {
        codeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().maLopHoc()));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().lopVaMon()));
        teacherColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().giaoVienDisplay()));
        gradeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().khoiDisplay()));
        sizeColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().siSo()));

        classTable.setPlaceholder(createPlaceholder("Không tìm thấy lớp học phù hợp"));

        filteredData = new FilteredList<>(masterData, item -> true);
        SortedList<ClassSummary> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(classTable.comparatorProperty());
        classTable.setItems(sortedData);
    }

    private void configureFilters() {
        yearFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        yearFilter.getSelectionModel().selectFirst();

        gradeFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        gradeFilter.getSelectionModel().selectFirst();

        teacherFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        teacherFilter.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        yearFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        gradeFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        teacherFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void bindSelection() {
        classTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                notesArea.setText("Chọn lớp để xem chi tiết lịch học.");
            } else {
                notesArea.setText(buildDetails(newSelection));
            }
        });
        notesArea.setText("Chọn lớp để xem chi tiết lịch học.");
    }

    private void loadClasses() {
        try {
            List<ClassSummary> classes = classService.fetchClassSummaries();
            masterData.setAll(classes);
            rebuildFilterOptions();
            applyFilters();
            summaryLabel.setText("Đang hiển thị " + filteredData.size() + " lớp học");
        } catch (Exception ex) {
            masterData.clear();
            summaryLabel.setText("Không thể tải dữ liệu lớp học: " + ex.getMessage());
        }
    }

    private void rebuildFilterOptions() {
        updateCombo(yearFilter, masterData.stream()
            .map(ClassSummary::namHoc)
            .filter(Objects::nonNull)
            .filter(value -> !value.isBlank())
            .collect(Collectors.toSet()));

        updateCombo(gradeFilter, masterData.stream()
            .map(ClassSummary::khoiDisplay)
            .filter(value -> !value.equals("N/A"))
            .collect(Collectors.toSet()));

        updateCombo(teacherFilter, masterData.stream()
            .map(ClassSummary::giaoVienDisplay)
            .filter(value -> !value.equals("Chưa phân công"))
            .collect(Collectors.toSet()));
    }

    private void updateCombo(ComboBox<String> comboBox, Set<String> rawValues) {
        String previous = comboBox.getSelectionModel().getSelectedItem();
        List<String> options = rawValues.stream()
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Tất cả");
        items.addAll(options);
        comboBox.setItems(items);
        if (previous != null && items.contains(previous)) {
            comboBox.getSelectionModel().select(previous);
        } else {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private void applyFilters() {
        String keyword = normalize(searchField.getText());
        String selectedYear = yearFilter.getSelectionModel().getSelectedItem();
        String selectedGrade = gradeFilter.getSelectionModel().getSelectedItem();
        String selectedTeacher = teacherFilter.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(summary -> matchesKeyword(summary, keyword)
            && matchesOption(summary.namHoc(), selectedYear)
            && matchesOption(summary.khoiDisplay(), selectedGrade)
            && matchesOption(summary.giaoVienDisplay(), selectedTeacher));

        summaryLabel.setText("Đang hiển thị " + filteredData.size() + " lớp học");
    }

    private boolean matchesKeyword(ClassSummary summary, String keyword) {
        if (keyword.isEmpty()) {
            return true;
        }
        return containsIgnoreCase(summary.maLopHoc(), keyword)
            || containsIgnoreCase(summary.tenLopHoc(), keyword)
            || containsIgnoreCase(summary.giaoVienDisplay(), keyword)
            || containsIgnoreCase(summary.monHoc(), keyword);
    }

    private boolean matchesOption(String value, String selection) {
        if (selection == null || "Tất cả".equalsIgnoreCase(selection)) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return selection.equalsIgnoreCase(value.trim());
    }

    private String buildDetails(ClassSummary summary) {
        StringBuilder builder = new StringBuilder();
        builder.append("Lớp: ").append(summary.tenLopHoc()).append(" (Mã: ").append(summary.maLopHoc()).append(")\n");
        builder.append("Môn học: ").append(optional(summary.monHoc())).append('\n');
        builder.append("Giáo viên phụ trách: ").append(summary.giaoVienDisplay()).append('\n');
        builder.append("Năm học: ").append(optional(summary.namHoc())).append(" | Khối: ").append(summary.khoiDisplay()).append('\n');
        builder.append("Thời gian: ").append(optional(summary.tietBatDau()))
            .append(" - ").append(optional(summary.tietKetThuc())).append('\n');
        builder.append("Phòng học: ").append(optional(summary.phongHoc())).append('\n');
        builder.append("Sĩ số: ").append(summary.siSo());
        return builder.toString();
    }

    private String optional(String value) {
        return value == null || value.isBlank() ? "Không xác định" : value;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        if (source == null) {
            return false;
        }
        return source.toLowerCase().contains(keyword);
    }

    private Label createPlaceholder(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("info-text");
        return label;
    }
}

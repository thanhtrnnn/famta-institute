package com.famta.controller;

import com.famta.model.HocSinh;
import com.famta.service.HocVienService;
import com.famta.service.ServiceProvider;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

public class StudentManagementController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final HocVienService hocVienService = ServiceProvider.getHocVienService();

    private final ObservableList<HocSinh> masterData = FXCollections.observableArrayList();
    private FilteredList<HocSinh> filteredData;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> genderFilter;

    @FXML
    private ComboBox<String> enrollmentYearFilter;

    @FXML
    private Button addStudentButton;

    @FXML
    private Button resetButton;

    @FXML
    private TableView<HocSinh> studentTable;

    @FXML
    private TableColumn<HocSinh, String> idColumn;

    @FXML
    private TableColumn<HocSinh, String> fullNameColumn;

    @FXML
    private TableColumn<HocSinh, String> genderColumn;

    @FXML
    private TableColumn<HocSinh, String> birthDateColumn;

    @FXML
    private TableColumn<HocSinh, String> enrollDateColumn;

    @FXML
    private Label resultsSummary;

    @FXML
    private Label placeholderMessage;

    @FXML
    private void initialize() {
        configureTable();
        loadData();
        configureFilters();
        hookListeners();
        placeholderMessage.setVisible(false);
        applyFilters();
    }

    private void configureTable() {
        // Use PropertyValueFactory for static columns that map directly to model methods.
    idColumn.setCellValueFactory(new PropertyValueFactory<>("maHocSinh"));
    fullNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(safeText(cellData.getValue().getHoTenDayDu())));
    genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(safeText(cellData.getValue().getGioiTinh())));
    birthDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getNgaySinh())));
    enrollDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getNgayNhapHoc())));

        studentTable.setPlaceholder(createTablePlaceholder());
    }

    private void loadData() {
        try {
            masterData.setAll(hocVienService.getAllHocVien());
            placeholderMessage.setVisible(false);
        } catch (Exception ex) {
            masterData.clear();
            placeholderMessage.setText("Không thể tải dữ liệu học viên. Vui lòng kiểm tra kết nối cơ sở dữ liệu.");
            placeholderMessage.setVisible(true);
        }
        filteredData = new FilteredList<>(masterData, hocSinh -> true);
        SortedList<HocSinh> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);
    }

    private void configureFilters() {
        Set<String> genders = masterData.stream()
            .map(HocSinh::getGioiTinh)
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        ObservableList<String> genderOptions = FXCollections.observableArrayList();
        genderOptions.add("Tất cả");
        genderOptions.addAll(genders);
        genderFilter.setItems(genderOptions);
        genderFilter.getSelectionModel().selectFirst();

        Set<String> enrollmentYears = masterData.stream()
            .map(HocSinh::getNgayNhapHoc)
            .filter(Objects::nonNull)
            .map(LocalDate::getYear)
            .map(String::valueOf)
            .collect(Collectors.toCollection(() -> new TreeSet<>(java.util.Collections.reverseOrder())));

        ObservableList<String> years = FXCollections.observableArrayList();
        years.add("Tất cả");
        years.addAll(enrollmentYears);
        enrollmentYearFilter.setItems(years);
        enrollmentYearFilter.getSelectionModel().selectFirst();
    }

    private void hookListeners() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        genderFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        enrollmentYearFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        filteredData.addListener((ListChangeListener<HocSinh>) change -> refreshSummary());
        refreshSummary();
    }

    private void applyFilters() {
        String searchKeyword = normalize(searchField.getText());
        String gender = genderFilter.getSelectionModel().getSelectedItem();
        String year = enrollmentYearFilter.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(student -> {
            if (!matchesSearch(student, searchKeyword)) {
                return false;
            }
            if (!matchesGender(student, gender)) {
                return false;
            }
            return matchesEnrollmentYear(student, year);
        });
    }

    private boolean matchesSearch(HocSinh student, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return student.getHoTenDayDu().toLowerCase(Locale.getDefault()).contains(keyword)
            || student.getMaHocSinh().toLowerCase(Locale.getDefault()).contains(keyword);
    }

    private boolean matchesGender(HocSinh student, String selection) {
        if (selection == null || "Tất cả".equalsIgnoreCase(selection)) {
            return true;
        }
        String gioiTinh = student.getGioiTinh();
        return gioiTinh != null && selection.equalsIgnoreCase(gioiTinh);
    }

    private boolean matchesEnrollmentYear(HocSinh student, String selection) {
        if (selection == null || "Tất cả".equals(selection)) {
            return true;
        }
        LocalDate enrollment = student.getNgayNhapHoc();
        return enrollment != null && selection.equals(String.valueOf(enrollment.getYear()));
    }

    private void refreshSummary() {
        resultsSummary.setText("Hiển thị " + filteredData.size() + " học viên");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.getDefault());
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : DATE_FORMATTER.format(date);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    @FXML
    private void handleAddStudent() {
        // Placeholder until dialog/workflow is implemented
        showMessage("Chức năng thêm học viên đang được phát triển");
    }

    @FXML
    private void handleImportCsv() {
        showMessage("Chức năng nhập CSV sẽ sớm có mặt");
    }

    @FXML
    private void handleExportList() {
        showMessage("Đang chuẩn bị xuất danh sách học viên");
    }

    @FXML
    private void handleResetFilters() {
        searchField.clear();
        genderFilter.getSelectionModel().selectFirst();
        enrollmentYearFilter.getSelectionModel().selectFirst();
        applyFilters();
        placeholderMessage.setVisible(false);
    }

    private void showMessage(String message) {
        placeholderMessage.setText(message);
        placeholderMessage.setVisible(true);
    }

    private Label createTablePlaceholder() {
        Label label = new Label("Không có học viên nào phù hợp");
        label.getStyleClass().add("error-text");
        return label;
    }
}

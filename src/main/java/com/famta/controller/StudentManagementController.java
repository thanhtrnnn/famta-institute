package com.famta.controller;

import com.famta.model.HocSinh;
import com.famta.service.HocVienService;
import com.famta.service.ServiceProvider;
import com.famta.util.SequentialIdGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentManagementController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String STUDENT_ID_PREFIX = "HS";
    private static final int STUDENT_ID_WIDTH = 8;

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
            hideMessage();
        } catch (Exception ex) {
            masterData.clear();
            showMessage("Không thể tải dữ liệu học viên. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", "error-message");
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
        Optional<StudentFormData> formData = showStudentDialog();
        formData.ifPresent(data -> {
            HocSinh hocSinh = new HocSinh(
                data.id(),
                data.lastName(),
                data.middleName(),
                data.firstName(),
                data.birthDate(),
                data.gender(),
                data.enrollmentDate()
            );
            try {
                boolean inserted = hocVienService.addHocVien(hocSinh);
                if (inserted) {
                    masterData.add(hocSinh);
                    applyFilters();
                    studentTable.getSelectionModel().select(hocSinh);
                    studentTable.scrollTo(hocSinh);
                    showMessage("Đã thêm học viên " + hocSinh.getHoTenDayDu(), "success-message");
                } else {
                    showMessage("Mã học viên " + hocSinh.getMaHocSinh() + " đã tồn tại.", "error-message");
                }
            } catch (Exception ex) {
                showMessage("Không thể thêm học viên: " + ex.getMessage(), "error-message");
            }
        });
    }

    @FXML
    private void handleImportCsv() {
        showMessage("Chức năng nhập CSV sẽ sớm có mặt", "warning-message");
    }

    @FXML
    private void handleExportList() {
        showMessage("Đang chuẩn bị xuất danh sách học viên", "info-text");
    }

    @FXML
    private void handleResetFilters() {
        searchField.clear();
        genderFilter.getSelectionModel().selectFirst();
        enrollmentYearFilter.getSelectionModel().selectFirst();
        applyFilters();
        hideMessage();
    }

    private void showMessage(String message, String styleClass) {
        placeholderMessage.getStyleClass().removeAll("info-text", "success-message", "error-message", "warning-message");
        placeholderMessage.getStyleClass().add(styleClass == null ? "info-text" : styleClass);
        placeholderMessage.setText(message);
        placeholderMessage.setManaged(true);
        placeholderMessage.setVisible(true);
    }

    private void hideMessage() {
        placeholderMessage.setText("");
        placeholderMessage.setManaged(false);
        placeholderMessage.setVisible(false);
    }

    private Optional<StudentFormData> showStudentDialog() {
        Dialog<StudentFormData> dialog = new Dialog<>();
        dialog.setTitle("Thêm học viên");
        dialog.setHeaderText("Điền thông tin học viên mới");
        if (studentTable.getScene() != null) {
            dialog.initOwner(studentTable.getScene().getWindow());
        }

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, saveButtonType);

        TextField idField = new TextField(suggestStudentId());
        idField.setPromptText("HS00000001");
        TextField lastNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField firstNameField = new TextField();
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        genderBox.setEditable(false);
        genderBox.setPromptText("-- Giới tính --");
        DatePicker birthPicker = new DatePicker();
        DatePicker enrollPicker = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Mã học viên"), idField);
        grid.addRow(1, new Label("Họ"), lastNameField);
        grid.addRow(2, new Label("Tên lót"), middleNameField);
        grid.addRow(3, new Label("Tên"), firstNameField);
        grid.addRow(4, new Label("Giới tính"), genderBox);
        grid.addRow(5, new Label("Ngày sinh"), birthPicker);
        grid.addRow(6, new Label("Ngày nhập học"), enrollPicker);
        GridPane.setHgrow(idField, Priority.ALWAYS);
        GridPane.setHgrow(lastNameField, Priority.ALWAYS);
        GridPane.setHgrow(middleNameField, Priority.ALWAYS);
        GridPane.setHgrow(firstNameField, Priority.ALWAYS);
        GridPane.setHgrow(genderBox, Priority.ALWAYS);
        GridPane.setHgrow(birthPicker, Priority.ALWAYS);
        GridPane.setHgrow(enrollPicker, Priority.ALWAYS);

        Label validationLabel = new Label();
        validationLabel.getStyleClass().add("error-message");
        validationLabel.setWrapText(true);
        validationLabel.setManaged(false);
        validationLabel.setVisible(false);

        VBox container = new VBox(12, grid, validationLabel);
        dialog.getDialogPane().setContent(container);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            String error = validateStudentForm(
                idField.getText(),
                lastNameField.getText(),
                firstNameField.getText(),
                birthPicker.getValue(),
                enrollPicker.getValue()
            );
            if (error != null) {
                validationLabel.setText(error);
                validationLabel.setManaged(true);
                validationLabel.setVisible(true);
                event.consume();
            } else {
                validationLabel.setManaged(false);
                validationLabel.setVisible(false);
            }
        });

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String gender = genderBox.getSelectionModel().isEmpty() ? null : genderBox.getSelectionModel().getSelectedItem();
                return new StudentFormData(
                    sanitizeId(idField.getText()),
                    trimNonNull(lastNameField.getText()),
                    trimNullable(middleNameField.getText()),
                    trimNonNull(firstNameField.getText()),
                    birthPicker.getValue(),
                    enrollPicker.getValue(),
                    trimNullable(gender)
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private String validateStudentForm(String id, String lastName, String firstName, LocalDate birthDate, LocalDate enrollDate) {
        if (id == null || id.isBlank()) {
            return "Vui lòng nhập mã học viên";
        }
        if (id.trim().length() != STUDENT_ID_PREFIX.length() + STUDENT_ID_WIDTH) {
            return "Mã học viên phải có dạng " + STUDENT_ID_PREFIX + " + " + STUDENT_ID_WIDTH + " chữ số";
        }
        if (lastName == null || lastName.isBlank()) {
            return "Vui lòng nhập họ";
        }
        if (firstName == null || firstName.isBlank()) {
            return "Vui lòng nhập tên";
        }
        if (birthDate == null) {
            return "Vui lòng chọn ngày sinh";
        }
        if (enrollDate == null) {
            return "Vui lòng chọn ngày nhập học";
        }
        if (enrollDate.isBefore(birthDate)) {
            return "Ngày nhập học phải sau ngày sinh";
        }
        return null;
    }

    private String trimNonNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? "" : trimmed;
    }

    private String trimNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String suggestStudentId() {
        try {
            return SequentialIdGenerator.nextId("HOCSINH", "MaHocSinh", STUDENT_ID_PREFIX, STUDENT_ID_WIDTH);
        } catch (Exception ex) {
            System.err.println("Không thể sinh mã học viên: " + ex.getMessage());
            return "";
        }
    }

    private String sanitizeId(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase(Locale.getDefault());
    }

    private Label createTablePlaceholder() {
        Label label = new Label("Không có học viên nào phù hợp");
        label.getStyleClass().add("error-text");
        return label;
    }

    private record StudentFormData(
        String id,
        String lastName,
        String middleName,
        String firstName,
        LocalDate birthDate,
        LocalDate enrollmentDate,
        String gender
    ) {
    }
}

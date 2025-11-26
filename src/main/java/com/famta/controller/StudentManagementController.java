package com.famta.controller;

import com.famta.model.HocSinh;
import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import com.famta.service.HocVienService;
import com.famta.service.JdbcClassService;
import com.famta.service.JdbcGuardianService;
import com.famta.service.JdbcHocVienService;
import com.famta.service.ServiceProvider;
import com.famta.service.dto.ClassSummary;
import com.famta.session.UserSession;
import com.famta.util.SequentialIdGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import javafx.util.StringConverter;

public class StudentManagementController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String STUDENT_ID_PREFIX = "HS";
    private static final int STUDENT_ID_WIDTH = 8;

    private final HocVienService hocVienService = ServiceProvider.getHocVienService();
    private final JdbcClassService classService = new JdbcClassService();
    private final JdbcGuardianService guardianService = new JdbcGuardianService();

    private final ObservableList<HocSinh> masterData = FXCollections.observableArrayList();
    private FilteredList<HocSinh> filteredData;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<ClassSummary> classFilter;

    @FXML
    private ComboBox<String> genderFilter;

    @FXML
    private ComboBox<String> enrollmentYearFilter;

    @FXML
    private Button addStudentButton;
    
    @FXML
    private Button deleteStudentButton;

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
    private Label formTitle;

    @FXML
    private Label formStatus;

    @FXML
    private TextField maHocSinhField;

    @FXML
    private TextField hoField;

    @FXML
    private TextField tenLotField;

    @FXML
    private TextField tenField;

    @FXML
    private ComboBox<String> gioiTinhBox;

    @FXML
    private DatePicker ngaySinhPicker;

    @FXML
    private DatePicker ngayNhapHocPicker;

    @FXML
    private void initialize() {
        configureTable();
        configureForm();
        loadData();
        configureFilters();
        hookListeners();
        placeholderMessage.setVisible(false);
        applyFilters();
        applyAccessControl();
    }

    private void applyAccessControl() {
        Optional<TaiKhoan> currentUser = UserSession.getCurrentAccount();
        if (currentUser.isEmpty()) return;

        QuyenTruyCap role = currentUser.get().getQuyen();
        
        // Disable editing for non-admins
        if (role != QuyenTruyCap.ADMIN) {
            addStudentButton.setVisible(false);
            addStudentButton.setManaged(false);
            deleteStudentButton.setVisible(false);
            deleteStudentButton.setManaged(false);
            // Also disable the form fields if they are visible
            maHocSinhField.setDisable(true);
            hoField.setDisable(true);
            tenLotField.setDisable(true);
            tenField.setDisable(true);
            ngaySinhPicker.setDisable(true);
            ngayNhapHocPicker.setDisable(true);
            gioiTinhBox.setDisable(true);
            // Hide save/cancel buttons if they exist in the FXML (not shown in snippet but good practice)
        }
    }

    private void configureForm() {
        gioiTinhBox.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        clearForm();
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void clearForm() {
        maHocSinhField.clear();
        hoField.clear();
        tenLotField.clear();
        tenField.clear();
        gioiTinhBox.setValue(null);
        ngaySinhPicker.setValue(null);
        ngayNhapHocPicker.setValue(LocalDate.now());
        formTitle.setText("Thêm học sinh mới");
        formStatus.setText("");
        studentTable.getSelectionModel().clearSelection();
    }

    private void populateForm(HocSinh hs) {
        maHocSinhField.setText(hs.getMaHocSinh());
        hoField.setText(hs.getHo());
        tenLotField.setText(hs.getTenLot());
        tenField.setText(hs.getTen());
        gioiTinhBox.setValue(hs.getGioiTinh()); // Assuming getter exists or I need to check model
        // HocSinh model has getGioiTinh() returning String? Yes.
        // But wait, HocSinh.java showed `private String gioiTinh;`
        // Let's check getters in HocSinh.java again.
        // It has `getGioiTinh()`.
        // What about dates? `getNgaySinh()` returns LocalDate.
        ngaySinhPicker.setValue(hs.getNgaySinh());
        ngayNhapHocPicker.setValue(hs.getNgayNhapHoc());
        formTitle.setText("Cập nhật thông tin");
        formStatus.setText("");
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
            showMessage("Không thể tải dữ liệu học sinh. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", "error-message");
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
        
        // Class Filter
        try {
            List<ClassSummary> classes = classService.fetchClassSummaries();
            classFilter.setItems(FXCollections.observableArrayList(classes));
            classFilter.setConverter(new StringConverter<ClassSummary>() {
                @Override
                public String toString(ClassSummary object) {
                    return object == null ? "Tất cả" : object.tenLopHoc();
                }

                @Override
                public ClassSummary fromString(String string) {
                    return null;
                }
            });
            classFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadStudentsByClass(newVal.maLopHoc());
                } else {
                    loadData(); // Reload all if cleared
                    applyFilters();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadStudentsByClass(String maLopHoc) {
        if (hocVienService instanceof JdbcHocVienService) {
            try {
                List<HocSinh> students = ((JdbcHocVienService) hocVienService).getHocVienByClass(maLopHoc);
                masterData.setAll(students);
                // Re-apply other filters locally
                applyFilters();
                refreshSummary();
            } catch (Exception ex) {
                showMessage("Lỗi tải danh sách lớp: " + ex.getMessage(), "error-message");
            }
        }
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
        resultsSummary.setText("Hiển thị " + filteredData.size() + " học sinh");
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
        clearForm();
        maHocSinhField.setText(suggestStudentId());
        hoField.requestFocus();
    }

    @FXML
    private void handleEditStudent() {
        HocSinh selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        } else {
            showMessage("Vui lòng chọn học sinh để sửa", "warning-message");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        HocSinh selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Vui lòng chọn học sinh để xóa", "warning-message");
            return;
        }
        try {
            if (hocVienService.deleteHocVien(selected.getMaHocSinh())) {
                masterData.remove(selected);
                clearForm();
                showMessage("Đã xóa học sinh " + selected.getHo() + " " + selected.getTen(), "success-message");
            } else {
                showMessage("Không thể xóa học sinh từ CSDL", "error-message");
            }
        } catch (Exception ex) {
            showMessage("Không thể xóa: " + ex.getMessage(), "error-message");
        }
    }

    @FXML
    private void handleSaveStudent() {
        String id = maHocSinhField.getText();
        String ho = hoField.getText();
        String tenLot = tenLotField.getText();
        String ten = tenField.getText();
        String gioiTinh = gioiTinhBox.getValue();
        LocalDate ngaySinh = ngaySinhPicker.getValue();
        LocalDate ngayNhapHoc = ngayNhapHocPicker.getValue();

        if (id == null || id.isBlank()) {
            formStatus.setText("Thiếu mã học sinh");
            return;
        }
        if (ho == null || ho.isBlank() || ten == null || ten.isBlank()) {
            formStatus.setText("Vui lòng nhập họ và tên");
            return;
        }

        try {
            HocSinh hs = new HocSinh(id, ho, tenLot, ten, ngaySinh, gioiTinh, ngayNhapHoc);
            Optional<HocSinh> existing = masterData.stream().filter(h -> h.getMaHocSinh().equals(id)).findFirst();
            if (existing.isPresent()) {
                if (hocVienService.updateHocVien(hs)) {
                    int index = masterData.indexOf(existing.get());
                    masterData.set(index, hs);
                    showMessage("Đã cập nhật học sinh", "success-message");
                } else {
                    formStatus.setText("Lỗi: Không thể cập nhật vào CSDL");
                    return;
                }
            } else {
                if (hocVienService.addHocVien(hs)) {
                    masterData.add(hs);
                    showMessage("Đã thêm học sinh mới", "success-message");
                } else {
                    formStatus.setText("Lỗi: Không thể thêm vào CSDL");
                    return;
                }
            }
            clearForm();
        } catch (Exception ex) {
            formStatus.setText("Lỗi: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        clearForm();
    }

    @FXML
    private void handleResetFilters() {
        searchField.clear();
        classFilter.getSelectionModel().clearSelection();
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
        dialog.setTitle("Thêm học sinh");
        dialog.setHeaderText("Điền thông tin học sinh mới");
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
        grid.addRow(0, new Label("Mã học sinh"), idField);
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
            return "Vui lòng nhập mã học sinh";
        }
        if (id.trim().length() != STUDENT_ID_PREFIX.length() + STUDENT_ID_WIDTH) {
            return "Mã học sinh phải có dạng " + STUDENT_ID_PREFIX + " + " + STUDENT_ID_WIDTH + " chữ số";
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
            System.err.println("Không thể sinh mã học sinh: " + ex.getMessage());
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
        Label label = new Label("Không có học sinh nào phù hợp");
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

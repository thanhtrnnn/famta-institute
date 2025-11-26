package com.famta.controller;

import com.famta.model.GiaoVien;
import com.famta.service.JdbcTeacherService;
import com.famta.service.TeacherService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<GiaoVien> teacherTable;

    @FXML
    private TableColumn<GiaoVien, String> colId;

    @FXML
    private TableColumn<GiaoVien, String> colFullName;

    @FXML
    private TableColumn<GiaoVien, String> colEmail;

    @FXML
    private TableColumn<GiaoVien, String> colPhone;

    @FXML private Label resultsSummary;
    @FXML private Label formTitle;
    @FXML private Label formStatus;
    @FXML private TextField maGiaoVienField;
    @FXML private TextField hoField;
    @FXML private TextField tenLotField;
    @FXML private TextField tenField;
    @FXML private TextField emailField;
    @FXML private TextField sdtField;

    private final TeacherService teacherService = new JdbcTeacherService();
    private final ObservableList<GiaoVien> teachers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getMaGiaoVien())));
        colFullName.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getHoTenDayDu())));
        colEmail.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getDiaChiEmail())));
        colPhone.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getSdt())));

        teacherTable.setItems(teachers);

        searchField.textProperty().addListener((obs, old, neu) -> filter(neu));
        
        teacherTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        clearForm();
        // load initially on FX thread
        Platform.runLater(this::loadTeachers);
    }

    private void clearForm() {
        maGiaoVienField.clear();
        hoField.clear();
        tenLotField.clear();
        tenField.clear();
        emailField.clear();
        sdtField.clear();
        formTitle.setText("Thêm giáo viên mới");
        formStatus.setText("");
        teacherTable.getSelectionModel().clearSelection();
    }

    private void populateForm(GiaoVien gv) {
        maGiaoVienField.setText(gv.getMaGiaoVien());
        hoField.setText(gv.getHo());
        tenLotField.setText(gv.getTenLot());
        tenField.setText(gv.getTen());
        emailField.setText(gv.getDiaChiEmail());
        sdtField.setText(gv.getSdt());
        formTitle.setText("Cập nhật thông tin");
        formStatus.setText("");
    }

    private void filter(String text) {
        if (text == null || text.isBlank()) {
            teacherTable.setItems(teachers);
            resultsSummary.setText("Hiển thị " + teachers.size() + " giáo viên");
            return;
        }
        String q = text.trim().toLowerCase();
        List<GiaoVien> filtered = teachers.stream()
                .filter(g -> (g.getMaGiaoVien() != null && g.getMaGiaoVien().toLowerCase().contains(q))
                        || (g.getHoTenDayDu() != null && g.getHoTenDayDu().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        teacherTable.setItems(FXCollections.observableArrayList(filtered));
        resultsSummary.setText("Tìm thấy " + filtered.size() + " kết quả");
    }

    private void loadTeachers() {
        try {
            List<GiaoVien> list = teacherService.findAll();
            teachers.setAll(list);
            resultsSummary.setText("Hiển thị " + list.size() + " giáo viên");
        } catch (Exception e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadTeachers();
    }

    @FXML
    private void handleAdd() {
        clearForm();
        maGiaoVienField.setText("GV" + System.currentTimeMillis() % 100000);
        hoField.requestFocus();
    }

    @FXML
    private void handleEdit() {
        GiaoVien selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chưa chọn giáo viên", "Vui lòng chọn một giáo viên để sửa.");
            return;
        }
        populateForm(selected);
    }

    @FXML
    private void handleCancelEdit() {
        clearForm();
    }

    @FXML
    private void handleSave() {
        String ma = maGiaoVienField.getText();
        String ho = hoField.getText();
        String tenLot = tenLotField.getText();
        String ten = tenField.getText();
        String email = emailField.getText();
        String sdt = sdtField.getText();

        if (ma == null || ma.isBlank()) {
            formStatus.setText("Thiếu mã giáo viên");
            return;
        }
        if (ho == null || ho.isBlank() || ten == null || ten.isBlank()) {
            formStatus.setText("Vui lòng nhập họ và tên");
            return;
        }

        try {
            // Assuming gender is not in form yet, passing null or default
            GiaoVien gv = new GiaoVien(ma, ho, tenLot, ten, null, email, sdt);
            
            boolean isUpdate = false;
            for (GiaoVien item : teachers) {
                if (item.getMaGiaoVien().equals(ma)) {
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                if (teacherService.updateTeacher(gv)) {
                    formStatus.setText("Đã cập nhật giáo viên");
                    loadTeachers();
                } else {
                    formStatus.setText("Lỗi: Không thể cập nhật vào CSDL");
                }
            } else {
                if (teacherService.addTeacher(gv)) {
                    formStatus.setText("Đã thêm giáo viên mới");
                    loadTeachers();
                } else {
                    formStatus.setText("Lỗi: Không thể thêm vào CSDL");
                }
            }
            clearForm();
        } catch (Exception e) {
            formStatus.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        GiaoVien selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chưa chọn giáo viên", "Vui lòng chọn một giáo viên để xóa.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa giáo viên: " + selected.getHoTenDayDu());
        alert.setContentText("Bạn có chắc chắn muốn xóa giáo viên này không?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (teacherService.deleteTeacher(selected.getMaGiaoVien())) {
                    loadTeachers();
                    clearForm();
                } else {
                    showAlert("Lỗi", "Không thể xóa giáo viên.");
                }
            } catch (Exception e) {
                showAlert("Lỗi xóa dữ liệu", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

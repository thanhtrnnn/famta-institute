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

        // load initially on FX thread
        Platform.runLater(this::loadTeachers);
    }

    private void filter(String text) {
        if (text == null || text.isBlank()) {
            teacherTable.setItems(teachers);
            return;
        }
        String q = text.trim().toLowerCase();
        List<GiaoVien> filtered = teachers.stream()
                .filter(g -> (g.getMaGiaoVien() != null && g.getMaGiaoVien().toLowerCase().contains(q))
                        || (g.getHoTenDayDu() != null && g.getHoTenDayDu().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        teacherTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleRefresh() {
        loadTeachers();
    }

    @FXML
    private void handleAdd() {
        showTeacherDialog(null);
    }

    @FXML
    private void handleEdit() {
        GiaoVien selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chưa chọn giáo viên", "Vui lòng chọn một giáo viên để sửa.");
            return;
        }
        showTeacherDialog(selected);
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
            if (teacherService.deleteTeacher(selected.getMaGiaoVien())) {
                loadTeachers();
            } else {
                showAlert("Lỗi", "Không thể xóa giáo viên.");
            }
        }
    }

    private void showTeacherDialog(GiaoVien teacher) {
        Dialog<GiaoVien> dialog = new Dialog<>();
        dialog.setTitle(teacher == null ? "Thêm giáo viên" : "Sửa giáo viên");
        dialog.setHeaderText(teacher == null ? "Nhập thông tin giáo viên mới" : "Cập nhật thông tin giáo viên");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField maField = new TextField();
        maField.setPromptText("Mã giáo viên");
        TextField hoField = new TextField();
        hoField.setPromptText("Họ");
        TextField tenLotField = new TextField();
        tenLotField.setPromptText("Tên lót");
        TextField tenField = new TextField();
        tenField.setPromptText("Tên");
        TextField gioiTinhField = new TextField();
        gioiTinhField.setPromptText("Giới tính");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField sdtField = new TextField();
        sdtField.setPromptText("Số điện thoại");

        if (teacher != null) {
            maField.setText(teacher.getMaGiaoVien());
            maField.setEditable(false); // Cannot change ID
            hoField.setText(teacher.getHo());
            tenLotField.setText(teacher.getTenLot());
            tenField.setText(teacher.getTen());
            gioiTinhField.setText(teacher.getGioiTinh());
            emailField.setText(teacher.getDiaChiEmail());
            sdtField.setText(teacher.getSdt());
        }

        grid.add(new Label("Mã GV:"), 0, 0);
        grid.add(maField, 1, 0);
        grid.add(new Label("Họ:"), 0, 1);
        grid.add(hoField, 1, 1);
        grid.add(new Label("Tên lót:"), 0, 2);
        grid.add(tenLotField, 1, 2);
        grid.add(new Label("Tên:"), 0, 3);
        grid.add(tenField, 1, 3);
        grid.add(new Label("Giới tính:"), 0, 4);
        grid.add(gioiTinhField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(new Label("SĐT:"), 0, 6);
        grid.add(sdtField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(maField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new GiaoVien(
                    maField.getText(),
                    hoField.getText(),
                    tenLotField.getText(),
                    tenField.getText(),
                    gioiTinhField.getText(),
                    emailField.getText(),
                    sdtField.getText()
                );
            }
            return null;
        });

        Optional<GiaoVien> result = dialog.showAndWait();

        result.ifPresent(newTeacher -> {
            try {
                if (teacher == null) {
                    if (teacherService.addTeacher(newTeacher)) {
                        loadTeachers();
                    } else {
                        showAlert("Lỗi", "Không thể thêm giáo viên (có thể mã đã tồn tại).");
                    }
                } else {
                    if (teacherService.updateTeacher(newTeacher)) {
                        loadTeachers();
                    } else {
                        showAlert("Lỗi", "Không thể cập nhật giáo viên.");
                    }
                }
            } catch (Exception e) {
                showAlert("Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadTeachers() {
        List<GiaoVien> list = teacherService.findAll();
        System.out.println("TeacherManagementController loaded " + list.size() + " teachers from database.");
        teachers.setAll(list);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

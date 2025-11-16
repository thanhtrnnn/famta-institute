package com.famta.controller;

import com.famta.model.NguoiGiamHo;
import com.famta.service.JdbcGuardianService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GuardianManagementController {

    @FXML private TextField searchField;
    @FXML private TableView<NguoiGiamHo> table;
    @FXML private TableColumn<NguoiGiamHo, String> colMa;
    @FXML private TableColumn<NguoiGiamHo, String> colHo;
    @FXML private TableColumn<NguoiGiamHo, String> colTenLot;
    @FXML private TableColumn<NguoiGiamHo, String> colTen;
    @FXML private TableColumn<NguoiGiamHo, String> colEmail;
    @FXML private TableColumn<NguoiGiamHo, String> colSdt;

    private final JdbcGuardianService service = new JdbcGuardianService();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNguoiGiamHo"));
        colHo.setCellValueFactory(new PropertyValueFactory<>("ho"));
        colTenLot.setCellValueFactory(new PropertyValueFactory<>("tenLot"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("diaChiEmail"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));

        loadData();
    }

    @FXML
    public void loadData() {
        try {
            List<NguoiGiamHo> list = service.getAllGuardians();
            table.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadData();
            return;
        }
        try {
            List<NguoiGiamHo> list = service.searchGuardians(keyword.trim());
            table.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tìm kiếm", e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        showDialog(null);
    }

    @FXML
    public void handleEdit() {
        NguoiGiamHo selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDialog(selected);
        } else {
            showWarning("Chưa chọn người giám hộ để sửa");
        }
    }

    @FXML
    public void handleDelete() {
        NguoiGiamHo selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa người giám hộ " + selected.getTen() + "?")) {
                try {
                    service.deleteGuardian(selected.getMaNguoiGiamHo());
                    loadData();
                } catch (SQLException e) {
                    showError("Lỗi xóa dữ liệu", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn người giám hộ để xóa");
        }
    }

    private void showDialog(NguoiGiamHo ngh) {
        Dialog<NguoiGiamHo> dialog = new Dialog<>();
        dialog.setTitle(ngh == null ? "Thêm Người Giám Hộ" : "Sửa Người Giám Hộ");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtHo = new TextField();
        TextField txtTenLot = new TextField();
        TextField txtTen = new TextField();
        TextField txtEmail = new TextField();
        TextField txtSdt = new TextField();

        if (ngh != null) {
            txtMa.setText(ngh.getMaNguoiGiamHo());
            txtMa.setDisable(true);
            txtHo.setText(ngh.getHo());
            txtTenLot.setText(ngh.getTenLot());
            txtTen.setText(ngh.getTen());
            txtEmail.setText(ngh.getDiaChiEmail());
            txtSdt.setText(ngh.getSdt());
        }

        grid.add(new Label("Mã NGH:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Họ:"), 0, 1);
        grid.add(txtHo, 1, 1);
        grid.add(new Label("Tên Lót:"), 0, 2);
        grid.add(txtTenLot, 1, 2);
        grid.add(new Label("Tên:"), 0, 3);
        grid.add(txtTen, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(txtEmail, 1, 4);
        grid.add(new Label("SĐT:"), 0, 5);
        grid.add(txtSdt, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (ngh != null) {
                    ngh.setHo(txtHo.getText());
                    ngh.setTenLot(txtTenLot.getText());
                    ngh.setTen(txtTen.getText());
                    ngh.setDiaChiEmail(txtEmail.getText());
                    ngh.setSdt(txtSdt.getText());
                    return ngh;
                } else {
                    return new NguoiGiamHo(
                        txtMa.getText(),
                        txtHo.getText(),
                        txtTenLot.getText(),
                        txtTen.getText(),
                        txtEmail.getText(),
                        txtSdt.getText()
                    );
                }
            }
            return null;
        });

        Optional<NguoiGiamHo> result = dialog.showAndWait();
        result.ifPresent(n -> {
            try {
                if (ngh == null) {
                    service.createGuardian(n);
                } else {
                    service.updateGuardian(n);
                }
                loadData();
            } catch (SQLException e) {
                showError("Lỗi lưu dữ liệu", e.getMessage());
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
